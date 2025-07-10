package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.fatsecret.*
import retrofit2.Response
import retrofit2.http.*

/**
 * FatSecret Platform API interface
 * Provides food database and nutrition information
 */
interface FatSecretApi {

    @POST(".")
    @FormUrlEncoded
    suspend fun searchFoods(
        @Field("method") method: String = "foods.search",
        @Field("search_expression") searchExpression: String,
        @Field("page_number") pageNumber: Int = 0,
        @Field("max_results") maxResults: Int = 20,
        @Field("format") format: String = "json"
    ): Response<FatSecretFoodSearchResponse>

    @POST(".")
    @FormUrlEncoded
    suspend fun getFoodDetails(
        @Field("method") method: String = "food.get",
        @Field("food_id") foodId: String,
        @Field("format") format: String = "json"
    ): Response<FatSecretFoodDetails>

    @POST(".")
    @FormUrlEncoded
    suspend fun getFoodByBarcode(
        @Field("method") method: String = "food.find_id_for_barcode",
        @Field("barcode") barcode: String,
        @Field("format") format: String = "json"
    ): Response<FatSecretBarcodeResponse>

    @POST(".")
    @FormUrlEncoded
    suspend fun getRecipes(
        @Field("method") method: String = "recipes.search",
        @Field("search_expression") searchExpression: String,
        @Field("page_number") pageNumber: Int = 0,
        @Field("max_results") maxResults: Int = 20,
        @Field("format") format: String = "json"
    ): Response<FatSecretRecipeSearchResponse>

    @POST(".")
    @FormUrlEncoded
    suspend fun getRecipeDetails(
        @Field("method") method: String = "recipe.get",
        @Field("recipe_id") recipeId: String,
        @Field("format") format: String = "json"
    ): Response<FatSecretRecipeDetails>

    @POST(".")
    @FormUrlEncoded
    suspend fun autocompleteFood(
        @Field("method") method: String = "foods.autocomplete",
        @Field("expression") expression: String,
        @Field("max_results") maxResults: Int = 8,
        @Field("format") format: String = "json"
    ): Response<FatSecretAutocompleteResponse>
}