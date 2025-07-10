package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.usda.*
import retrofit2.Response
import retrofit2.http.*

/**
 * USDA FoodData Central API interface
 * Provides access to comprehensive food nutrition database
 */
interface UsdaFoodDataApi {

    @GET("foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("dataType") dataType: String = "Foundation,SR Legacy,Survey (FNDDS)",
        @Query("pageSize") pageSize: Int = 25,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("sortBy") sortBy: String = "dataType.keyword",
        @Query("sortOrder") sortOrder: String = "asc",
        @Query("brandOwner") brandOwner: String? = null,
        @Query("api_key") apiKey: String
    ): Response<UsdaFoodSearchResponse>

    @GET("food/{fdcId}")
    suspend fun getFoodDetails(
        @Path("fdcId") fdcId: String,
        @Query("nutrients") nutrients: String? = null,
        @Query("api_key") apiKey: String
    ): Response<UsdaFoodDetails>

    @POST("foods")
    suspend fun getFoodsByIds(
        @Body request: UsdaFoodsByIdsRequest,
        @Query("api_key") apiKey: String
    ): Response<List<UsdaFoodDetails>>

    @GET("foods/list")
    suspend fun getFoodsList(
        @Query("dataType") dataType: String = "Foundation,SR Legacy",
        @Query("pageSize") pageSize: Int = 25,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("sortBy") sortBy: String = "description",
        @Query("sortOrder") sortOrder: String = "asc",
        @Query("api_key") apiKey: String
    ): Response<UsdaFoodsListResponse>

    @GET("nutrients")
    suspend fun getNutrients(
        @Query("api_key") apiKey: String
    ): Response<List<UsdaNutrient>>
}