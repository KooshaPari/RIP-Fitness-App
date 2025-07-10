package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Food database entity for comprehensive food tracking.
 * 
 * Stores detailed nutritional information for foods, supporting both
 * database foods and user-created entries. Designed to work with
 * various food databases (USDA, brand databases, restaurant data).
 * 
 * MacroFactor-inspired features:
 * - Comprehensive nutrient profiles
 * - Food quality scoring
 * - Multiple serving size options
 * - Brand and barcode support
 */
@Entity(
    tableName = "foods",
    indices = [
        Index(value = ["barcode"], unique = true),
        Index(value = ["food_name"]),
        Index(value = ["brand_name"]),
        Index(value = ["food_category"]),
        Index(value = ["data_source"]),
        Index(value = ["is_verified"]),
        Index(value = ["usage_count"]),
        Index(value = ["created_at"])
    ]
)
data class FoodEntity(
    @PrimaryKey
    @ColumnInfo(name = "food_id")
    val foodId: String,
    
    // Basic food information
    @ColumnInfo(name = "food_name")
    val foodName: String,
    
    @ColumnInfo(name = "brand_name")
    val brandName: String?,
    
    @ColumnInfo(name = "barcode")
    val barcode: String?,
    
    @ColumnInfo(name = "food_description")
    val foodDescription: String?,
    
    @ColumnInfo(name = "food_category")
    val foodCategory: String?, // "protein", "carbohydrate", "fat", "vegetable", "fruit", "dairy", etc.
    
    @ColumnInfo(name = "food_subcategory")
    val foodSubcategory: String?,
    
    // Serving size information (per 100g as base)
    @ColumnInfo(name = "serving_size_grams")
    val servingSizeGrams: Float = 100.0f, // Base serving size for calculations
    
    @ColumnInfo(name = "serving_description")
    val servingDescription: String?, // "1 cup", "1 medium", "1 slice", etc.
    
    @ColumnInfo(name = "alternative_serving_sizes")
    val alternativeServingSizes: String?, // JSON array of alternative serving options
    
    // Macronutrients (per 100g)
    @ColumnInfo(name = "calories_per_100g")
    val caloriesPer100g: Float,
    
    @ColumnInfo(name = "protein_grams_per_100g")
    val proteinGramsPer100g: Float,
    
    @ColumnInfo(name = "carbohydrate_grams_per_100g")
    val carbohydrateGramsPer100g: Float,
    
    @ColumnInfo(name = "fat_grams_per_100g")
    val fatGramsPer100g: Float,
    
    @ColumnInfo(name = "fiber_grams_per_100g")
    val fiberGramsPer100g: Float?,
    
    @ColumnInfo(name = "sugar_grams_per_100g")
    val sugarGramsPer100g: Float?,
    
    @ColumnInfo(name = "sodium_mg_per_100g")
    val sodiumMgPer100g: Float?,
    
    // Detailed carbohydrate breakdown
    @ColumnInfo(name = "starch_grams_per_100g")
    val starchGramsPer100g: Float?,
    
    @ColumnInfo(name = "net_carbs_grams_per_100g")
    val netCarbsGramsPer100g: Float?,
    
    // Detailed fat breakdown
    @ColumnInfo(name = "saturated_fat_grams_per_100g")
    val saturatedFatGramsPer100g: Float?,
    
    @ColumnInfo(name = "monounsaturated_fat_grams_per_100g")
    val monounsaturatedFatGramsPer100g: Float?,
    
    @ColumnInfo(name = "polyunsaturated_fat_grams_per_100g")
    val polyunsaturatedFatGramsPer100g: Float?,
    
    @ColumnInfo(name = "trans_fat_grams_per_100g")
    val transFatGramsPer100g: Float?,
    
    @ColumnInfo(name = "cholesterol_mg_per_100g")
    val cholesterolMgPer100g: Float?,
    
    // Micronutrients (per 100g)
    @ColumnInfo(name = "vitamin_a_mcg_per_100g")
    val vitaminAMcgPer100g: Float?,
    
    @ColumnInfo(name = "vitamin_c_mg_per_100g")
    val vitaminCMgPer100g: Float?,
    
    @ColumnInfo(name = "vitamin_d_mcg_per_100g")
    val vitaminDMcgPer100g: Float?,
    
    @ColumnInfo(name = "calcium_mg_per_100g")
    val calciumMgPer100g: Float?,
    
    @ColumnInfo(name = "iron_mg_per_100g")
    val ironMgPer100g: Float?,
    
    @ColumnInfo(name = "magnesium_mg_per_100g")
    val magnesiumMgPer100g: Float?,
    
    @ColumnInfo(name = "potassium_mg_per_100g")
    val potassiumMgPer100g: Float?,
    
    @ColumnInfo(name = "zinc_mg_per_100g")
    val zincMgPer100g: Float?,
    
    // Food quality and classification
    @ColumnInfo(name = "food_quality_score")
    val foodQualityScore: Float?, // 0-100 based on nutrient density
    
    @ColumnInfo(name = "processing_level")
    val processingLevel: String?, // "minimally_processed", "processed", "ultra_processed"
    
    @ColumnInfo(name = "is_whole_food")
    val isWholeFood: Boolean = false,
    
    @ColumnInfo(name = "glycemic_index")
    val glycemicIndex: Int?,
    
    @ColumnInfo(name = "glycemic_load")
    val glycemicLoad: Float?,
    
    // Data source and verification
    @ColumnInfo(name = "data_source")
    val dataSource: String, // "usda", "user_input", "brand_database", "restaurant"
    
    @ColumnInfo(name = "data_source_id")
    val dataSourceId: String?, // External ID from the data source
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,
    
    @ColumnInfo(name = "verified_by")
    val verifiedBy: String?, // "admin", "community", "brand"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String?, // "high", "medium", "low"
    
    // Usage and popularity
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,
    
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: LocalDateTime?,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "user_rating")
    val userRating: Float?, // 1.0-5.0
    
    // Allergen and dietary information
    @ColumnInfo(name = "allergens")
    val allergens: String?, // JSON array of allergens
    
    @ColumnInfo(name = "dietary_tags")
    val dietaryTags: String?, // JSON array: "vegan", "vegetarian", "gluten_free", "keto", etc.
    
    @ColumnInfo(name = "ingredients")
    val ingredients: String?, // For packaged foods
    
    // Preparation and storage
    @ColumnInfo(name = "preparation_methods")
    val preparationMethods: String?, // JSON array of cooking methods
    
    @ColumnInfo(name = "storage_instructions")
    val storageInstructions: String?,
    
    @ColumnInfo(name = "shelf_life_days")
    val shelfLifeDays: Int?,
    
    // Images and media
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String?,
    
    // Administrative
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "created_by_user_id")
    val createdByUserId: String?,
    
    @ColumnInfo(name = "approved_by_admin")
    val approvedByAdmin: Boolean = false,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "data_source_updated_at")
    val dataSourceUpdatedAt: LocalDateTime?
)