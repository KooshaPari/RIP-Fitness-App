package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Set entity for detailed set-by-set tracking.
 * 
 * Represents individual sets within workout exercises, storing
 * detailed performance data for each set including weight, reps,
 * RPE, rest time, and quality metrics.
 * 
 * Strong app inspired features:
 * - Set-by-set tracking with precise metrics
 * - Rest timer integration
 * - RPE and quality tracking
 * - Automatic progression calculations
 * - Warmup vs working set differentiation
 */
@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["workout_exercise_id"],
            childColumns = ["workout_exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workout_exercise_id"]),
        Index(value = ["set_number"]),
        Index(value = ["set_type"]),
        Index(value = ["is_completed"]),
        Index(value = ["is_personal_record"]),
        Index(value = ["created_at"]),
        Index(value = ["workout_exercise_id", "set_number"]) // Composite for ordered set queries
    ]
)
data class SetEntity(
    @PrimaryKey
    @ColumnInfo(name = "set_id")
    val setId: String,
    
    @ColumnInfo(name = "workout_exercise_id")
    val workoutExerciseId: String,
    
    // Set identification and ordering
    @ColumnInfo(name = "set_number")
    val setNumber: Int, // 1, 2, 3, etc. within the exercise
    
    @ColumnInfo(name = "set_type")
    val setType: String, // "warmup", "working", "drop", "cluster", "rest_pause", "failure"
    
    @ColumnInfo(name = "superset_position")
    val supersetPosition: Int?, // Position within superset if applicable
    
    // Performance metrics (Strong-inspired)
    @ColumnInfo(name = "weight_kg")
    val weightKg: Float?,
    
    @ColumnInfo(name = "reps")
    val reps: Int?,
    
    @ColumnInfo(name = "reps_target")
    val repsTarget: Int?, // Planned reps for this set
    
    @ColumnInfo(name = "reps_in_reserve")
    val repsInReserve: Int?, // RIR - how many more reps could be done
    
    @ColumnInfo(name = "rpe")
    val rpe: Float?, // Rate of Perceived Exertion (1-10, with 0.5 increments)
    
    @ColumnInfo(name = "rpe_target")
    val rpeTarget: Float?, // Planned RPE for this set
    
    // Time-based metrics
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int?, // For time-based exercises
    
    @ColumnInfo(name = "rest_time_seconds")
    val restTimeSeconds: Int?, // Rest before this set
    
    @ColumnInfo(name = "rest_time_target_seconds")
    val restTimeTargetSeconds: Int?, // Planned rest time
    
    @ColumnInfo(name = "tempo")
    val tempo: String?, // "3-1-2-1" format (eccentric-pause-concentric-pause)
    
    @ColumnInfo(name = "time_under_tension_seconds")
    val timeUnderTensionSeconds: Int?,
    
    // Distance-based metrics (for cardio)
    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Float?,
    
    @ColumnInfo(name = "pace_per_km")
    val pacePerKm: String?, // "5:30" format for running pace
    
    @ColumnInfo(name = "speed_kmh")
    val speedKmh: Float?,
    
    // Quality and form metrics
    @ColumnInfo(name = "form_quality")
    val formQuality: String?, // "excellent", "good", "fair", "poor"
    
    @ColumnInfo(name = "range_of_motion")
    val rangeOfMotion: String?, // "full", "partial", "limited"
    
    @ColumnInfo(name = "control_quality")
    val controlQuality: String?, // "controlled", "moderate", "rushed"
    
    @ColumnInfo(name = "mind_muscle_connection")
    val mindMuscleConnection: Int?, // 1-10 scale
    
    @ColumnInfo(name = "muscle_activation")
    val muscleActivation: Int?, // 1-10 scale
    
    // Failure and intensity
    @ColumnInfo(name = "reached_failure")
    val reachedFailure: Boolean = false,
    
    @ColumnInfo(name = "failure_type")
    val failureType: String?, // "muscular", "cardiovascular", "form_breakdown", "mental"
    
    @ColumnInfo(name = "assisted_reps")
    val assistedReps: Int = 0, // Reps with spotter assistance
    
    @ColumnInfo(name = "forced_reps")
    val forcedReps: Int = 0, // Reps beyond failure with help
    
    @ColumnInfo(name = "partial_reps")
    val partialReps: Int = 0, // Partial range of motion reps
    
    @ColumnInfo(name = "negative_reps")
    val negativeReps: Int = 0, // Eccentric-only reps
    
    // Equipment and technique variations
    @ColumnInfo(name = "grip_type")
    val gripType: String?, // "overhand", "underhand", "mixed", "hook", "straps"
    
    @ColumnInfo(name = "stance_width")
    val stanceWidth: String?, // "narrow", "shoulder_width", "wide", "sumo"
    
    @ColumnInfo(name = "equipment_variation")
    val equipmentVariation: String?, // "barbell", "dumbbell", "machine", "cable", "bodyweight"
    
    @ColumnInfo(name = "safety_equipment_used")
    val safetyEquipmentUsed: String?, // JSON array: "belt", "straps", "sleeves", "wraps"
    
    // Progressive overload tracking
    @ColumnInfo(name = "estimated_1rm_kg")
    val estimated1rmKg: Float?, // Calculated 1RM from this set
    
    @ColumnInfo(name = "intensity_percentage")
    val intensityPercentage: Float?, // Percentage of 1RM used
    
    @ColumnInfo(name = "volume_kg")
    val volumeKg: Float?, // Weight × reps for this set
    
    @ColumnInfo(name = "relative_intensity")
    val relativeIntensity: Float?, // Intensity relative to bodyweight
    
    // Personal records and achievements
    @ColumnInfo(name = "is_personal_record")
    val isPersonalRecord: Boolean = false,
    
    @ColumnInfo(name = "pr_type")
    val prType: String?, // "weight", "reps", "volume", "1rm", "endurance"
    
    @ColumnInfo(name = "previous_best_weight")
    val previousBestWeightKg: Float?, // Previous PR for comparison
    
    @ColumnInfo(name = "previous_best_reps")
    val previousBestReps: Int?,
    
    @ColumnInfo(name = "improvement_percentage")
    val improvementPercentage: Float?, // % improvement from last time
    
    // Environmental and contextual factors
    @ColumnInfo(name = "fatigue_level")
    val fatigueLevel: Int?, // 1-10 scale before this set
    
    @ColumnInfo(name = "energy_level")
    val energyLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "focus_level")
    val focusLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "pump_quality")
    val pumpQuality: Int?, // 1-10 scale muscle pump after set
    
    // Assistance and support
    @ColumnInfo(name = "spotter_used")
    val spotterUsed: Boolean = false,
    
    @ColumnInfo(name = "spotter_assistance_level")
    val spotterAssistanceLevel: String?, // "minimal", "moderate", "significant"
    
    @ColumnInfo(name = "training_partner")
    val trainingPartner: Boolean = false,
    
    @ColumnInfo(name = "coaching_cues_received")
    val coachingCuesReceived: String?, // Cues given during this set
    
    // Technique and execution
    @ColumnInfo(name = "breathing_pattern_followed")
    val breathingPatternFollowed: Boolean?,
    
    @ColumnInfo(name = "pause_duration_seconds")
    val pauseDurationSeconds: Float?, // For paused reps
    
    @ColumnInfo(name = "eccentric_emphasis")
    val eccentricEmphasis: Boolean = false,
    
    @ColumnInfo(name = "concentric_emphasis")
    val concentricEmphasis: Boolean = false,
    
    @ColumnInfo(name = "isometric_hold_seconds")
    val isometricHoldSeconds: Float?,
    
    // Completion and status
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completion_reason")
    val completionReason: String?, // "completed", "failure", "form_breakdown", "injury", "time"
    
    @ColumnInfo(name = "was_planned")
    val wasPlanned: Boolean = true, // False for bonus/extra sets
    
    // Notes and observations
    @ColumnInfo(name = "set_notes")
    val setNotes: String?,
    
    @ColumnInfo(name = "technique_notes")
    val techniqueNotes: String?,
    
    @ColumnInfo(name = "improvement_notes")
    val improvementNotes: String?, // What to work on next time
    
    // Heart rate and physiological data (if available)
    @ColumnInfo(name = "peak_heart_rate")
    val peakHeartRate: Int?,
    
    @ColumnInfo(name = "average_heart_rate")
    val averageHeartRate: Int?,
    
    @ColumnInfo(name = "heart_rate_recovery_60s")
    val heartRateRecovery60s: Int?, // HR drop after 60 seconds
    
    @ColumnInfo(name = "calories_burned")
    val caloriesBurned: Float?, // If tracked by wearable
    
    // Media and analysis
    @ColumnInfo(name = "form_video_url")
    val formVideoUrl: String?,
    
    @ColumnInfo(name = "set_photo_url")
    val setPhotoUrl: String?,
    
    @ColumnInfo(name = "velocity_data")
    val velocityData: String?, // JSON data from velocity-based training
    
    @ColumnInfo(name = "force_plate_data")
    val forcePlateData: String?, // JSON data if available
    
    // Timestamps and duration
    @ColumnInfo(name = "started_at")
    val startedAt: LocalDateTime?,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)