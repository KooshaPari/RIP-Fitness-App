package com.fitnessapp.core.network.util

import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Network utilities for common operations
 */
object NetworkUtils {

    /**
     * Safe API call wrapper with error handling
     */
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error("Response body is null", response.code())
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Timber.e("API call failed: ${response.code()} - $errorMessage")
                ApiResult.Error(errorMessage, response.code())
            }
        } catch (e: IOException) {
            Timber.e(e, "Network error during API call")
            ApiResult.NetworkError(e.message ?: "Network error")
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during API call")
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Check if error is retryable
     */
    fun isRetryableError(error: ApiResult.Error): Boolean {
        return when (error.code) {
            408, 429, 500, 502, 503, 504 -> true
            else -> false
        }
    }

    /**
     * Get appropriate cache TTL based on data type
     */
    fun getCacheTtlMinutes(dataType: DataType): Int {
        return when (dataType) {
            DataType.FOOD_SEARCH -> 60 // 1 hour
            DataType.FOOD_DETAILS -> 24 * 60 // 24 hours
            DataType.EXERCISE_LIST -> 7 * 24 * 60 // 7 days
            DataType.BARCODE_LOOKUP -> 30 * 24 * 60 // 30 days
            DataType.USER_DATA -> 5 // 5 minutes
            DataType.STATIC_DATA -> 7 * 24 * 60 // 7 days
        }
    }

    /**
     * Build cache key for consistent caching
     */
    fun buildCacheKey(
        endpoint: String,
        params: Map<String, String> = emptyMap()
    ): String {
        val sortedParams = params.toSortedMap()
        val paramString = sortedParams.entries.joinToString("&") { "${it.key}=${it.value}" }
        return if (paramString.isNotEmpty()) {
            "$endpoint?$paramString"
        } else {
            endpoint
        }
    }

    /**
     * Validate API response data
     */
    fun <T> validateResponse(data: T?, requiredFields: List<String>): Boolean {
        if (data == null) return false
        
        // Use reflection to check required fields
        return try {
            val clazz = data::class.java
            requiredFields.all { fieldName ->
                val field = clazz.getDeclaredField(fieldName)
                field.isAccessible = true
                field.get(data) != null
            }
        } catch (e: Exception) {
            Timber.w("Field validation failed: ${e.message}")
            false
        }
    }

    /**
     * Rate limit information
     */
    data class RateLimitInfo(
        val remaining: Int,
        val limit: Int,
        val resetTime: Long
    )

    /**
     * Extract rate limit info from response headers
     */
    fun extractRateLimitInfo(response: Response<*>): RateLimitInfo? {
        return try {
            val remaining = response.headers()["X-RateLimit-Remaining"]?.toIntOrNull()
            val limit = response.headers()["X-RateLimit-Limit"]?.toIntOrNull()
            val reset = response.headers()["X-RateLimit-Reset"]?.toLongOrNull()
            
            if (remaining != null && limit != null && reset != null) {
                RateLimitInfo(remaining, limit, reset)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.w("Failed to extract rate limit info: ${e.message}")
            null
        }
    }
}

/**
 * Sealed class for API results
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data class NetworkError(val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

/**
 * Data types for cache TTL calculation
 */
enum class DataType {
    FOOD_SEARCH,
    FOOD_DETAILS,
    EXERCISE_LIST,
    BARCODE_LOOKUP,
    USER_DATA,
    STATIC_DATA
}

/**
 * Extension functions for ApiResult
 */
inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (String, Int?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(message, code)
    return this
}

inline fun <T> ApiResult<T>.onNetworkError(action: (String) -> Unit): ApiResult<T> {
    if (this is ApiResult.NetworkError) action(message)
    return this
}

/**
 * Map API result to different type
 */
inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> this
        is ApiResult.NetworkError -> this
        ApiResult.Loading -> ApiResult.Loading
    }
}