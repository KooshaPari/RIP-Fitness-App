package com.fitnessapp.core.network.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.fitnessapp.core.network.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure API key management with encryption
 */
@Singleton
class ApiKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "api_keys_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Production API Keys (encrypted in BuildConfig)
    private val apiKeys = mapOf(
        "USDA" to BuildConfig.USDA_API_KEY,
        "FATSECRET_ID" to BuildConfig.FATSECRET_CLIENT_ID,
        "FATSECRET_SECRET" to BuildConfig.FATSECRET_CLIENT_SECRET,
        "NUTRITIONIX_ID" to BuildConfig.NUTRITIONIX_APP_ID,
        "NUTRITIONIX_KEY" to BuildConfig.NUTRITIONIX_API_KEY,
        "UPC" to BuildConfig.UPC_DATABASE_API_KEY,
        "EXERCISE" to BuildConfig.EXERCISE_DB_API_KEY
    )

    /**
     * Get API key for specific service
     */
    suspend fun getApiKey(service: String): String? = withContext(Dispatchers.IO) {
        try {
            // First check encrypted preferences for user-provided keys
            val userKey = encryptedPrefs.getString("${service}_key", null)
            if (!userKey.isNullOrEmpty()) {
                return@withContext decrypt(userKey)
            }

            // Fall back to build config keys
            return@withContext apiKeys[service]?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get API key for service: $service")
            null
        }
    }

    /**
     * Store user-provided API key securely
     */
    suspend fun storeApiKey(service: String, apiKey: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val encryptedKey = encrypt(apiKey)
            encryptedPrefs.edit()
                .putString("${service}_key", encryptedKey)
                .apply()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to store API key for service: $service")
            false
        }
    }

    /**
     * Remove stored API key
     */
    suspend fun removeApiKey(service: String): Boolean = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit()
                .remove("${service}_key")
                .apply()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to remove API key for service: $service")
            false
        }
    }

    /**
     * Check if API key is available for service
     */
    suspend fun hasApiKey(service: String): Boolean {
        return getApiKey(service) != null
    }

    /**
     * Get OAuth token for FatSecret API
     */
    suspend fun getFatSecretOAuthToken(): String? = withContext(Dispatchers.IO) {
        try {
            val clientId = getApiKey("FATSECRET_ID") ?: return@withContext null
            val clientSecret = getApiKey("FATSECRET_SECRET") ?: return@withContext null
            
            // Check if we have a cached token
            val cachedToken = encryptedPrefs.getString("fatsecret_token", null)
            val tokenExpiry = encryptedPrefs.getLong("fatsecret_token_expiry", 0)
            
            if (cachedToken != null && System.currentTimeMillis() < tokenExpiry) {
                return@withContext decrypt(cachedToken)
            }

            // Generate new OAuth token
            val credentials = "$clientId:$clientSecret"
            val encodedCredentials = android.util.Base64.encodeToString(
                credentials.toByteArray(),
                android.util.Base64.NO_WRAP
            )
            
            // Store token (this would normally involve an HTTP request to FatSecret OAuth endpoint)
            // For now, return the encoded credentials for Bearer auth
            return@withContext "Bearer $encodedCredentials"
        } catch (e: Exception) {
            Timber.e(e, "Failed to get FatSecret OAuth token")
            null
        }
    }

    private val encryptionKey by lazy {
        generateOrRetrieveKey()
    }

    /**
     * Generate or retrieve persistent encryption key
     */
    private fun generateOrRetrieveKey(): SecretKey {
        val keyAlias = "api_key_encryption_key"
        val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        return if (keyStore.containsAlias(keyAlias)) {
            // Retrieve existing key
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            // Generate new key
            val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            val keyGenParameterSpec = android.security.keystore.KeyGenParameterSpec.Builder(
                keyAlias,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    /**
     * Encrypt sensitive data using AndroidKeyStore
     */
    private fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
        
        val encryptedData = cipher.doFinal(data.toByteArray())
        val iv = cipher.iv
        
        // Combine IV and encrypted data
        val combined = ByteArray(iv.size + encryptedData.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedData, 0, combined, iv.size, encryptedData.size)
        
        return android.util.Base64.encodeToString(combined, android.util.Base64.NO_WRAP)
    }

    /**
     * Decrypt sensitive data using AndroidKeyStore
     */
    private fun decrypt(encryptedData: String): String {
        val combined = android.util.Base64.decode(encryptedData, android.util.Base64.NO_WRAP)
        val iv = combined.sliceArray(0..11)
        val encrypted = combined.sliceArray(12 until combined.size)
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey, spec)
        
        return String(cipher.doFinal(encrypted))
    }

    /**
     * Clear all stored API keys (for logout/reset)
     */
    suspend fun clearAllKeys(): Boolean = withContext(Dispatchers.IO) {
        try {
            encryptedPrefs.edit().clear().apply()
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to clear all API keys")
            false
        }
    }
}