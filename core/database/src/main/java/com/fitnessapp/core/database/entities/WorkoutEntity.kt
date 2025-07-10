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
 * Workout entity for comprehensive workout tracking.
 * 
 * Inspired by Strong app's approach to workout logging with detailed
 * session tracking, performance metrics, and progression analysis.
 * 
 * Strong-inspired features:
 * - Session-based workout tracking
 * - Rest timer integration
 * - Volume and intensity calculations
 * - Workout template support
 * - Progression tracking
 */
@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routine_id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["date"]),
        Index(value = ["workout_type"]),
        Index(value = ["routine_id"]),
        Index(value = ["is_completed"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite index for user's daily workouts
    ]
)
data class WorkoutEntity(
    @PrimaryKey
    @ColumnInfo(name = "workout_id")
    val workoutId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "routine_id")
    val routineId: String?, // Link to workout routine/template
    
    // Basic workout information
    @ColumnInfo(name = "workout_name")
    val workoutName: String,
    
    @ColumnInfo(name = "workout_type")
    val workoutType: String, // "strength", "cardio", "hiit", "yoga", "mobility", "sports", "hybrid"
    
    @ColumnInfo(name = "focus_areas")
    val focusAreas: String?, // JSON array: "chest", "back", "legs", "shoulders", "arms", "core"
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "planned_start_time")
    val plannedStartTime: LocalTime?,
    
    @ColumnInfo(name = "actual_start_time")
    val actualStartTime: LocalTime?,
    
    @ColumnInfo(name = "actual_end_time")
    val actualEndTime: LocalTime?,
    
    // Duration tracking (Strong-inspired)
    @ColumnInfo(name = "planned_duration_minutes")
    val plannedDurationMinutes: Int?,
    
    @ColumnInfo(name = "actual_duration_minutes")
    val actualDurationMinutes: Int?,
    
    @ColumnInfo(name = "rest_time_minutes")
    val restTimeMinutes: Int?, // Total rest time during workout
    
    @ColumnInfo(name = "active_time_minutes")
    val activeTimeMinutes: Int?, // Time actually exercising
    
    // Workout completion and quality
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completion_percentage")
    val completionPercentage: Float = 0.0f, // How much of planned workout was completed
    
    @ColumnInfo(name = "workout_quality")
    val workoutQuality: String?, // "excellent", "good", "average", "poor"
    
    @ColumnInfo(name = "energy_level_before")
    val energyLevelBefore: Int?, // 1-10 scale
    
    @ColumnInfo(name = "energy_level_after")
    val energyLevelAfter: Int?, // 1-10 scale
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 scale
    
    // Volume and intensity metrics (Strong-inspired)
    @ColumnInfo(name = "total_volume_kg")
    val totalVolumeKg: Float?, // Total weight moved (sets × reps × weight)
    
    @ColumnInfo(name = "total_reps")
    val totalReps: Int?,
    
    @ColumnInfo(name = "total_sets")
    val totalSets: Int?,
    
    @ColumnInfo(name = "average_intensity")
    val averageIntensity: Float?, // Average percentage of 1RM used
    
    @ColumnInfo(name = "peak_intensity")
    val peakIntensity: Float?, // Highest percentage of 1RM used
    
    @ColumnInfo(name = "volume_load_index")
    val volumeLoadIndex: Float?, // Calculated volume metric
    
    // Personal records and achievements
    @ColumnInfo(name = "personal_records_count")
    val personalRecordsCount: Int = 0,
    
    @ColumnInfo(name = "personal_records_json")
    val personalRecordsJson: String?, // JSON array of PRs achieved
    
    @ColumnInfo(name = "volume_pr")
    val volumePr: Boolean = false,
    
    @ColumnInfo(name = "duration_pr")
    val durationPr: Boolean = false,
    
    // Environmental and context factors
    @ColumnInfo(name = "location")
    val location: String?, // "gym_name", "home", "outdoor", "hotel_gym", etc.
    
    @ColumnInfo(name = "gym_crowdedness")
    val gymCrowdedness: String?, // "empty", "light", "moderate", "busy", "very_busy"
    
    @ColumnInfo(name = "equipment_availability")
    val equipmentAvailability: String?, // "full", "limited", "minimal"
    
    @ColumnInfo(name = "training_partner")
    val trainingPartner: String?, // "alone", "with_partner", "with_group", "with_trainer"
    
    // Recovery and readiness
    @ColumnInfo(name = "sleep_hours_last_night")
    val sleepHoursLastNight: Float?,
    
    @ColumnInfo(name = "sleep_quality_last_night")
    val sleepQualityLastNight: Int?, // 1-10 scale
    
    @ColumnInfo(name = "stress_level")
    val stressLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "muscle_soreness_level")
    val muscleSorenessLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "readiness_score")
    val readinessScore: Int?, // 1-10 calculated readiness
    
    // Nutrition context
    @ColumnInfo(name = "pre_workout_nutrition")
    val preWorkoutNutrition: Boolean = false,
    
    @ColumnInfo(name = "pre_workout_nutrition_timing")
    val preWorkoutNutritionTiming: Int?, // Minutes before workout
    
    @ColumnInfo(name = "hydration_level")
    val hydrationLevel: String?, // "well_hydrated", "adequate", "dehydrated"
    
    @ColumnInfo(name = "caffeine_intake")
    val caffeineIntake: Boolean = false,
    
    @ColumnInfo(name = "supplements_taken")
    val supplementsTaken: String?, // JSON array of supplements
    
    // Weather and environment (for outdoor workouts)
    @ColumnInfo(name = "weather_conditions")
    val weatherConditions: String?, // "sunny", "cloudy", "rainy", "hot", "cold", "humid"
    
    @ColumnInfo(name = "temperature_celsius")
    val temperatureCelsius: Float?,
    
    @ColumnInfo(name = "humidity_percentage")
    val humidityPercentage: Float?,
    
    // Workout structure and methodology
    @ColumnInfo(name = "warmup_duration_minutes")
    val warmupDurationMinutes: Int?,
    
    @ColumnInfo(name = "cooldown_duration_minutes")
    val cooldownDurationMinutes: Int?,
    
    @ColumnInfo(name = "training_style")
    val trainingStyle: String?, // "powerlifting", "bodybuilding", "crossfit", "calisthenics", "functional"
    
    @ColumnInfo(name = "rep_ranges_focus")
    val repRangesFocus: String?, // "strength", "hypertrophy", "endurance", "power", "mixed"
    
    // Progress tracking
    @ColumnInfo(name = "progression_from_last")
    val progressionFromLast: String?, // "improved", "maintained", "declined"
    
    @ColumnInfo(name = "volume_change_percentage")
    val volumeChangePercentage: Float?, // Change from previous similar workout
    
    @ColumnInfo(name = "intensity_change_percentage")
    val intensityChangePercentage: Float?,
    
    // Recovery recommendations
    @ColumnInfo(name = "recommended_rest_days")
    val recommendedRestDays: Int?,
    
    @ColumnInfo(name = "predicted_soreness_level")
    val predictedSorenessLevel: Int?, // AI prediction based on volume/intensity
    
    // Social and sharing
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "shared_with_trainer")
    val sharedWithTrainer: Boolean = false,
    
    @ColumnInfo(name = "workout_rating")
    val workoutRating: Float?, // 1-5 star rating
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "coach_feedback")
    val coachFeedback: String?,
    
    // Media
    @ColumnInfo(name = "workout_photo_urls")
    val workoutPhotoUrls: String?, // JSON array of photo URLs
    
    @ColumnInfo(name = "form_video_urls")
    val formVideoUrls: String?, // JSON array of form check videos
    
    // Administrative
    @ColumnInfo(name = "synced_to_wearable")
    val syncedToWearable: Boolean = false,
    
    @ColumnInfo(name = "heart_rate_data_available")
    val heartRateDataAvailable: Boolean = false,
    
    @ColumnInfo(name = "calories_burned")
    val caloriesBurned: Float?, // From wearable or calculation
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "started_at")
    val startedAt: LocalDateTime?,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime?
)