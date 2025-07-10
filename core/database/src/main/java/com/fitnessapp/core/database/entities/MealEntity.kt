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
 * Meal entity for grouping nutrition entries and planning.
 * 
 * Allows users to group multiple foods/recipes into meals,
 * supporting meal planning, templates, and batch nutrition tracking.
 * 
 * MacroFactor-inspired features:
 * - Meal timing analysis
 * - Template creation for repeated meals
 * - Macro distribution optimization
 */
@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["date"]),
        Index(value = ["meal_type"]),
        Index(value = ["is_template"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite index for daily meal queries
    ]
)
data class MealEntity(
    @PrimaryKey
    @ColumnInfo(name = "meal_id")
    val mealId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Meal identification
    @ColumnInfo(name = "meal_name")
    val mealName: String?,
    
    @ColumnInfo(name = "meal_type")
    val mealType: String, // "breakfast", "lunch", "dinner", "snack", "pre_workout", "post_workout"
    
    @ColumnInfo(name = "date")
    val date: LocalDate?,
    
    @ColumnInfo(name = "planned_time")
    val plannedTime: LocalTime?,
    
    @ColumnInfo(name = "actual_time")
    val actualTime: LocalTime?,
    
    // Template functionality
    @ColumnInfo(name = "is_template")
    val isTemplate: Boolean = false,
    
    @ColumnInfo(name = "template_name")
    val templateName: String?,
    
    @ColumnInfo(name = "template_category")
    val templateCategory: String?, // "quick_breakfast", "post_workout", "high_protein", etc.
    
    @ColumnInfo(name = "template_usage_count")
    val templateUsageCount: Int = 0,
    
    // Meal components (stored as JSON for flexibility)
    @ColumnInfo(name = "food_items_json")
    val foodItemsJson: String?, // JSON array of food IDs and quantities
    
    @ColumnInfo(name = "recipe_items_json")
    val recipeItemsJson: String?, // JSON array of recipe IDs and servings
    
    // Calculated totals
    @ColumnInfo(name = "total_calories")
    val totalCalories: Float?,
    
    @ColumnInfo(name = "total_protein_grams")
    val totalProteinGrams: Float?,
    
    @ColumnInfo(name = "total_carbohydrate_grams")
    val totalCarbohydrateGrams: Float?,
    
    @ColumnInfo(name = "total_fat_grams")
    val totalFatGrams: Float?,
    
    @ColumnInfo(name = "total_fiber_grams")
    val totalFiberGrams: Float?,
    
    @ColumnInfo(name = "total_sodium_mg")
    val totalSodiumMg: Float?,
    
    // Meal timing and context
    @ColumnInfo(name = "pre_workout_meal")
    val preWorkoutMeal: Boolean = false,
    
    @ColumnInfo(name = "post_workout_meal")
    val postWorkoutMeal: Boolean = false,
    
    @ColumnInfo(name = "workout_id")
    val workoutId: String?, // Link to associated workout
    
    @ColumnInfo(name = "minutes_before_workout")
    val minutesBeforeWorkout: Int?,
    
    @ColumnInfo(name = "minutes_after_workout")
    val minutesAfterWorkout: Int?,
    
    // Meal planning and preparation
    @ColumnInfo(name = "prep_time_minutes")
    val prepTimeMinutes: Int?,
    
    @ColumnInfo(name = "is_meal_prepped")
    val isMealPrepped: Boolean = false,
    
    @ColumnInfo(name = "prep_date")
    val prepDate: LocalDate?,
    
    @ColumnInfo(name = "storage_instructions")
    val storageInstructions: String?,
    
    // Goal alignment
    @ColumnInfo(name = "fits_macro_targets")
    val fitsMacroTargets: Boolean?,
    
    @ColumnInfo(name = "macro_distribution_score")
    val macroDistributionScore: Float?, // How well it fits daily macro goals
    
    @ColumnInfo(name = "satiety_score")
    val satietyScore: Float?, // Predicted satiety based on food composition
    
    // Social and sharing
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "shared_as_template")
    val sharedAsTemplate: Boolean = false,
    
    @ColumnInfo(name = "rating")
    val rating: Float?, // User's personal rating of the meal
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    // Cost tracking
    @ColumnInfo(name = "estimated_cost")
    val estimatedCost: Float?,
    
    @ColumnInfo(name = "cost_per_protein_gram")
    val costPerProteinGram: Float?,
    
    // Completion status
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completion_percentage")
    val completionPercentage: Float = 100.0f, // How much of the planned meal was consumed
    
    @ColumnInfo(name = "completion_notes")
    val completionNotes: String?,
    
    // Images
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?,
    
    @ColumnInfo(name = "before_photo_url")
    val beforePhotoUrl: String?,
    
    @ColumnInfo(name = "after_photo_url")
    val afterPhotoUrl: String?,
    
    // Location and context
    @ColumnInfo(name = "location")
    val location: String?, // "home", "restaurant", "work", "gym", etc.
    
    @ColumnInfo(name = "eating_context")
    val eatingContext: String?, // "rushed", "relaxed", "social", "alone", etc.
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "last_nutrition_calculation")
    val lastNutritionCalculation: LocalDateTime?
)