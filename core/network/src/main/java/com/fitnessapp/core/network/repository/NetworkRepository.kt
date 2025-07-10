package com.fitnessapp.core.network.repository

import com.fitnessapp.core.network.api.*
import com.fitnessapp.core.network.cache.NetworkCacheManager
import com.fitnessapp.core.network.model.ai.*
import com.fitnessapp.core.network.model.exercise.*
import com.fitnessapp.core.network.model.fatsecret.*
import com.fitnessapp.core.network.model.nutritionix.*
import com.fitnessapp.core.network.model.upc.*
import com.fitnessapp.core.network.model.usda.*
import com.fitnessapp.core.network.monitoring.NetworkMonitor
import com.fitnessapp.core.network.sync.OfflineSyncManager
import com.fitnessapp.core.network.util.ApiResult
import com.fitnessapp.core.network.util.DataType
import com.fitnessapp.core.network.util.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized repository for all network operations
 */
@Singleton
class NetworkRepository @Inject constructor(
    private val usdaApi: UsdaFoodDataApi,
    private val fatSecretApi: FatSecretApi,
    private val nutritionixApi: NutritionixApi,
    private val upcApi: UpcDatabaseApi,
    private val exerciseApi: ExerciseApi,
    private val foodRecognitionApi: FoodRecognitionApi,
    private val cacheManager: NetworkCacheManager,
    private val networkMonitor: NetworkMonitor,
    private val offlineSyncManager: OfflineSyncManager,
    private val json: Json
) {

    // USDA Food Data Central operations
    suspend fun searchFoods(
        query: String,
        pageSize: Int = 25,
        pageNumber: Int = 1
    ): ApiResult<UsdaFoodSearchResponse> {
        return NetworkUtils.safeApiCall {
            usdaApi.searchFoods(
                query = query,
                pageSize = pageSize,
                pageNumber = pageNumber,
                apiKey = "" // Handled by interceptor
            )
        }
    }

    suspend fun getFoodDetails(fdcId: String): ApiResult<UsdaFoodDetails> {
        val cacheKey = NetworkUtils.buildCacheKey("usda/food/$fdcId")
        
        // Check cache first
        val cachedData = cacheManager.getCachedResponse(cacheKey)
        if (cachedData != null) {
            return try {
                val data = json.decodeFromString<UsdaFoodDetails>(cachedData)
                ApiResult.Success(data)
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode cached food details")
                fetchFoodDetailsFromApi(fdcId, cacheKey)
            }
        }

        return fetchFoodDetailsFromApi(fdcId, cacheKey)
    }

    private suspend fun fetchFoodDetailsFromApi(
        fdcId: String,
        cacheKey: String
    ): ApiResult<UsdaFoodDetails> {
        return NetworkUtils.safeApiCall {
            usdaApi.getFoodDetails(fdcId, apiKey = "")
        }.also { result ->
            if (result is ApiResult.Success) {
                val jsonData = json.encodeToString(UsdaFoodDetails.serializer(), result.data)
                cacheManager.cacheResponse(
                    cacheKey,
                    jsonData,
                    NetworkUtils.getCacheTtlMinutes(DataType.FOOD_DETAILS)
                )
            }
        }
    }

    // FatSecret operations
    suspend fun searchFoodsWithFatSecret(
        searchExpression: String,
        maxResults: Int = 20
    ): ApiResult<FatSecretFoodSearchResponse> {
        return NetworkUtils.safeApiCall {
            fatSecretApi.searchFoods(searchExpression = searchExpression, maxResults = maxResults)
        }
    }

    suspend fun getFatSecretFoodDetails(foodId: String): ApiResult<FatSecretFoodDetails> {
        return NetworkUtils.safeApiCall {
            fatSecretApi.getFoodDetails(foodId = foodId)
        }
    }

    suspend fun lookupFoodByBarcode(barcode: String): ApiResult<FatSecretBarcodeResponse> {
        return NetworkUtils.safeApiCall {
            fatSecretApi.getFoodByBarcode(barcode = barcode)
        }
    }

    // Nutritionix operations
    suspend fun parseNaturalFood(
        query: String,
        numServings: Int? = null
    ): ApiResult<NutritionixNaturalResponse> {
        val request = NutritionixNaturalRequest(
            query = query,
            numServings = numServings
        )
        
        return NetworkUtils.safeApiCall {
            nutritionixApi.parseNaturalFood(request, "", "")
        }
    }

    suspend fun searchFoodsInstant(query: String): ApiResult<NutritionixInstantSearchResponse> {
        return NetworkUtils.safeApiCall {
            nutritionixApi.instantSearch(query, appId = "", appKey = "")
        }
    }

    suspend fun analyzePhoto(imageUris: List<String>): ApiResult<NutritionixPhotoResponse> {
        val request = NutritionixPhotoRequest(mediaUris = imageUris)
        return NetworkUtils.safeApiCall {
            nutritionixApi.analyzePhoto(request, "", "")
        }
    }

    // UPC Database operations
    suspend fun lookupProductByBarcode(upc: String): ApiResult<UpcLookupResponse> {
        val cacheKey = NetworkUtils.buildCacheKey("upc/lookup/$upc")
        
        val cachedData = cacheManager.getCachedResponse(cacheKey)
        if (cachedData != null) {
            return try {
                val data = json.decodeFromString<UpcLookupResponse>(cachedData)
                ApiResult.Success(data)
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode cached UPC data")
                fetchUpcFromApi(upc, cacheKey)
            }
        }

        return fetchUpcFromApi(upc, cacheKey)
    }

    private suspend fun fetchUpcFromApi(upc: String, cacheKey: String): ApiResult<UpcLookupResponse> {
        return NetworkUtils.safeApiCall {
            upcApi.lookupBarcode(upc, "")
        }.also { result ->
            if (result is ApiResult.Success) {
                val jsonData = json.encodeToString(UpcLookupResponse.serializer(), result.data)
                cacheManager.cacheResponse(
                    cacheKey,
                    jsonData,
                    NetworkUtils.getCacheTtlMinutes(DataType.BARCODE_LOOKUP)
                )
            }
        }
    }

    // Exercise Database operations
    suspend fun getAllExercises(
        limit: Int = 20,
        offset: Int = 0
    ): ApiResult<List<Exercise>> {
        return NetworkUtils.safeApiCall {
            exerciseApi.getAllExercises(limit, offset, "", "")
        }
    }

    suspend fun getExercisesByBodyPart(
        bodyPart: String,
        limit: Int = 20
    ): ApiResult<List<Exercise>> {
        val cacheKey = NetworkUtils.buildCacheKey(
            "exercise/bodypart/$bodyPart",
            mapOf("limit" to limit.toString())
        )

        val cachedData = cacheManager.getCachedResponse(cacheKey)
        if (cachedData != null) {
            return try {
                val data = json.decodeFromString<List<Exercise>>(cachedData)
                ApiResult.Success(data)
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode cached exercise data")
                fetchExercisesByBodyPartFromApi(bodyPart, limit, cacheKey)
            }
        }

        return fetchExercisesByBodyPartFromApi(bodyPart, limit, cacheKey)
    }

    private suspend fun fetchExercisesByBodyPartFromApi(
        bodyPart: String,
        limit: Int,
        cacheKey: String
    ): ApiResult<List<Exercise>> {
        return NetworkUtils.safeApiCall {
            exerciseApi.getExercisesByBodyPart(bodyPart, limit, 0, "", "")
        }.also { result ->
            if (result is ApiResult.Success) {
                val jsonData = json.encodeToString(result.data)
                cacheManager.cacheResponse(
                    cacheKey,
                    jsonData,
                    NetworkUtils.getCacheTtlMinutes(DataType.EXERCISE_LIST)
                )
            }
        }
    }

    suspend fun searchExercisesByName(name: String): ApiResult<List<Exercise>> {
        return NetworkUtils.safeApiCall {
            exerciseApi.searchExercisesByName(name, 20, 0, "", "")
        }
    }

    // AI Food Recognition operations
    suspend fun recognizeFood(
        image: MultipartBody.Part,
        confidenceThreshold: Float = 0.7f
    ): ApiResult<FoodRecognitionResponse> {
        return NetworkUtils.safeApiCall {
            foodRecognitionApi.recognizeFood(image, confidenceThreshold, 5, "")
        }
    }

    suspend fun analyzeNutritionFromImage(
        image: MultipartBody.Part
    ): ApiResult<NutritionAnalysisResponse> {
        return NetworkUtils.safeApiCall {
            foodRecognitionApi.analyzeNutrition(image, true, true, "")
        }
    }

    suspend fun analyzeIngredients(
        image: MultipartBody.Part
    ): ApiResult<IngredientAnalysisResponse> {
        return NetworkUtils.safeApiCall {
            foodRecognitionApi.analyzeIngredients(image, "en", "")
        }
    }

    // Network status monitoring
    fun observeNetworkStatus(): Flow<Boolean> = networkMonitor.isOnline

    fun observeConnectionType() = networkMonitor.connectionType

    fun observeIsMetered() = networkMonitor.isMetered

    // Offline operations
    suspend fun queueOfflineRequest(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap()
    ): String {
        return offlineSyncManager.queueOfflineRequest(url, method, body, headers)
    }

    suspend fun processPendingRequests() = offlineSyncManager.processPendingRequests()

    // Cache management
    suspend fun clearCache(): Boolean = cacheManager.clearAllCache()

    suspend fun clearExpiredCache(): Int = cacheManager.clearExpiredEntries()

    suspend fun getCacheInfo() = cacheManager.getCacheInfo()
}