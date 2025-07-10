package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * User goals entity for tracking fitness objectives and targets.
 * 
 * Supports multiple concurrent goals with progress tracking,
 * deadlines, and adaptive adjustments based on user progress.
 * 
 * MacroFactor-inspired adaptive features:
 * - Dynamic calorie target adjustments
 * - Metabolic adaptation considerations
 * - Evidence-based goal setting
 */
@Entity(
    tableName = "user_goals",
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
        Index(value = ["goal_type"]),
        Index(value = ["is_active"]),
        Index(value = ["target_date"]),
        Index(value = ["created_at"])
    ]
)
data class UserGoalsEntity(
    @PrimaryKey
    @ColumnInfo(name = "goal_id")
    val goalId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "goal_type")
    val goalType: String, // "weight_loss", "weight_gain", "muscle_gain", "strength", "endurance", "body_recomposition", "maintenance"
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "priority")
    val priority: String = "medium", // "low", "medium", "high"
    
    // Weight-related goals
    @ColumnInfo(name = "target_weight_kg")
    val targetWeightKg: Float?,
    
    @ColumnInfo(name = "starting_weight_kg")
    val startingWeightKg: Float?,
    
    @ColumnInfo(name = "current_weight_kg")
    val currentWeightKg: Float?,
    
    @ColumnInfo(name = "weekly_weight_change_goal_kg")
    val weeklyWeightChangeGoalKg: Float?, // Positive for gain, negative for loss
    
    // Body composition goals
    @ColumnInfo(name = "target_body_fat_percentage")
    val targetBodyFatPercentage: Float?,
    
    @ColumnInfo(name = "target_muscle_mass_kg")
    val targetMuscleMassKg: Float?,
    
    // Strength goals
    @ColumnInfo(name = "target_squat_1rm_kg")
    val targetSquat1rmKg: Float?,
    
    @ColumnInfo(name = "target_bench_1rm_kg")
    val targetBench1rmKg: Float?,
    
    @ColumnInfo(name = "target_deadlift_1rm_kg")
    val targetDeadlift1rmKg: Float?,
    
    @ColumnInfo(name = "target_overhead_press_1rm_kg")
    val targetOverheadPress1rmKg: Float?,
    
    // Performance goals
    @ColumnInfo(name = "target_5k_time_seconds")
    val target5kTimeSeconds: Int?,
    
    @ColumnInfo(name = "target_10k_time_seconds")
    val target10kTimeSeconds: Int?,
    
    @ColumnInfo(name = "target_marathon_time_seconds")
    val targetMarathonTimeSeconds: Int?,
    
    // Activity goals
    @ColumnInfo(name = "weekly_workout_frequency")
    val weeklyWorkoutFrequency: Int?,
    
    @ColumnInfo(name = "weekly_cardio_minutes")
    val weeklyCardioMinutes: Int?,
    
    @ColumnInfo(name = "daily_steps_target")
    val dailyStepsTarget: Int?,
    
    @ColumnInfo(name = "weekly_active_minutes")
    val weeklyActiveMinutes: Int?,
    
    // Nutrition goals (MacroFactor-inspired)
    @ColumnInfo(name = "daily_calorie_target")
    val dailyCalorieTarget: Int?,
    
    @ColumnInfo(name = "protein_target_grams")
    val proteinTargetGrams: Float?,
    
    @ColumnInfo(name = "carb_target_grams")
    val carbTargetGrams: Float?,
    
    @ColumnInfo(name = "fat_target_grams")
    val fatTargetGrams: Float?,
    
    @ColumnInfo(name = "fiber_target_grams")
    val fiberTargetGrams: Float?,
    
    @ColumnInfo(name = "water_target_ml")
    val waterTargetMl: Int?,
    
    // Adaptive calorie adjustment (MacroFactor-inspired)
    @ColumnInfo(name = "adaptive_calories_enabled")
    val adaptiveCaloriesEnabled: Boolean = true,
    
    @ColumnInfo(name = "metabolic_adaptation_factor")
    val metabolicAdaptationFactor: Float = 1.0f, // Multiplier for TDEE adjustments
    
    @ColumnInfo(name = "last_calorie_adjustment_date")
    val lastCalorieAdjustmentDate: LocalDate?,
    
    @ColumnInfo(name = "calorie_adjustment_frequency_days")
    val calorieAdjustmentFrequencyDays: Int = 7,
    
    // Timeline
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    
    @ColumnInfo(name = "target_date")
    val targetDate: LocalDate?,
    
    @ColumnInfo(name = "estimated_completion_date")
    val estimatedCompletionDate: LocalDate?, // AI-calculated based on current progress
    
    // Progress tracking
    @ColumnInfo(name = "progress_percentage")
    val progressPercentage: Float = 0.0f,
    
    @ColumnInfo(name = "last_progress_update")
    val lastProgressUpdate: LocalDateTime?,
    
    @ColumnInfo(name = "milestone_count")
    val milestoneCount: Int = 0,
    
    @ColumnInfo(name = "milestones_achieved")
    val milestonesAchieved: Int = 0,
    
    // Motivation and tracking
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: String?, // "low", "medium", "high"
    
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String?, // "easy", "moderate", "challenging", "very_challenging"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String?, // "low", "medium", "high"
    
    // Completion status
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime?,
    
    @ColumnInfo(name = "completion_notes")
    val completionNotes: String?,
    
    // Adjustment history
    @ColumnInfo(name = "adjustment_count")
    val adjustmentCount: Int = 0,
    
    @ColumnInfo(name = "last_adjustment_reason")
    val lastAdjustmentReason: String?,
    
    @ColumnInfo(name = "last_adjustment_date")
    val lastAdjustmentDate: LocalDateTime?,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)