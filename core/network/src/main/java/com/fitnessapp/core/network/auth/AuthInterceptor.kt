package com.fitnessapp.core.network.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * Interceptor to add authentication headers to API requests
 */
class AuthInterceptor @Inject constructor(
    private val apiKeyManager: ApiKeyManager,
    private val service: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        return try {
            val authenticatedRequest = when (service) {
                "USDA" -> {
                    val apiKey = runBlocking { apiKeyManager.getApiKey("USDA") }
                    if (apiKey != null) {
                        originalRequest.newBuilder()
                            .url(originalRequest.url.newBuilder()
                                .addQueryParameter("api_key", apiKey)
                                .build())
                            .build()
                    } else {
                        originalRequest
                    }
                }
                
                "FATSECRET" -> {
                    val token = runBlocking { apiKeyManager.getFatSecretOAuthToken() }
                    if (token != null) {
                        originalRequest.newBuilder()
                            .addHeader("Authorization", token)
                            .build()
                    } else {
                        originalRequest
                    }
                }
                
                "NUTRITIONIX" -> {
                    val appId = runBlocking { apiKeyManager.getApiKey("NUTRITIONIX_ID") }
                    val appKey = runBlocking { apiKeyManager.getApiKey("NUTRITIONIX_KEY") }
                    if (appId != null && appKey != null) {
                        originalRequest.newBuilder()
                            .addHeader("x-app-id", appId)
                            .addHeader("x-app-key", appKey)
                            .build()
                    } else {
                        originalRequest
                    }
                }
                
                "UPC" -> {
                    val apiKey = runBlocking { apiKeyManager.getApiKey("UPC") }
                    if (apiKey != null) {
                        originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer $apiKey")
                            .build()
                    } else {
                        originalRequest
                    }
                }
                
                "EXERCISE" -> {
                    val apiKey = runBlocking { apiKeyManager.getApiKey("EXERCISE") }
                    if (apiKey != null) {
                        originalRequest.newBuilder()
                            .addHeader("X-RapidAPI-Key", apiKey)
                            .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                            .build()
                    } else {
                        originalRequest
                    }
                }
                
                else -> originalRequest
            }
            
            chain.proceed(authenticatedRequest)
        } catch (e: Exception) {
            Timber.e(e, "Authentication failed for service: $service")
            chain.proceed(originalRequest)
        }
    }
}