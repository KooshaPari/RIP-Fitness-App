package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Recipe entity for custom meal creation and tracking.
 * 
 * Allows users to create custom recipes with multiple ingredients,
 * supporting accurate nutrition calculation and meal planning.
 * 
 * Features:
 * - Multi-ingredient recipes
 * - Scalable serving sizes
 * - Nutrition auto-calculation
 * - Recipe sharing and rating
 */
@Entity(
    tableName = "recipes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["created_by_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["created_by_user_id"]),
        Index(value = ["recipe_name"]),
        Index(value = ["recipe_category"]),
        Index(value = ["is_public"]),
        Index(value = ["difficulty_level"]),
        Index(value = ["created_at"]),
        Index(value = ["usage_count"])
    ]
)
data class RecipeEntity(
    @PrimaryKey
    @ColumnInfo(name = "recipe_id")
    val recipeId: String,
    
    @ColumnInfo(name = "created_by_user_id")
    val createdByUserId: String,
    
    // Basic recipe information
    @ColumnInfo(name = "recipe_name")
    val recipeName: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "recipe_category")
    val recipeCategory: String?, // "breakfast", "lunch", "dinner", "snack", "dessert", "drink"
    
    @ColumnInfo(name = "cuisine_type")
    val cuisineType: String?, // "italian", "mexican", "asian", "american", etc.
    
    @ColumnInfo(name = "dietary_tags")
    val dietaryTags: String?, // JSON array: "vegan", "vegetarian", "gluten_free", "keto", etc.
    
    // Serving information
    @ColumnInfo(name = "servings")
    val servings: Int,
    
    @ColumnInfo(name = "serving_size_description")
    val servingSizeDescription: String?, // "1 cup", "1 slice", etc.
    
    // Preparation details
    @ColumnInfo(name = "prep_time_minutes")
    val prepTimeMinutes: Int?,
    
    @ColumnInfo(name = "cook_time_minutes")
    val cookTimeMinutes: Int?,
    
    @ColumnInfo(name = "total_time_minutes")
    val totalTimeMinutes: Int?,
    
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String?, // "easy", "medium", "hard"
    
    // Instructions and ingredients (stored as JSON)
    @ColumnInfo(name = "ingredients_json")
    val ingredientsJson: String, // JSON array of ingredients with quantities
    
    @ColumnInfo(name = "instructions")
    val instructions: String?,
    
    @ColumnInfo(name = "cooking_tips")
    val cookingTips: String?,
    
    // Calculated nutrition (per serving)
    @ColumnInfo(name = "calories_per_serving")
    val caloriesPerServing: Float?,
    
    @ColumnInfo(name = "protein_grams_per_serving")
    val proteinGramsPerServing: Float?,
    
    @ColumnInfo(name = "carbohydrate_grams_per_serving")
    val carbohydrateGramsPerServing: Float?,
    
    @ColumnInfo(name = "fat_grams_per_serving")
    val fatGramsPerServing: Float?,
    
    @ColumnInfo(name = "fiber_grams_per_serving")
    val fiberGramsPerServing: Float?,
    
    @ColumnInfo(name = "sugar_grams_per_serving")
    val sugarGramsPerServing: Float?,
    
    @ColumnInfo(name = "sodium_mg_per_serving")
    val sodiumMgPerServing: Float?,
    
    // Recipe quality metrics
    @ColumnInfo(name = "nutrition_score")
    val nutritionScore: Float?, // 0-100 based on nutrient density
    
    @ColumnInfo(name = "ingredient_quality_score")
    val ingredientQualityScore: Float?, // Based on whole food percentage
    
    // Social features
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false,
    
    @ColumnInfo(name = "average_rating")
    val averageRating: Float?, // 1.0-5.0
    
    @ColumnInfo(name = "rating_count")
    val ratingCount: Int = 0,
    
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,
    
    @ColumnInfo(name = "save_count")
    val saveCount: Int = 0, // How many users saved this recipe
    
    // Media
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String?,
    
    @ColumnInfo(name = "video_url")
    val videoUrl: String?,
    
    // Equipment and allergens
    @ColumnInfo(name = "required_equipment")
    val requiredEquipment: String?, // JSON array of equipment needed
    
    @ColumnInfo(name = "allergens")
    val allergens: String?, // JSON array of allergens
    
    // Cost and nutrition efficiency
    @ColumnInfo(name = "estimated_cost_per_serving")
    val estimatedCostPerServing: Float?,
    
    @ColumnInfo(name = "protein_per_dollar")
    val proteinPerDollar: Float?,
    
    @ColumnInfo(name = "calories_per_dollar")
    val caloriesPerDollar: Float?,
    
    // Recipe source and attribution
    @ColumnInfo(name = "source_name")
    val sourceName: String?, // "user_created", "imported", "cookbook", "website"
    
    @ColumnInfo(name = "source_url")
    val sourceUrl: String?,
    
    @ColumnInfo(name = "attribution")
    val attribution: String?,
    
    // Versioning for recipe updates
    @ColumnInfo(name = "version")
    val version: Int = 1,
    
    @ColumnInfo(name = "parent_recipe_id")
    val parentRecipeId: String?, // For recipe variations
    
    // Administrative
    @ColumnInfo(name = "is_approved")
    val isApproved: Boolean = true,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "last_nutrition_calculation")
    val lastNutritionCalculation: LocalDateTime?,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: LocalDateTime?
)