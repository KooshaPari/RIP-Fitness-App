package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * User preferences entity for personalized app experience.
 * 
 * Stores all user customization settings, notification preferences,
 * and app behavior configurations.
 * 
 * MacroFactor-inspired features:
 * - Detailed nutrition tracking preferences
 * - Adaptive calculation settings
 * - Smart coaching preferences
 */
@Entity(
    tableName = "user_preferences",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"], unique = true),
        Index(value = ["updated_at"])
    ]
)
data class UserPreferencesEntity(
    @PrimaryKey
    @ColumnInfo(name = "preference_id")
    val preferenceId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Unit preferences
    @ColumnInfo(name = "weight_unit")
    val weightUnit: String = "kg", // "kg", "lbs"
    
    @ColumnInfo(name = "distance_unit")
    val distanceUnit: String = "km", // "km", "miles"
    
    @ColumnInfo(name = "temperature_unit")
    val temperatureUnit: String = "celsius", // "celsius", "fahrenheit"
    
    @ColumnInfo(name = "first_day_of_week")
    val firstDayOfWeek: String = "monday", // "sunday", "monday"
    
    // Nutrition preferences (MacroFactor-inspired)
    @ColumnInfo(name = "calorie_tracking_enabled")
    val calorieTrackingEnabled: Boolean = true,
    
    @ColumnInfo(name = "macro_tracking_enabled")
    val macroTrackingEnabled: Boolean = true,
    
    @ColumnInfo(name = "micronutrient_tracking")
    val micronutrientTracking: Boolean = false,
    
    @ColumnInfo(name = "barcode_scanning_enabled")
    val barcodeScanningEnabled: Boolean = true,
    
    @ColumnInfo(name = "restaurant_tracking_enabled")
    val restaurantTrackingEnabled: Boolean = true,
    
    @ColumnInfo(name = "quick_add_favorites")
    val quickAddFavorites: Boolean = true,
    
    // Adaptive calculation preferences
    @ColumnInfo(name = "adaptive_tdee_enabled")
    val adaptiveTdeeEnabled: Boolean = true,
    
    @ColumnInfo(name = "metabolic_adaptation_sensitivity")
    val metabolicAdaptationSensitivity: String = "medium", // "low", "medium", "high"
    
    @ColumnInfo(name = "weekly_weight_average")
    val weeklyWeightAverage: Boolean = true,
    
    @ColumnInfo(name = "expenditure_calculation_days")
    val expenditureCalculationDays: Int = 14, // Days to use for TDEE calculation
    
    // Workout preferences (Strong-inspired)
    @ColumnInfo(name = "rest_timer_enabled")
    val restTimerEnabled: Boolean = true,
    
    @ColumnInfo(name = "default_rest_time_seconds")
    val defaultRestTimeSeconds: Int = 120,
    
    @ColumnInfo(name = "plate_calculator_enabled")
    val plateCalculatorEnabled: Boolean = true,
    
    @ColumnInfo(name = "auto_start_timer")
    val autoStartTimer: Boolean = true,
    
    @ColumnInfo(name = "weight_increment_kg")
    val weightIncrementKg: Float = 2.5f,
    
    @ColumnInfo(name = "show_warmup_sets")
    val showWarmupSets: Boolean = true,
    
    @ColumnInfo(name = "exercise_instructions_enabled")
    val exerciseInstructionsEnabled: Boolean = true,
    
    // Notification preferences
    @ColumnInfo(name = "workout_reminders")
    val workoutReminders: Boolean = true,
    
    @ColumnInfo(name = "meal_reminders")
    val mealReminders: Boolean = false,
    
    @ColumnInfo(name = "progress_photo_reminders")
    val progressPhotoReminders: Boolean = false,
    
    @ColumnInfo(name = "weekly_check_in_reminders")
    val weeklyCheckInReminders: Boolean = true,
    
    @ColumnInfo(name = "achievement_notifications")
    val achievementNotifications: Boolean = true,
    
    @ColumnInfo(name = "social_notifications")
    val socialNotifications: Boolean = false,
    
    // Privacy preferences
    @ColumnInfo(name = "analytics_sharing")
    val analyticsSharing: Boolean = true,
    
    @ColumnInfo(name = "crash_reporting")
    val crashReporting: Boolean = true,
    
    @ColumnInfo(name = "workout_sharing_default")
    val workoutSharingDefault: String = "private", // "public", "friends", "private"
    
    // Display preferences
    @ColumnInfo(name = "dark_mode")
    val darkMode: String = "system", // "light", "dark", "system"
    
    @ColumnInfo(name = "compact_workout_view")
    val compactWorkoutView: Boolean = false,
    
    @ColumnInfo(name = "show_body_weight_exercises")
    val showBodyWeightExercises: Boolean = true,
    
    @ColumnInfo(name = "exercise_video_autoplay")
    val exerciseVideoAutoplay: Boolean = false,
    
    // Smart coaching preferences
    @ColumnInfo(name = "ai_coaching_enabled")
    val aiCoachingEnabled: Boolean = true,
    
    @ColumnInfo(name = "form_analysis_enabled")
    val formAnalysisEnabled: Boolean = false,
    
    @ColumnInfo(name = "auto_progression_suggestions")
    val autoProgressionSuggestions: Boolean = true,
    
    @ColumnInfo(name = "deload_week_suggestions")
    val deloadWeekSuggestions: Boolean = true,
    
    // Data sync preferences
    @ColumnInfo(name = "cloud_sync_enabled")
    val cloudSyncEnabled: Boolean = true,
    
    @ColumnInfo(name = "wifi_only_sync")
    val wifiOnlySync: Boolean = false,
    
    @ColumnInfo(name = "auto_backup_enabled")
    val autoBackupEnabled: Boolean = true,
    
    @ColumnInfo(name = "backup_frequency_days")
    val backupFrequencyDays: Int = 7,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)