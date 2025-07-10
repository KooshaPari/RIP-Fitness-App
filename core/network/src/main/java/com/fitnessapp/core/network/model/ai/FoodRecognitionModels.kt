package com.fitnessapp.core.network.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FoodRecognitionResponse(
    @SerialName("request_id") val requestId: String,
    @SerialName("predictions") val predictions: List<FoodPrediction>,
    @SerialName("confidence_threshold") val confidenceThreshold: Float,
    @SerialName("processing_time_ms") val processingTimeMs: Long,
    @SerialName("model_version") val modelVersion: String
)

@Serializable
data class FoodPrediction(
    @SerialName("food_name") val foodName: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("bounding_box") val boundingBox: BoundingBox? = null,
    @SerialName("food_id") val foodId: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("subcategory") val subcategory: String? = null,
    @SerialName("estimated_portion") val estimatedPortion: PortionEstimate? = null,
    @SerialName("nutrition_preview") val nutritionPreview: NutritionPreview? = null
)

@Serializable
data class BoundingBox(
    @SerialName("x") val x: Float,
    @SerialName("y") val y: Float,
    @SerialName("width") val width: Float,
    @SerialName("height") val height: Float
)

@Serializable
data class PortionEstimate(
    @SerialName("serving_size") val servingSize: String,
    @SerialName("weight_grams") val weightGrams: Float? = null,
    @SerialName("volume_ml") val volumeMl: Float? = null,
    @SerialName("confidence") val confidence: Float
)

@Serializable
data class NutritionPreview(
    @SerialName("calories") val calories: Float? = null,
    @SerialName("protein_g") val proteinG: Float? = null,
    @SerialName("carbs_g") val carbsG: Float? = null,
    @SerialName("fat_g") val fatG: Float? = null,
    @SerialName("fiber_g") val fiberG: Float? = null
)

@Serializable
data class NutritionAnalysisResponse(
    @SerialName("request_id") val requestId: String,
    @SerialName("foods_detected") val foodsDetected: List<DetectedFood>,
    @SerialName("total_nutrition") val totalNutrition: DetailedNutrition,
    @SerialName("portion_analysis") val portionAnalysis: PortionAnalysis,
    @SerialName("processing_time_ms") val processingTimeMs: Long
)

@Serializable
data class DetectedFood(
    @SerialName("food_name") val foodName: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("portion") val portion: PortionEstimate,
    @SerialName("nutrition") val nutrition: DetailedNutrition,
    @SerialName("ingredients") val ingredients: List<String>? = null,
    @SerialName("allergens") val allergens: List<String>? = null
)

@Serializable
data class DetailedNutrition(
    @SerialName("calories") val calories: Float,
    @SerialName("total_fat_g") val totalFatG: Float,
    @SerialName("saturated_fat_g") val saturatedFatG: Float? = null,
    @SerialName("trans_fat_g") val transFatG: Float? = null,
    @SerialName("cholesterol_mg") val cholesterolMg: Float? = null,
    @SerialName("sodium_mg") val sodiumMg: Float? = null,
    @SerialName("total_carbs_g") val totalCarbsG: Float,
    @SerialName("dietary_fiber_g") val dietaryFiberG: Float? = null,
    @SerialName("total_sugars_g") val totalSugarsG: Float? = null,
    @SerialName("added_sugars_g") val addedSugarsG: Float? = null,
    @SerialName("protein_g") val proteinG: Float,
    @SerialName("vitamin_a_mcg") val vitaminAMcg: Float? = null,
    @SerialName("vitamin_c_mg") val vitaminCMg: Float? = null,
    @SerialName("vitamin_d_mcg") val vitaminDMcg: Float? = null,
    @SerialName("calcium_mg") val calciumMg: Float? = null,
    @SerialName("iron_mg") val ironMg: Float? = null,
    @SerialName("potassium_mg") val potassiumMg: Float? = null
)

@Serializable
data class PortionAnalysis(
    @SerialName("total_detected_items") val totalDetectedItems: Int,
    @SerialName("confidence_average") val confidenceAverage: Float,
    @SerialName("portion_accuracy") val portionAccuracy: String, // "high", "medium", "low"
    @SerialName("recommendations") val recommendations: List<String>
)

