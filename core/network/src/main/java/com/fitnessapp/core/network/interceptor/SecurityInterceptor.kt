package com.fitnessapp.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * Security interceptor for additional security headers and validation
 */
class SecurityInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add security headers
        val secureRequest = originalRequest.newBuilder()
            .addHeader("User-Agent", "FitnessApp/1.0 (Android)")
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Cache-Control", "no-cache")
            .build()
        
        val response = chain.proceed(secureRequest)
        
        // Validate response security
        validateResponse(response)
        
        return response
    }
    
    private fun validateResponse(response: Response) {
        try {
            // Check for security headers
            val contentType = response.header("Content-Type")
            if (contentType != null && !isValidContentType(contentType)) {
                Timber.w("Suspicious content type: $contentType")
            }
            
            // Check for HTTPS
            if (!response.request.isHttps) {
                Timber.w("Insecure HTTP request detected: ${response.request.url}")
            }
            
            // Check response size (prevent large response attacks)
            val contentLength = response.header("Content-Length")?.toLongOrNull()
            if (contentLength != null && contentLength > MAX_RESPONSE_SIZE) {
                Timber.w("Large response detected: $contentLength bytes")
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Error validating response security")
        }
    }
    
    private fun isValidContentType(contentType: String): Boolean {
        val validTypes = listOf(
            "application/json",
            "application/xml",
            "text/plain",
            "text/html",
            "image/jpeg",
            "image/png",
            "image/gif"
        )
        return validTypes.any { contentType.contains(it, ignoreCase = true) }
    }
    
    companion object {
        private const val MAX_RESPONSE_SIZE = 50 * 1024 * 1024 // 50MB
    }
}