package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.ai.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * AI Food Recognition API interface
 * Provides image-based food identification and nutrition analysis
 */
interface FoodRecognitionApi {

    @Multipart
    @POST("recognize")
    suspend fun recognizeFood(
        @Part image: MultipartBody.Part,
        @Query("confidence_threshold") confidenceThreshold: Float = 0.7f,
        @Query("max_predictions") maxPredictions: Int = 5,
        @Header("Authorization") authorization: String
    ): Response<FoodRecognitionResponse>

    @Multipart
    @POST("analyze/nutrition")
    suspend fun analyzeNutrition(
        @Part image: MultipartBody.Part,
        @Query("portion_estimation") portionEstimation: Boolean = true,
        @Query("detailed_nutrients") detailedNutrients: Boolean = true,
        @Header("Authorization") authorization: String
    ): Response<NutritionAnalysisResponse>

    @Multipart
    @POST("analyze/ingredients")
    suspend fun analyzeIngredients(
        @Part image: MultipartBody.Part,
        @Query("language") language: String = "en",
        @Header("Authorization") authorization: String
    ): Response<IngredientAnalysisResponse>

    @POST("batch/recognize")
    suspend fun recognizeFoodBatch(
        @Body request: BatchRecognitionRequest,
        @Header("Authorization") authorization: String
    ): Response<BatchRecognitionResponse>

    @GET("models/info")
    suspend fun getModelInfo(
        @Header("Authorization") authorization: String
    ): Response<ModelInfoResponse>

    @POST("feedback")
    suspend fun submitFeedback(
        @Body feedback: FeedbackRequest,
        @Header("Authorization") authorization: String
    ): Response<FeedbackResponse>
}