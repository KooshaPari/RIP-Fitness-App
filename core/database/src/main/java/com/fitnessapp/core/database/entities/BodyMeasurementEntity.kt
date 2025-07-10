package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Body measurements entity for comprehensive body composition tracking.
 * 
 * Tracks various body measurements including circumferences, body fat,
 * and other metrics important for fitness progress monitoring.
 * 
 * Features:
 * - Multiple measurement types
 * - Progress tracking and trends
 * - Health integration compatibility
 * - Professional measurement support
 */
@Entity(
    tableName = "body_measurements",
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
        Index(value = ["measurement_type"]),
        Index(value = ["measurement_method"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite for daily measurement queries
    ]
)
data class BodyMeasurementEntity(
    @PrimaryKey
    @ColumnInfo(name = "measurement_id")
    val measurementId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic measurement information
    @ColumnInfo(name = "measurement_type")
    val measurementType: String, // "circumference", "body_fat", "muscle_mass", "bone_density", "flexibility"
    
    @ColumnInfo(name = "body_part")
    val bodyPart: String?, // "chest", "waist", "hips", "bicep", "thigh", "neck", "forearm", "calf"
    
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "measurement_value")
    val measurementValue: Float,
    
    @ColumnInfo(name = "measurement_unit")
    val measurementUnit: String, // "cm", "mm", "percentage", "kg", "lbs"
    
    // Circumference measurements (in cm)
    @ColumnInfo(name = "chest_cm")
    val chestCm: Float?,
    
    @ColumnInfo(name = "waist_cm")
    val waistCm: Float?,
    
    @ColumnInfo(name = "hips_cm")
    val hipsCm: Float?,
    
    @ColumnInfo(name = "bicep_left_cm")
    val bicepLeftCm: Float?,
    
    @ColumnInfo(name = "bicep_right_cm")
    val bicepRightCm: Float?,
    
    @ColumnInfo(name = "thigh_left_cm")
    val thighLeftCm: Float?,
    
    @ColumnInfo(name = "thigh_right_cm")
    val thighRightCm: Float?,
    
    @ColumnInfo(name = "neck_cm")
    val neckCm: Float?,
    
    @ColumnInfo(name = "forearm_left_cm")
    val forearmLeftCm: Float?,
    
    @ColumnInfo(name = "forearm_right_cm")
    val forearmRightCm: Float?,
    
    @ColumnInfo(name = "calf_left_cm")
    val calfLeftCm: Float?,
    
    @ColumnInfo(name = "calf_right_cm")
    val calfRightCm: Float?,
    
    @ColumnInfo(name = "shoulder_cm")
    val shoulderCm: Float?,
    
    // Body composition measurements
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
    
    @ColumnInfo(name = "lean_body_mass_kg")
    val leanBodyMassKg: Float?,
    
    @ColumnInfo(name = "fat_mass_kg")
    val fatMassKg: Float?,
    
    // Advanced body composition (DEXA/BodPod)
    @ColumnInfo(name = "bone_density_g_cm2")
    val boneDensityGCm2: Float?,
    
    @ColumnInfo(name = "t_score")
    val tScore: Float?, // Bone density T-score
    
    @ColumnInfo(name = "z_score")
    val zScore: Float?, // Bone density Z-score
    
    @ColumnInfo(name = "trunk_fat_percentage")
    val trunkFatPercentage: Float?,
    
    @ColumnInfo(name = "android_fat_percentage")
    val androidFatPercentage: Float?, // Apple-shaped fat distribution
    
    @ColumnInfo(name = "gynoid_fat_percentage")
    val gynoidFatPercentage: Float?, // Pear-shaped fat distribution
    
    @ColumnInfo(name = "android_gynoid_ratio")
    val androidGynoidRatio: Float?,
    
    // Measurement methodology
    @ColumnInfo(name = "measurement_method")
    val measurementMethod: String, // "tape_measure", "calipers", "dexa", "bodpod", "bioimpedance", "ultrasound"
    
    @ColumnInfo(name = "measured_by")
    val measuredBy: String?, // "self", "trainer", "medical_professional", "automated"
    
    @ColumnInfo(name = "measurement_location")
    val measurementLocation: String?, // "home", "gym", "clinic", "lab"
    
    @ColumnInfo(name = "device_used")
    val deviceUsed: String?, // Name/model of measuring device
    
    @ColumnInfo(name = "device_id")
    val deviceId: String?, // Unique device identifier
    
    // Measurement conditions
    @ColumnInfo(name = "measurement_time_of_day")
    val measurementTimeOfDay: String?, // "morning", "afternoon", "evening"
    
    @ColumnInfo(name = "fasting_status")
    val fastingStatus: Boolean?, // Whether measured while fasting
    
    @ColumnInfo(name = "hydration_status")
    val hydrationStatus: String?, // "well_hydrated", "dehydrated", "normal"
    
    @ColumnInfo(name = "pre_workout")
    val preWorkout: Boolean = false, // Measured before workout
    
    @ColumnInfo(name = "post_workout")
    val postWorkout: Boolean = false, // Measured after workout
    
    @ColumnInfo(name = "measurement_posture")
    val measurementPosture: String?, // "standing", "lying", "seated"
    
    @ColumnInfo(name = "breathing_state")
    val breathingState: String?, // "normal", "exhaled", "inhaled"
    
    // Data quality and reliability
    @ColumnInfo(name = "data_quality")
    val dataQuality: String, // "high", "medium", "low"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: Float = 1.0f, // 0.0-1.0 confidence in measurement
    
    @ColumnInfo(name = "measurement_precision")
    val measurementPrecision: Float?, // Precision of the measuring device (e.g., 0.1 cm)
    
    @ColumnInfo(name = "inter_measurement_variability")
    val interMeasurementVariability: Float?, // Variability between repeated measurements
    
    @ColumnInfo(name = "is_outlier")
    val isOutlier: Boolean = false,
    
    @ColumnInfo(name = "outlier_reason")
    val outlierReason: String?,
    
    // Progress tracking
    @ColumnInfo(name = "change_from_previous")
    val changeFromPrevious: Float?, // Change from last measurement of same type
    
    @ColumnInfo(name = "change_7_day")
    val change7Day: Float?, // Change from 7 days ago
    
    @ColumnInfo(name = "change_30_day")
    val change30Day: Float?, // Change from 30 days ago
    
    @ColumnInfo(name = "trend_direction")
    val trendDirection: String?, // "increasing", "decreasing", "stable"
    
    @ColumnInfo(name = "rate_of_change_per_week")
    val rateOfChangePerWeek: Float?, // Rate of change per week
    
    // Goal context
    @ColumnInfo(name = "distance_from_goal")
    val distanceFromGoal: Float?, // How far from target measurement
    
    @ColumnInfo(name = "goal_progress_percentage")
    val goalProgressPercentage: Float?, // Progress toward goal (0-100%)
    
    @ColumnInfo(name = "target_value")
    val targetValue: Float?, // Target value for this measurement
    
    @ColumnInfo(name = "target_date")
    val targetDate: LocalDate?, // Target date to reach goal
    
    // Health implications
    @ColumnInfo(name = "health_risk_category")
    val healthRiskCategory: String?, // "low", "moderate", "high" based on measurements
    
    @ColumnInfo(name = "waist_hip_ratio")
    val waistHipRatio: Float?, // Calculated WHR for health assessment
    
    @ColumnInfo(name = "body_fat_category")
    val bodyFatCategory: String?, // "essential", "athlete", "fitness", "average", "obese"
    
    @ColumnInfo(name = "muscle_mass_category")
    val muscleMassCategory: String?, // "below_average", "average", "above_average", "high"
    
    // Environmental factors
    @ColumnInfo(name = "room_temperature")
    val roomTemperature: Float?, // Can affect some measurements
    
    @ColumnInfo(name = "humidity_percentage")
    val humidityPercentage: Float?,
    
    @ColumnInfo(name = "menstrual_cycle_day")
    val menstrualCycleDay: Int?, // For women, affects measurements
    
    @ColumnInfo(name = "stress_level")
    val stressLevel: Int?, // 1-10 scale
    
    // Notes and context
    @ColumnInfo(name = "measurement_notes")
    val measurementNotes: String?,
    
    @ColumnInfo(name = "measurement_difficulty")
    val measurementDifficulty: String?, // "easy", "moderate", "difficult"
    
    @ColumnInfo(name = "measurement_consistency")
    val measurementConsistency: String?, // How consistent with usual measurements
    
    @ColumnInfo(name = "technician_notes")
    val technicianNotes: String?, // Notes from professional who took measurement
    
    // Validation and correction
    @ColumnInfo(name = "is_validated")
    val isValidated: Boolean = false,
    
    @ColumnInfo(name = "validated_by")
    val validatedBy: String?, // Who validated the measurement
    
    @ColumnInfo(name = "was_corrected")
    val wasCorrected: Boolean = false,
    
    @ColumnInfo(name = "corrected_from_value")
    val correctedFromValue: Float?,
    
    @ColumnInfo(name = "correction_reason")
    val correctionReason: String?,
    
    // Media
    @ColumnInfo(name = "measurement_photo_urls")
    val measurementPhotoUrls: String?, // JSON array of measurement photos
    
    @ColumnInfo(name = "scan_image_url")
    val scanImageUrl: String?, // DEXA scan or other medical images
    
    @ColumnInfo(name = "report_pdf_url")
    val reportPdfUrl: String?, // Professional report PDF
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "measured_at")
    val measuredAt: LocalDateTime?, // Exact timestamp of measurement
    
    @ColumnInfo(name = "synced_at")
    val syncedAt: LocalDateTime? // When synced from external source
)