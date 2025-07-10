package com.fitnessapp.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Comprehensive error handling interceptor
 */
class ErrorHandlingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        return try {
            val response = chain.proceed(request)
            
            when (response.code) {
                in 200..299 -> {
                    // Success
                    response
                }
                400 -> {
                    Timber.w("Bad Request (400) for ${request.url}")
                    response
                }
                401 -> {
                    Timber.w("Unauthorized (401) for ${request.url} - Check API credentials")
                    response
                }
                403 -> {
                    Timber.w("Forbidden (403) for ${request.url} - Insufficient permissions")
                    response
                }
                404 -> {
                    Timber.w("Not Found (404) for ${request.url}")
                    response
                }
                429 -> {
                    Timber.w("Rate Limited (429) for ${request.url}")
                    response
                }
                in 500..599 -> {
                    Timber.e("Server Error (${response.code}) for ${request.url}")
                    response
                }
                else -> {
                    Timber.w("Unexpected response code (${response.code}) for ${request.url}")
                    response
                }
            }
        } catch (e: SocketTimeoutException) {
            Timber.e(e, "Request timeout for ${request.url}")
            throw IOException("Request timeout", e)
        } catch (e: UnknownHostException) {
            Timber.e(e, "Network unavailable for ${request.url}")
            throw IOException("Network unavailable", e)
        } catch (e: IOException) {
            Timber.e(e, "Network error for ${request.url}")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error for ${request.url}")
            throw IOException("Unexpected error", e)
        }
    }
}