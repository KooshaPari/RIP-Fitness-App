package com.fitnessapp.core.network.auth

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * Rate limiting interceptor to respect API limits
 */
class RateLimitInterceptor @Inject constructor(
    private val maxRequests: Int,
    private val windowSize: Long,
    private val timeUnit: TimeUnit
) : Interceptor {

    private val requestCount = AtomicInteger(0)
    private val windowStart = AtomicLong(System.currentTimeMillis())
    private val windowSizeMillis = timeUnit.toMillis(windowSize)

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentTime = System.currentTimeMillis()
        
        synchronized(this) {
            // Reset window if time has passed
            if (currentTime - windowStart.get() >= windowSizeMillis) {
                windowStart.set(currentTime)
                requestCount.set(0)
                Timber.d("Rate limit window reset")
            }
            
            // Check if we're within limits
            if (requestCount.get() >= maxRequests) {
                val waitTime = windowSizeMillis - (currentTime - windowStart.get())
                if (waitTime > 0) {
                    Timber.w("Rate limit exceeded, waiting ${waitTime}ms")
                    try {
                        Thread.sleep(waitTime)
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Rate limit wait interrupted", e)
                    }
                    // Reset after waiting
                    windowStart.set(System.currentTimeMillis())
                    requestCount.set(0)
                }
            }
            
            requestCount.incrementAndGet()
        }
        
        val response = chain.proceed(chain.request())
        
        // Handle rate limit responses from server
        if (response.code == 429) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 60
            Timber.w("Server rate limit hit, retrying after ${retryAfter}s")
            
            try {
                Thread.sleep(retryAfter * 1000)
                response.close()
                return chain.proceed(chain.request())
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Rate limit retry interrupted", e)
            }
        }
        
        return response
    }
}