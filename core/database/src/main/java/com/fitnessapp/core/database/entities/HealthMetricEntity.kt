package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Health metrics entity for comprehensive health tracking.
 * 
 * Stores various health-related metrics including vital signs,
 * sleep data, stress levels, and other wellness indicators.
 * Designed for integration with health platforms and wearables.
 * 
 * Features:
 * - Comprehensive vital signs tracking
 * - Sleep and recovery metrics
 * - Stress and wellness indicators
 * - Health app integration support
 */
@Entity(
    tableName = "health_metrics",
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
        Index(value = ["metric_type"]),
        Index(value = ["data_source"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date", "metric_type"]) // Composite for specific metric queries
    ]
)
data class HealthMetricEntity(
    @PrimaryKey
    @ColumnInfo(name = "health_metric_id")
    val healthMetricId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic metric information
    @ColumnInfo(name = "metric_type")
    val metricType: String, // "heart_rate", "blood_pressure", "sleep", "stress", "recovery", "temperature"
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "metric_value")
    val metricValue: Float?,
    
    @ColumnInfo(name = "metric_unit")
    val metricUnit: String?, // "bpm", "mmHg", "hours", "percentage", "celsius"
    
    // Cardiovascular metrics
    @ColumnInfo(name = "resting_heart_rate")
    val restingHeartRate: Int?, // BPM
    
    @ColumnInfo(name = "max_heart_rate")
    val maxHeartRate: Int?, // BPM
    
    @ColumnInfo(name = "average_heart_rate")
    val averageHeartRate: Int?, // BPM for the day
    
    @ColumnInfo(name = "heart_rate_variability")
    val heartRateVariability: Float?, // HRV in milliseconds
    
    @ColumnInfo(name = "systolic_bp")
    val systolicBp: Int?, // mmHg
    
    @ColumnInfo(name = "diastolic_bp")
    val diastolicBp: Int?, // mmHg
    
    @ColumnInfo(name = "pulse_pressure")
    val pulsePressure: Int?, // Systolic - Diastolic
    
    @ColumnInfo(name = "mean_arterial_pressure")
    val meanArterialPressure: Float?, // Calculated MAP
    
    // Sleep metrics
    @ColumnInfo(name = "sleep_duration_hours")
    val sleepDurationHours: Float?,
    
    @ColumnInfo(name = "sleep_efficiency_percentage")
    val sleepEfficiencyPercentage: Float?, // Time asleep / time in bed
    
    @ColumnInfo(name = "deep_sleep_hours")
    val deepSleepHours: Float?,
    
    @ColumnInfo(name = "rem_sleep_hours")
    val remSleepHours: Float?,
    
    @ColumnInfo(name = "light_sleep_hours")
    val lightSleepHours: Float?,
    
    @ColumnInfo(name = "awake_time_hours")
    val awakeTimeHours: Float?, // Time awake during sleep period
    
    @ColumnInfo(name = "sleep_onset_minutes")
    val sleepOnsetMinutes: Int?, // Time to fall asleep
    
    @ColumnInfo(name = "wake_up_count")
    val wakeUpCount: Int?, // Number of times woken up
    
    @ColumnInfo(name = "sleep_score")
    val sleepScore: Float?, // Overall sleep quality score (0-100)
    
    @ColumnInfo(name = "bedtime")
    val bedtime: LocalDateTime?,
    
    @ColumnInfo(name = "wake_time")
    val wakeTime: LocalDateTime?,
    
    // Recovery and wellness metrics
    @ColumnInfo(name = "recovery_score")
    val recoveryScore: Float?, // 0-100 recovery score
    
    @ColumnInfo(name = "readiness_score")
    val readinessScore: Float?, // 0-100 readiness for training
    
    @ColumnInfo(name = "stress_score")
    val stressScore: Float?, // 0-100 stress level
    
    @ColumnInfo(name = "energy_level")
    val energyLevel: Int?, // 1-10 subjective energy
    
    @ColumnInfo(name = "mood_score")
    val moodScore: Int?, // 1-10 mood rating
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 motivation rating
    
    @ColumnInfo(name = "perceived_stress")
    val perceivedStress: Int?, // 1-10 subjective stress
    
    // Physical condition metrics
    @ColumnInfo(name = "body_temperature_celsius")
    val bodyTemperatureCelsius: Float?,
    
    @ColumnInfo(name = "muscle_soreness_level")
    val muscleSorenessLevel: Int?, // 1-10 overall soreness
    
    @ColumnInfo(name = "muscle_soreness_areas")
    val muscleSorenessAreas: String?, // JSON array of sore muscle groups
    
    @ColumnInfo(name = "joint_stiffness_level")
    val jointStiffnessLevel: Int?, // 1-10 overall stiffness
    
    @ColumnInfo(name = "fatigue_level")
    val fatigueLevel: Int?, // 1-10 fatigue level
    
    @ColumnInfo(name = "hydration_level")
    val hydrationLevel: String?, // "dehydrated", "normal", "well_hydrated"
    
    // Respiratory metrics
    @ColumnInfo(name = "respiratory_rate")
    val respiratoryRate: Int?, // Breaths per minute
    
    @ColumnInfo(name = "oxygen_saturation")
    val oxygenSaturation: Float?, // SpO2 percentage
    
    @ColumnInfo(name = "vo2_max")
    val vo2Max: Float?, // VO2 Max estimation
    
    @ColumnInfo(name = "lung_capacity")
    val lungCapacity: Float?, // If measured
    
    // Activity and movement metrics
    @ColumnInfo(name = "steps_count")
    val stepsCount: Int?,
    
    @ColumnInfo(name = "active_minutes")
    val activeMinutes: Int?,
    
    @ColumnInfo(name = "sedentary_minutes")
    val sedentaryMinutes: Int?,
    
    @ColumnInfo(name = "calories_burned")
    val caloriesBurned: Float?,
    
    @ColumnInfo(name = "distance_km")
    val distanceKm: Float?,
    
    @ColumnInfo(name = "floors_climbed")
    val floorsClimbed: Int?,
    
    @ColumnInfo(name = "activity_score")
    val activityScore: Float?, // 0-100 daily activity score
    
    // Mental health and cognitive metrics
    @ColumnInfo(name = "anxiety_level")
    val anxietyLevel: Int?, // 1-10 anxiety rating
    
    @ColumnInfo(name = "focus_level")
    val focusLevel: Int?, // 1-10 focus/concentration
    
    @ColumnInfo(name = "cognitive_function")
    val cognitiveFunction: Int?, // 1-10 cognitive clarity
    
    @ColumnInfo(name = "mental_clarity")
    val mentalClarity: Int?, // 1-10 mental clarity
    
    @ColumnInfo(name = "meditation_minutes")
    val meditationMinutes: Int?,
    
    @ColumnInfo(name = "mindfulness_score")
    val mindfulnessScore: Float?, // If using mindfulness apps
    
    // Environmental and lifestyle factors
    @ColumnInfo(name = "caffeine_intake_mg")
    val caffeineIntakeMg: Float?,
    
    @ColumnInfo(name = "alcohol_units")
    val alcoholUnits: Float?,
    
    @ColumnInfo(name = "water_intake_ml")
    val waterIntakeMl: Float?,
    
    @ColumnInfo(name = "screen_time_hours")
    val screenTimeHours: Float?,
    
    @ColumnInfo(name = "outdoor_time_hours")
    val outdoorTimeHours: Float?,
    
    @ColumnInfo(name = "social_interaction_hours")
    val socialInteractionHours: Float?,
    
    // Women's health metrics
    @ColumnInfo(name = "menstrual_cycle_day")
    val menstrualCycleDay: Int?,
    
    @ColumnInfo(name = "menstrual_flow")
    val menstrualFlow: String?, // "none", "light", "medium", "heavy"
    
    @ColumnInfo(name = "pms_symptoms")
    val pmsSymptoms: String?, // JSON array of symptoms
    
    @ColumnInfo(name = "ovulation_day")
    val ovulationDay: Boolean = false,
    
    // Data source and quality
    @ColumnInfo(name = "data_source")
    val dataSource: String, // "manual", "apple_health", "google_fit", "fitbit", "garmin", "whoop", "oura"
    
    @ColumnInfo(name = "device_name")
    val deviceName: String?, // Name of device/app that recorded data
    
    @ColumnInfo(name = "device_id")
    val deviceId: String?,
    
    @ColumnInfo(name = "data_quality")
    val dataQuality: String, // "high", "medium", "low"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: Float = 1.0f, // 0.0-1.0 confidence in data
    
    @ColumnInfo(name = "is_estimated")
    val isEstimated: Boolean = false, // Whether value is estimated vs measured
    
    // Contextual information
    @ColumnInfo(name = "measurement_context")
    val measurementContext: String?, // "rest", "post_workout", "morning", "evening"
    
    @ColumnInfo(name = "location")
    val location: String?, // "home", "gym", "office", "outdoor"
    
    @ColumnInfo(name = "weather_conditions")
    val weatherConditions: String?, // For outdoor activities
    
    @ColumnInfo(name = "illness_status")
    val illnessStatus: String?, // "healthy", "minor_illness", "recovering", "sick"
    
    @ColumnInfo(name = "medication_taken")
    val medicationTaken: Boolean = false,
    
    @ColumnInfo(name = "supplements_taken")
    val supplementsTaken: String?, // JSON array of supplements
    
    // Trends and analysis
    @ColumnInfo(name = "trend_direction")
    val trendDirection: String?, // "improving", "stable", "declining"
    
    @ColumnInfo(name = "change_from_baseline")
    val changeFromBaseline: Float?, // Change from personal baseline
    
    @ColumnInfo(name = "percentile_rank")
    val percentileRank: Float?, // Where this falls in user's historical data
    
    @ColumnInfo(name = "is_outlier")
    val isOutlier: Boolean = false,
    
    @ColumnInfo(name = "normal_range_min")
    val normalRangeMin: Float?, // Clinical normal range minimum
    
    @ColumnInfo(name = "normal_range_max")
    val normalRangeMax: Float?, // Clinical normal range maximum
    
    // Notes and observations
    @ColumnInfo(name = "user_notes")
    val userNotes: String?,
    
    @ColumnInfo(name = "symptoms")
    val symptoms: String?, // JSON array of reported symptoms
    
    @ColumnInfo(name = "interventions")
    val interventions: String?, // JSON array of actions taken
    
    @ColumnInfo(name = "external_factors")
    val externalFactors: String?, // JSON array of external influences
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "measured_at")
    val measuredAt: LocalDateTime?, // Exact measurement timestamp
    
    @ColumnInfo(name = "synced_at")
    val syncedAt: LocalDateTime? // When synced from external source
)