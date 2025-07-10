package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Comprehensive nutrition tracking entity inspired by MacroFactor's approach.
 * 
 * Tracks daily nutrition intake with detailed macro and micronutrient data,
 * supporting adaptive TDEE calculations and metabolic adaptation analysis.
 * 
 * Key MacroFactor features:
 * - Detailed macronutrient tracking
 * - Meal timing analysis
 * - Adaptive calorie target adjustments
 * - Food quality scoring
 */
@Entity(
    tableName = "nutrition_entries",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodEntity::class,
            parentColumns = ["food_id"],
            childColumns = ["food_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["date"]),
        Index(value = ["meal_type"]),
        Index(value = ["food_id"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite index for daily queries
    ]
)
data class NutritionEntity(
    @PrimaryKey
    @ColumnInfo(name = "nutrition_id")
    val nutritionId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "food_id")
    val foodId: String?,
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "meal_type")
    val mealType: String, // "breakfast", "lunch", "dinner", "snack", "pre_workout", "post_workout"
    
    @ColumnInfo(name = "meal_time")
    val mealTime: LocalTime?,
    
    // Food identification
    @ColumnInfo(name = "food_name")
    val foodName: String,
    
    @ColumnInfo(name = "brand_name")
    val brandName: String?,
    
    @ColumnInfo(name = "barcode")
    val barcode: String?,
    
    // Serving information
    @ColumnInfo(name = "serving_size_grams")
    val servingSizeGrams: Float,
    
    @ColumnInfo(name = "serving_description")
    val servingDescription: String?, // "1 cup", "1 medium apple", etc.
    
    @ColumnInfo(name = "quantity")
    val quantity: Float = 1.0f,
    
    // Macronutrients (per serving consumed)
    @ColumnInfo(name = "calories")
    val calories: Float,
    
    @ColumnInfo(name = "protein_grams")
    val proteinGrams: Float,
    
    @ColumnInfo(name = "carbohydrate_grams")
    val carbohydrateGrams: Float,
    
    @ColumnInfo(name = "fat_grams")
    val fatGrams: Float,
    
    @ColumnInfo(name = "fiber_grams")
    val fiberGrams: Float?,
    
    @ColumnInfo(name = "sugar_grams")
    val sugarGrams: Float?,
    
    @ColumnInfo(name = "sodium_mg")
    val sodiumMg: Float?,
    
    // Detailed carbohydrate breakdown
    @ColumnInfo(name = "starch_grams")
    val starchGrams: Float?,
    
    @ColumnInfo(name = "net_carbs_grams")
    val netCarbsGrams: Float?,
    
    // Detailed fat breakdown
    @ColumnInfo(name = "saturated_fat_grams")
    val saturatedFatGrams: Float?,
    
    @ColumnInfo(name = "monounsaturated_fat_grams")
    val monounsaturatedFatGrams: Float?,
    
    @ColumnInfo(name = "polyunsaturated_fat_grams")
    val polyunsaturatedFatGrams: Float?,
    
    @ColumnInfo(name = "trans_fat_grams")
    val transFatGrams: Float?,
    
    @ColumnInfo(name = "cholesterol_mg")
    val cholesterolMg: Float?,
    
    // Micronutrients (optional detailed tracking)
    @ColumnInfo(name = "vitamin_a_mcg")
    val vitaminAMcg: Float?,
    
    @ColumnInfo(name = "vitamin_c_mg")
    val vitaminCMg: Float?,
    
    @ColumnInfo(name = "vitamin_d_mcg")
    val vitaminDMcg: Float?,
    
    @ColumnInfo(name = "vitamin_e_mg")
    val vitaminEMg: Float?,
    
    @ColumnInfo(name = "vitamin_k_mcg")
    val vitaminKMcg: Float?,
    
    @ColumnInfo(name = "thiamine_mg")
    val thiamineMg: Float?,
    
    @ColumnInfo(name = "riboflavin_mg")
    val riboflavinMg: Float?,
    
    @ColumnInfo(name = "niacin_mg")
    val niacinMg: Float?,
    
    @ColumnInfo(name = "vitamin_b6_mg")
    val vitaminB6Mg: Float?,
    
    @ColumnInfo(name = "folate_mcg")
    val folateMcg: Float?,
    
    @ColumnInfo(name = "vitamin_b12_mcg")
    val vitaminB12Mcg: Float?,
    
    @ColumnInfo(name = "calcium_mg")
    val calciumMg: Float?,
    
    @ColumnInfo(name = "iron_mg")
    val ironMg: Float?,
    
    @ColumnInfo(name = "magnesium_mg")
    val magnesiumMg: Float?,
    
    @ColumnInfo(name = "phosphorus_mg")
    val phosphorusMg: Float?,
    
    @ColumnInfo(name = "potassium_mg")
    val potassiumMg: Float?,
    
    @ColumnInfo(name = "zinc_mg")
    val zincMg: Float?,
    
    // Food quality and classification
    @ColumnInfo(name = "food_quality_score")
    val foodQualityScore: Float?, // 0-100 based on nutrient density
    
    @ColumnInfo(name = "processing_level")
    val processingLevel: String?, // "minimally_processed", "processed", "ultra_processed"
    
    @ColumnInfo(name = "is_whole_food")
    val isWholeFood: Boolean = false,
    
    @ColumnInfo(name = "food_category")
    val foodCategory: String?, // "protein", "carbohydrate", "fat", "vegetable", "fruit", "dairy", etc.
    
    // Tracking metadata
    @ColumnInfo(name = "entry_method")
    val entryMethod: String, // "manual", "barcode", "search", "recipe", "quick_add", "meal_plan"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String?, // "high", "medium", "low" - for accuracy of nutrition data
    
    @ColumnInfo(name = "data_source")
    val dataSource: String?, // "usda", "user_input", "brand_database", "restaurant"
    
    // MacroFactor-inspired features
    @ColumnInfo(name = "pre_workout_window")
    val preWorkoutWindow: Boolean = false, // Eaten within 2 hours of workout
    
    @ColumnInfo(name = "post_workout_window")
    val postWorkoutWindow: Boolean = false, // Eaten within 2 hours after workout
    
    @ColumnInfo(name = "contributes_to_tdee")
    val contributesToTdee: Boolean = true, // For adaptive TDEE calculations
    
    // Social and sharing
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)