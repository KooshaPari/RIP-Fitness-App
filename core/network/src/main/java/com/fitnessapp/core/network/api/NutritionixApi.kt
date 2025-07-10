package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.nutritionix.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Nutritionix API interface
 * Provides natural language food parsing and nutrition data
 */
interface NutritionixApi {

    @POST("natural/nutrients")
    suspend fun parseNaturalFood(
        @Body request: NutritionixNaturalRequest,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixNaturalResponse>

    @GET("search/instant")
    suspend fun instantSearch(
        @Query("query") query: String,
        @Query("detailed") detailed: Boolean = true,
        @Query("common") common: Boolean = true,
        @Query("branded") branded: Boolean = true,
        @Query("self") self: Boolean = false,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixInstantSearchResponse>

    @GET("search/item")
    suspend fun getItemById(
        @Query("nix_item_id") nixItemId: String,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixBrandedFood>

    @POST("natural/exercise")
    suspend fun parseNaturalExercise(
        @Body request: NutritionixExerciseRequest,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixExerciseResponse>

    @GET("locations")
    suspend fun getLocations(
        @Query("ll") latLng: String,
        @Query("distance") distance: String = "1mi",
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixLocationsResponse>

    @POST("natural/nutrients/photo")
    suspend fun analyzePhoto(
        @Body request: NutritionixPhotoRequest,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<NutritionixPhotoResponse>

    @GET("autocomplete")
    suspend fun autocomplete(
        @Query("q") query: String,
        @Header("x-app-id") appId: String,
        @Header("x-app-key") appKey: String
    ): Response<List<String>>
}