@Serializable
data class IngredientAnalysisResponse(
    @SerialName("request_id") val requestId: String,
    @SerialName("detected_ingredients") val detectedIngredients: List<DetectedIngredient>,
    @SerialName("recipe_suggestions") val recipeSuggestions: List<RecipeSuggestion>? = null,
    @SerialName("processing_time_ms") val processingTimeMs: Long
)

@Serializable
data class DetectedIngredient(
    @SerialName("ingredient_name") val ingredientName: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("category") val category: String, // "vegetable", "protein", "grain", etc.
    @SerialName("freshness_estimate") val freshnessEstimate: String? = null, // "fresh", "ripe", "overripe"
    @SerialName("estimated_amount") val estimatedAmount: String? = null,
    @SerialName("bounding_box") val boundingBox: BoundingBox? = null,
    @SerialName("nutrition_density") val nutritionDensity: String? = null // "high", "medium", "low"
)

@Serializable
data class RecipeSuggestion(
    @SerialName("recipe_name") val recipeName: String,
    @SerialName("confidence") val confidence: Float,
    @SerialName("cuisine_type") val cuisineType: String? = null,
    @SerialName("difficulty_level") val difficultyLevel: String? = null,
    @SerialName("cooking_time_minutes") val cookingTimeMinutes: Int? = null,
    @SerialName("matching_ingredients") val matchingIngredients: List<String>,
    @SerialName("missing_ingredients") val missingIngredients: List<String>? = null
)

@Serializable
data class BatchRecognitionRequest(
    @SerialName("image_urls") val imageUrls: List<String>,
    @SerialName("confidence_threshold") val confidenceThreshold: Float = 0.7f,
    @SerialName("max_predictions_per_image") val maxPredictionsPerImage: Int = 5,
    @SerialName("include_nutrition") val includeNutrition: Boolean = true,
    @SerialName("include_portions") val includePortions: Boolean = true
)

@Serializable
data class BatchRecognitionResponse(
    @SerialName("request_id") val requestId: String,
    @SerialName("results") val results: List<ImageRecognitionResult>,
    @SerialName("total_processing_time_ms") val totalProcessingTimeMs: Long,
    @SerialName("batch_statistics") val batchStatistics: BatchStatistics
)

@Serializable
data class ImageRecognitionResult(
    @SerialName("image_url") val imageUrl: String,
    @SerialName("status") val status: String, // "success", "failed", "partially_processed"
    @SerialName("predictions") val predictions: List<FoodPrediction>? = null,
    @SerialName("error_message") val errorMessage: String? = null,
    @SerialName("processing_time_ms") val processingTimeMs: Long
)

@Serializable
data class BatchStatistics(
    @SerialName("total_images") val totalImages: Int,
    @SerialName("successful_images") val successfulImages: Int,
    @SerialName("failed_images") val failedImages: Int,
    @SerialName("total_foods_detected") val totalFoodsDetected: Int,
    @SerialName("average_confidence") val averageConfidence: Float
)

@Serializable
data class ModelInfoResponse(
    @SerialName("model_name") val modelName: String,
    @SerialName("version") val version: String,
    @SerialName("supported_foods_count") val supportedFoodsCount: Int,
    @SerialName("supported_cuisines") val supportedCuisines: List<String>,
    @SerialName("supported_languages") val supportedLanguages: List<String>,
    @SerialName("accuracy_metrics") val accuracyMetrics: AccuracyMetrics,
    @SerialName("last_updated") val lastUpdated: String
)

@Serializable
data class AccuracyMetrics(
    @SerialName("top1_accuracy") val top1Accuracy: Float,
    @SerialName("top5_accuracy") val top5Accuracy: Float,
    @SerialName("portion_estimation_accuracy") val portionEstimationAccuracy: Float,
    @SerialName("nutrition_estimation_accuracy") val nutritionEstimationAccuracy: Float
)

@Serializable
data class FeedbackRequest(
    @SerialName("request_id") val requestId: String,
    @SerialName("feedback_type") val feedbackType: String, // "correction", "confirmation", "suggestion"
    @SerialName("correct_food_name") val correctFoodName: String? = null,
    @SerialName("correct_portion") val correctPortion: String? = null,
    @SerialName("user_comments") val userComments: String? = null,
    @SerialName("rating") val rating: Int? = null // 1-5 scale
)

@Serializable
data class FeedbackResponse(
    @SerialName("feedback_id") val feedbackId: String,
    @SerialName("status") val status: String, // "received", "processed", "incorporated"
    @SerialName("thank_you_message") val thankYouMessage: String
)