package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Workout Exercise entity linking exercises to specific workouts.
 * 
 * Represents the bridge between workouts and exercises, storing
 * exercise-specific data for each workout session including
 * order, planned vs actual performance, and session notes.
 * 
 * Strong app inspired features:
 * - Exercise ordering within workouts
 * - Planned vs actual tracking
 * - Rest timer integration
 * - Progressive overload tracking
 */
@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["workout_id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workout_id"]),
        Index(value = ["exercise_id"]),
        Index(value = ["exercise_order"]),
        Index(value = ["is_completed"]),
        Index(value = ["superset_group"]),
        Index(value = ["workout_id", "exercise_order"]) // Composite for ordered queries
    ]
)
data class WorkoutExerciseEntity(
    @PrimaryKey
    @ColumnInfo(name = "workout_exercise_id")
    val workoutExerciseId: String,
    
    @ColumnInfo(name = "workout_id")
    val workoutId: String,
    
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    
    // Exercise positioning and grouping
    @ColumnInfo(name = "exercise_order")
    val exerciseOrder: Int, // Order within the workout (1, 2, 3, etc.)
    
    @ColumnInfo(name = "superset_group")
    val supersetGroup: String?, // Group identifier for supersets/circuits
    
    @ColumnInfo(name = "superset_order")
    val supersetOrder: Int?, // Order within the superset (1, 2, 3, etc.)
    
    @ColumnInfo(name = "exercise_type")
    val exerciseType: String = "main", // "warmup", "main", "accessory", "cooldown"
    
    // Planning and targets (Strong-inspired)
    @ColumnInfo(name = "planned_sets")
    val plannedSets: Int?,
    
    @ColumnInfo(name = "planned_reps")
    val plannedReps: String?, // Can be range like "8-12" or specific like "10"
    
    @ColumnInfo(name = "planned_weight_kg")
    val plannedWeightKg: Float?,
    
    @ColumnInfo(name = "planned_rpe")
    val plannedRpe: Float?, // Rate of Perceived Exertion (1-10)
    
    @ColumnInfo(name = "planned_rest_seconds")
    val plannedRestSeconds: Int?,
    
    @ColumnInfo(name = "planned_tempo")
    val plannedTempo: String?, // "3-1-2-1" format
    
    // Actual performance tracking
    @ColumnInfo(name = "actual_sets")
    val actualSets: Int = 0,
    
    @ColumnInfo(name = "best_set_weight_kg")
    val bestSetWeightKg: Float?,
    
    @ColumnInfo(name = "best_set_reps")
    val bestSetReps: Int?,
    
    @ColumnInfo(name = "total_volume_kg")
    val totalVolumeKg: Float?, // Sum of all sets (weight × reps)
    
    @ColumnInfo(name = "average_rpe")
    val averageRpe: Float?,
    
    @ColumnInfo(name = "peak_rpe")
    val peakRpe: Float?,
    
    // Performance metrics
    @ColumnInfo(name = "estimated_1rm_kg")
    val estimated1rmKg: Float?, // Calculated from best set
    
    @ColumnInfo(name = "volume_load")
    val volumeLoad: Float?, // Alternative volume calculation
    
    @ColumnInfo(name = "intensity_index")
    val intensityIndex: Float?, // Percentage of estimated 1RM
    
    @ColumnInfo(name = "density_score")
    val densityScore: Float?, // Volume / time ratio
    
    // Progression tracking
    @ColumnInfo(name = "progression_from_last")
    val progressionFromLast: String?, // "weight", "reps", "volume", "none", "regression"
    
    @ColumnInfo(name = "weight_progression_kg")
    val weightProgressionKg: Float?, // Change from last similar session
    
    @ColumnInfo(name = "rep_progression")
    val repProgression: Int?, // Change in total reps
    
    @ColumnInfo(name = "volume_progression_kg")
    val volumeProgressionKg: Float?, // Change in total volume
    
    @ColumnInfo(name = "is_personal_record")
    val isPersonalRecord: Boolean = false,
    
    @ColumnInfo(name = "pr_type")
    val prType: String?, // "1rm", "volume", "reps", "time"
    
    // Rest and timing (Strong-inspired rest timer)
    @ColumnInfo(name = "total_rest_time_seconds")
    val totalRestTimeSeconds: Int?,
    
    @ColumnInfo(name = "average_rest_time_seconds")
    val averageRestTimeSeconds: Int?,
    
    @ColumnInfo(name = "exercise_duration_seconds")
    val exerciseDurationSeconds: Int?, // Total time on this exercise
    
    @ColumnInfo(name = "warmup_sets")
    val warmupSets: Int = 0,
    
    @ColumnInfo(name = "working_sets")
    val workingSets: Int = 0,
    
    @ColumnInfo(name = "dropsets")
    val dropsets: Int = 0,
    
    // Exercise execution quality
    @ColumnInfo(name = "form_quality")
    val formQuality: String?, // "excellent", "good", "fair", "poor"
    
    @ColumnInfo(name = "range_of_motion")
    val rangeOfMotion: String?, // "full", "partial", "limited"
    
    @ColumnInfo(name = "control_quality")
    val controlQuality: String?, // "controlled", "moderate", "rushed"
    
    @ColumnInfo(name = "mind_muscle_connection")
    val mindMuscleConnection: Int?, // 1-10 scale
    
    // Equipment and setup
    @ColumnInfo(name = "equipment_used")
    val equipmentUsed: String?, // JSON array of actual equipment used
    
    @ColumnInfo(name = "weight_plates_used")
    val weightPlatesUsed: String?, // JSON array for plate tracking
    
    @ColumnInfo(name = "setup_time_seconds")
    val setupTimeSeconds: Int?,
    
    @ColumnInfo(name = "equipment_notes")
    val equipmentNotes: String?, // Notes about equipment substitutions
    
    // Environmental factors
    @ColumnInfo(name = "crowd_interference")
    val crowdInterference: Boolean = false,
    
    @ColumnInfo(name = "equipment_wait_time")
    val equipmentWaitTimeSeconds: Int?,
    
    @ColumnInfo(name = "training_partner_assistance")
    val trainingPartnerAssistance: Boolean = false,
    
    @ColumnInfo(name = "spotter_used")
    val spotterUsed: Boolean = false,
    
    // Adaptive features and coaching
    @ColumnInfo(name = "auto_progression_applied")
    val autoProgressionApplied: Boolean = false,
    
    @ColumnInfo(name = "progression_suggestion")
    val progressionSuggestion: String?, // AI recommendation for next session
    
    @ColumnInfo(name = "deload_recommended")
    val deloadRecommended: Boolean = false,
    
    @ColumnInfo(name = "technique_improvement_area")
    val techniqueImprovementArea: String?,
    
    // Fatigue and readiness
    @ColumnInfo(name = "fatigue_level_start")
    val fatigueLevelStart: Int?, // 1-10 scale before starting exercise
    
    @ColumnInfo(name = "fatigue_level_end")
    val fatigueLevelEnd: Int?, // 1-10 scale after completing exercise
    
    @ColumnInfo(name = "pump_quality")
    val pumpQuality: Int?, // 1-10 scale muscle pump achieved
    
    @ColumnInfo(name = "muscle_activation")
    val muscleActivation: Int?, // 1-10 scale how well muscles were activated
    
    // Completion and status
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completion_reason")
    val completionReason: String?, // "completed", "fatigue", "injury", "time", "equipment"
    
    @ColumnInfo(name = "sets_completed")
    val setsCompleted: Int = 0,
    
    @ColumnInfo(name = "completion_percentage")
    val completionPercentage: Float = 0.0f,
    
    // Notes and feedback
    @ColumnInfo(name = "exercise_notes")
    val exerciseNotes: String?,
    
    @ColumnInfo(name = "coach_feedback")
    val coachFeedback: String?,
    
    @ColumnInfo(name = "form_check_notes")
    val formCheckNotes: String?,
    
    // Media
    @ColumnInfo(name = "form_video_url")
    val formVideoUrl: String?,
    
    @ColumnInfo(name = "set_photos")
    val setPhotos: String?, // JSON array of photo URLs
    
    // Timestamps
    @ColumnInfo(name = "started_at")
    val startedAt: LocalDateTime?,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)