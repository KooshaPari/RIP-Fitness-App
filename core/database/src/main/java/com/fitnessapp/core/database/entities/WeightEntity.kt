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
 * Weight tracking entity for comprehensive weight management.
 * 
 * Inspired by MacroFactor's approach to weight tracking with
 * emphasis on trends, data quality, and adaptive calculations.
 * 
 * MacroFactor-inspired features:
 * - Multiple daily weigh-ins support
 * - Data quality assessment
 * - Trend calculation integration
 * - Context-aware weight tracking
 * - Outlier detection and handling
 */
@Entity(
    tableName = "weight_entries",
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
        Index(value = ["weigh_time"]),
        Index(value = ["is_outlier"]),
        Index(value = ["data_quality"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite for daily weight queries
    ]
)
data class WeightEntity(
    @PrimaryKey
    @ColumnInfo(name = "weight_id")
    val weightId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic weight information
    @ColumnInfo(name = "weight_kg")
    val weightKg: Float,
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "weigh_time")
    val weighTime: LocalTime?,
    
    // Data source and context
    @ColumnInfo(name = "data_source")
    val dataSource: String, // "manual", "smart_scale", "health_app", "imported"
    
    @ColumnInfo(name = "device_name")
    val deviceName: String?, // Name of the scale or device used
    
    @ColumnInfo(name = "device_id")
    val deviceId: String?, // Unique device identifier
    
    @ColumnInfo(name = "sync_source")
    val syncSource: String?, // "fitbit", "withings", "garmin", "manual"
    
    // MacroFactor-inspired data quality assessment
    @ColumnInfo(name = "data_quality")
    val dataQuality: String, // "high", "medium", "low", "questionable"
    
    @ColumnInfo(name = "confidence_score")
    val confidenceScore: Float = 1.0f, // 0.0-1.0 confidence in this measurement
    
    @ColumnInfo(name = "is_outlier")
    val isOutlier: Boolean = false, // Flagged as statistical outlier
    
    @ColumnInfo(name = "outlier_reason")
    val outlierReason: String?, // "large_deviation", "timing", "context", "manual_flag"
    
    @ColumnInfo(name = "is_manually_verified")
    val isManuallyVerified: Boolean = false, // User confirmed this reading
    
    // Contextual factors affecting weight
    @ColumnInfo(name = "weighing_conditions")
    val weighingConditions: String?, // "fasted", "post_meal", "post_workout", "clothed", "unclothed"
    
    @ColumnInfo(name = "clothing_weight_kg")
    val clothingWeightKg: Float?, // Estimated weight of clothing
    
    @ColumnInfo(name = "hydration_status")
    val hydrationStatus: String?, // "well_hydrated", "dehydrated", "overhydrated", "normal"
    
    @ColumnInfo(name = "meal_timing")
    val mealTiming: String?, // "fasted", "1h_post_meal", "2h_post_meal", "recently_ate"
    
    @ColumnInfo(name = "bathroom_visit")
    val bathroomVisit: Boolean?, // Whether user used bathroom before weighing
    
    @ColumnInfo(name = "sleep_hours")
    val sleepHours: Float?, // Hours of sleep the night before
    
    @ColumnInfo(name = "sleep_quality")
    val sleepQuality: String?, // "poor", "fair", "good", "excellent"
    
    // Physiological context
    @ColumnInfo(name = "menstrual_cycle_day")
    val menstrualCycleDay: Int?, // Day of cycle for women (affects water retention)
    
    @ColumnInfo(name = "stress_level")
    val stressLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "illness_status")
    val illnessStatus: String?, // "healthy", "minor_illness", "recovering", "medication"
    
    @ColumnInfo(name = "workout_yesterday")
    val workoutYesterday: Boolean?, // Did user work out yesterday
    
    @ColumnInfo(name = "workout_intensity_yesterday")
    val workoutIntensityYesterday: String?, // "light", "moderate", "intense"
    
    @ColumnInfo(name = "alcohol_consumption_yesterday")
    val alcoholConsumptionYesterday: Boolean?,
    
    @ColumnInfo(name = "sodium_intake_yesterday")
    val sodiumIntakeYesterday: String?, // "low", "normal", "high", "very_high"
    
    // Calculated and derived metrics
    @ColumnInfo(name = "trend_weight_kg")
    val trendWeightKg: Float?, // Moving average/trend calculation
    
    @ColumnInfo(name = "weight_change_from_previous")
    val weightChangeFromPrevious: Float?, // Change from last measurement
    
    @ColumnInfo(name = "weight_change_7_day")
    val weightChange7Day: Float?, // Change from 7 days ago
    
    @ColumnInfo(name = "weight_change_30_day")
    val weightChange30Day: Float?, // Change from 30 days ago
    
    @ColumnInfo(name = "distance_from_trend")
    val distanceFromTrend: Float?, // How far from trend line
    
    @ColumnInfo(name = "percentile_rank")
    val percentileRank: Float?, // Where this falls in user's historical data
    
    // Goal and progress context
    @ColumnInfo(name = "distance_from_goal_kg")
    val distanceFromGoalKg: Float?, // How far from current weight goal
    
    @ColumnInfo(name = "goal_progress_percentage")
    val goalProgressPercentage: Float?, // Progress toward goal (0-100%)
    
    @ColumnInfo(name = "milestone_reached")
    val milestoneReached: String?, // "5kg_lost", "goal_weight", "halfway", etc.
    
    // Body composition estimates (if available from smart scale)
    @ColumnInfo(name = "body_fat_percentage")
    val bodyFatPercentage: Float?,
    
    @ColumnInfo(name = "muscle_mass_kg")
    val muscleMassKg: Float?,
    
    @ColumnInfo(name = "bone_mass_kg")
    val boneMassKg: Float?,
    
    @ColumnInfo(name = "body_water_percentage")
    val bodyWaterPercentage: Float?,
    
    @ColumnInfo(name = "visceral_fat_rating")
    val visceralFatRating: Float?,
    
    @ColumnInfo(name = "metabolic_age")
    val metabolicAge: Int?,
    
    @ColumnInfo(name = "bmi")
    val bmi: Float?,
    
    // Data quality indicators for body composition
    @ColumnInfo(name = "body_comp_reliability")
    val bodyCompReliability: String?, // "high", "medium", "low" - reliability of body comp data
    
    @ColumnInfo(name = "impedance_measurement")
    val impedanceMeasurement: Boolean = false, // Whether bioelectrical impedance was used
    
    // Notes and observations
    @ColumnInfo(name = "user_notes")
    val userNotes: String?,
    
    @ColumnInfo(name = "energy_level")
    val energyLevel: Int?, // 1-10 scale how user felt
    
    @ColumnInfo(name = "mood")
    val mood: String?, // "happy", "motivated", "frustrated", "neutral"
    
    @ColumnInfo(name = "satisfaction_with_progress")
    val satisfactionWithProgress: Int?, // 1-10 scale
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 scale
    
    // Environmental factors
    @ColumnInfo(name = "scale_location")
    val scaleLocation: String?, // "bathroom", "bedroom", "gym" (for consistency)
    
    @ColumnInfo(name = "room_temperature")
    val roomTemperature: Float?, // Can affect electronic scales
    
    @ColumnInfo(name = "scale_calibration_date")
    val scaleCalibrationDate: LocalDate?, // When scale was last calibrated
    
    // MacroFactor adaptive calculation context
    @ColumnInfo(name = "includes_in_tdee_calculation")
    val includesInTdeeCalculation: Boolean = true, // Whether to use for TDEE calculation
    
    @ColumnInfo(name = "adaptive_weight_smoothing")
    val adaptiveWeightSmoothing: Float?, // Smoothing factor applied
    
    @ColumnInfo(name = "trend_calculation_method")
    val trendCalculationMethod: String?, // "simple_moving_average", "exponential_smoothing", "adaptive"
    
    // Historical comparison
    @ColumnInfo(name = "same_day_last_week_kg")
    val sameDayLastWeekKg: Float?, // Weight on same day of week last week
    
    @ColumnInfo(name = "same_day_last_month_kg")
    val sameDayLastMonthKg: Float?, // Weight on same day last month
    
    @ColumnInfo(name = "lowest_weight_kg")
    val lowestWeightKg: Float?, // Lowest weight ever recorded
    
    @ColumnInfo(name = "highest_weight_kg")
    val highestWeightKg: Float?, // Highest weight ever recorded
    
    // Validation and correction
    @ColumnInfo(name = "was_corrected")
    val wasCorrected: Boolean = false, // Whether this entry was later corrected
    
    @ColumnInfo(name = "corrected_from_kg")
    val correctedFromKg: Float?, // Original value if corrected
    
    @ColumnInfo(name = "correction_reason")
    val correctionReason: String?, // Why it was corrected
    
    @ColumnInfo(name = "approved_by_user")
    val approvedByUser: Boolean = true, // User approved this measurement
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "measured_at")
    val measuredAt: LocalDateTime?, // Exact timestamp of measurement
    
    @ColumnInfo(name = "synced_at")
    val syncedAt: LocalDateTime? // When data was synced from external source
)