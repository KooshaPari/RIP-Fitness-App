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
 * Quick Add entity for rapid calorie and macro tracking.
 * 
 * Inspired by MacroFactor's quick add feature, allowing users to
 * rapidly log calories and macros without detailed food selection.
 * Perfect for estimating restaurant meals, social eating, or
 * when precise tracking isn't feasible.
 * 
 * Features:
 * - Quick calorie logging
 * - Macro estimation
 * - Meal categorization
 * - Template creation for common quick adds
 */
@Entity(
    tableName = "quick_adds",
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
        Index(value = ["user_id", "date"]) // Composite index for daily queries
    ]
)
data class QuickAddEntity(
    @PrimaryKey
    @ColumnInfo(name = "quick_add_id")
    val quickAddId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic information
    @ColumnInfo(name = "name")
    val name: String, // "Restaurant dinner", "Birthday cake", "Protein shake", etc.
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "date")
    val date: LocalDate?,
    
    @ColumnInfo(name = "meal_type")
    val mealType: String, // "breakfast", "lunch", "dinner", "snack", "pre_workout", "post_workout"
    
    @ColumnInfo(name = "meal_time")
    val mealTime: LocalTime?,
    
    // Nutrition information (user estimated or calculated)
    @ColumnInfo(name = "calories")
    val calories: Float,
    
    @ColumnInfo(name = "protein_grams")
    val proteinGrams: Float?,
    
    @ColumnInfo(name = "carbohydrate_grams")
    val carbohydrateGrams: Float?,
    
    @ColumnInfo(name = "fat_grams")
    val fatGrams: Float?,
    
    @ColumnInfo(name = "fiber_grams")
    val fiberGrams: Float?,
    
    @ColumnInfo(name = "sodium_mg")
    val sodiumMg: Float?,
    
    // Estimation method and confidence
    @ColumnInfo(name = "estimation_method")
    val estimationMethod: String, // "visual", "portion_comparison", "menu_lookup", "experience", "calculated"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String, // "low", "medium", "high"
    
    @ColumnInfo(name = "estimation_notes")
    val estimationNotes: String?, // User notes about how they estimated
    
    // Context information
    @ColumnInfo(name = "location")
    val location: String?, // "restaurant_name", "home", "work", "friend's_house", etc.
    
    @ColumnInfo(name = "occasion")
    val occasion: String?, // "date_night", "business_lunch", "birthday", "holiday", etc.
    
    @ColumnInfo(name = "eating_context")
    val eatingContext: String?, // "social", "alone", "rushed", "relaxed"
    
    @ColumnInfo(name = "portion_size_description")
    val portionSizeDescription: String?, // "large plate", "small portion", "2 slices", etc.
    
    // Template functionality
    @ColumnInfo(name = "is_template")
    val isTemplate: Boolean = false,
    
    @ColumnInfo(name = "template_name")
    val templateName: String?,
    
    @ColumnInfo(name = "template_category")
    val templateCategory: String?, // "restaurant_meals", "drinks", "snacks", "social_eating"
    
    @ColumnInfo(name = "template_usage_count")
    val templateUsageCount: Int = 0,
    
    // Food type classification
    @ColumnInfo(name = "food_category")
    val foodCategory: String?, // "restaurant_meal", "home_cooked", "packaged_food", "alcohol", "dessert"
    
    @ColumnInfo(name = "cuisine_type")
    val cuisineType: String?, // "italian", "mexican", "asian", "american", etc.
    
    @ColumnInfo(name = "meal_complexity")
    val mealComplexity: String?, // "simple", "moderate", "complex"
    
    // Quality and health metrics
    @ColumnInfo(name = "estimated_quality_score")
    val estimatedQualityScore: Float?, // 1-10 user rating of food quality/healthiness
    
    @ColumnInfo(name = "contains_alcohol")
    val containsAlcohol: Boolean = false,
    
    @ColumnInfo(name = "is_cheat_meal")
    val isCheatMeal: Boolean = false,
    
    @ColumnInfo(name = "is_comfort_food")
    val isComfortFood: Boolean = false,
    
    // MacroFactor-inspired adaptive features
    @ColumnInfo(name = "contributes_to_tdee")
    val contributesToTdee: Boolean = true, // Whether to include in adaptive TDEE calculations
    
    @ColumnInfo(name = "pre_workout_window")
    val preWorkoutWindow: Boolean = false,
    
    @ColumnInfo(name = "post_workout_window")
    val postWorkoutWindow: Boolean = false,
    
    // Cost tracking
    @ColumnInfo(name = "estimated_cost")
    val estimatedCost: Float?,
    
    @ColumnInfo(name = "cost_per_calorie")
    val costPerCalorie: Float?,
    
    // Social and mood tracking
    @ColumnInfo(name = "mood_before")
    val moodBefore: String?, // "hungry", "stressed", "happy", "tired", etc.
    
    @ColumnInfo(name = "mood_after")
    val moodAfter: String?, // "satisfied", "guilty", "energized", "sluggish", etc.
    
    @ColumnInfo(name = "hunger_before")
    val hungerBefore: Int?, // 1-10 scale
    
    @ColumnInfo(name = "hunger_after")
    val hungerAfter: Int?, // 1-10 scale
    
    @ColumnInfo(name = "satisfaction_rating")
    val satisfactionRating: Float?, // 1-5 rating of meal satisfaction
    
    // Images and documentation
    @ColumnInfo(name = "photo_url")
    val photoUrl: String?,
    
    @ColumnInfo(name = "receipt_photo_url")
    val receiptPhotoUrl: String?,
    
    @ColumnInfo(name = "menu_photo_url")
    val menuPhotoUrl: String?,
    
    // Follow-up tracking
    @ColumnInfo(name = "would_eat_again")
    val wouldEatAgain: Boolean?,
    
    @ColumnInfo(name = "recommend_to_others")
    val recommendToOthers: Boolean?,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    // Refinement and learning
    @ColumnInfo(name = "was_accurate_estimate")
    val wasAccurateEstimate: Boolean?, // User reflection on estimate accuracy
    
    @ColumnInfo(name = "actual_calories")
    val actualCalories: Float?, // If user finds out actual values later
    
    @ColumnInfo(name = "refinement_notes")
    val refinementNotes: String?, // What user learned for future estimates
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)