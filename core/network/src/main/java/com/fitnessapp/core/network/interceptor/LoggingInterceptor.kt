package com.fitnessapp.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom logging interceptor for comprehensive debugging
 */
class LoggingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        Timber.d("🌐 API Request: ${request.method} ${request.url}")
        Timber.d("📋 Headers: ${request.headers}")
        
        if (request.body != null) {
            Timber.d("📦 Request Body Size: ${request.body!!.contentLength()} bytes")
        }
        
        val response = chain.proceed(request)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        val statusEmoji = when (response.code) {
            in 200..299 -> "✅"
            in 300..399 -> "🔄"
            in 400..499 -> "❌"
            in 500..599 -> "💥"
            else -> "❓"
        }
        
        Timber.d("$statusEmoji API Response: ${response.code} ${response.message} (${duration}ms)")
        Timber.d("📦 Response Size: ${response.body?.contentLength() ?: 0} bytes")
        Timber.d("📋 Response Headers: ${response.headers}")
        
        // Log rate limit info if available
        response.header("X-RateLimit-Remaining")?.let { remaining ->
            val limit = response.header("X-RateLimit-Limit") ?: "Unknown"
            val reset = response.header("X-RateLimit-Reset") ?: "Unknown"
            Timber.d("⏱️ Rate Limit: $remaining/$limit remaining, resets at $reset")
        }
        
        // Log cache information
        response.cacheResponse?.let {
            Timber.d("💾 Cache Hit for ${request.url}")
        } ?: response.networkResponse?.let {
            Timber.d("🌐 Network Response for ${request.url}")
        }
        
        return response
    }
}