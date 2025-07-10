package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Exercise database entity for comprehensive exercise library.
 * 
 * Stores all exercise information including biomechanics, equipment,
 * difficulty levels, and instructional content. Designed to support
 * both database exercises and user-created custom exercises.
 * 
 * Strong app inspired features:
 * - Comprehensive exercise library
 * - Equipment and setup tracking
 * - Progression tracking capabilities
 * - Form and technique guidance
 */
@Entity(
    tableName = "exercises",
    indices = [
        Index(value = ["exercise_name"]),
        Index(value = ["primary_muscle_group"]),
        Index(value = ["exercise_category"]),
        Index(value = ["equipment_required"]),
        Index(value = ["difficulty_level"]),
        Index(value = ["is_compound"]),
        Index(value = ["usage_count"]),
        Index(value = ["is_active"])
    ]
)
data class ExerciseEntity(
    @PrimaryKey
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    
    // Basic exercise information
    @ColumnInfo(name = "exercise_name")
    val exerciseName: String,
    
    @ColumnInfo(name = "alternative_names")
    val alternativeNames: String?, // JSON array of alternative exercise names
    
    @ColumnInfo(name = "exercise_description")
    val exerciseDescription: String?,
    
    @ColumnInfo(name = "exercise_category")
    val exerciseCategory: String, // "strength", "cardio", "flexibility", "balance", "plyometric", "core"
    
    @ColumnInfo(name = "movement_pattern")
    val movementPattern: String?, // "squat", "hinge", "push", "pull", "carry", "lunge", "rotation"
    
    // Muscle targeting
    @ColumnInfo(name = "primary_muscle_group")
    val primaryMuscleGroup: String, // "chest", "back", "legs", "shoulders", "arms", "core", "glutes"
    
    @ColumnInfo(name = "primary_muscles")
    val primaryMuscles: String?, // JSON array of specific muscles
    
    @ColumnInfo(name = "secondary_muscles")
    val secondaryMuscles: String?, // JSON array of secondary muscles worked
    
    @ColumnInfo(name = "stabilizer_muscles")
    val stabilizerMuscles: String?, // JSON array of stabilizing muscles
    
    @ColumnInfo(name = "is_compound")
    val isCompound: Boolean = true, // true for compound movements, false for isolation
    
    @ColumnInfo(name = "is_unilateral")
    val isUnilateral: Boolean = false, // true for single-limb exercises
    
    // Equipment and setup
    @ColumnInfo(name = "equipment_required")
    val equipmentRequired: String?, // JSON array of required equipment
    
    @ColumnInfo(name = "equipment_alternatives")
    val equipmentAlternatives: String?, // JSON array of alternative equipment options
    
    @ColumnInfo(name = "setup_complexity")
    val setupComplexity: String?, // "simple", "moderate", "complex"
    
    @ColumnInfo(name = "space_requirements")
    val spaceRequirements: String?, // "minimal", "moderate", "large"
    
    @ColumnInfo(name = "safety_equipment")
    val safetyEquipment: String?, // JSON array: "spotter", "safety_bars", "straps", etc.
    
    // Exercise characteristics
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String, // "beginner", "intermediate", "advanced", "expert"
    
    @ColumnInfo(name = "learning_curve")
    val learningCurve: String?, // "easy", "moderate", "steep"
    
    @ColumnInfo(name = "injury_risk")
    val injuryRisk: String?, // "low", "moderate", "high"
    
    @ColumnInfo(name = "coordination_required")
    val coordinationRequired: String?, // "low", "moderate", "high"
    
    @ColumnInfo(name = "balance_required")
    val balanceRequired: String?, // "low", "moderate", "high"
    
    // Biomechanics and form
    @ColumnInfo(name = "range_of_motion")
    val rangeOfMotion: String?, // "partial", "full", "extended"
    
    @ColumnInfo(name = "plane_of_movement")
    val planeOfMovement: String?, // "sagittal", "frontal", "transverse", "multi_planar"
    
    @ColumnInfo(name = "tempo_recommendations")
    val tempoRecommendations: String?, // "2-1-2-1" format or description
    
    @ColumnInfo(name = "breathing_pattern")
    val breathingPattern: String?, // Description of recommended breathing
    
    @ColumnInfo(name = "key_form_cues")
    val keyFormCues: String?, // JSON array of important form cues
    
    // Programming guidelines
    @ColumnInfo(name = "recommended_rep_ranges")
    val recommendedRepRanges: String?, // JSON object with different goals
    
    @ColumnInfo(name = "recommended_sets")
    val recommendedSets: String?, // "2-4", "3-5", etc.
    
    @ColumnInfo(name = "rest_time_recommendations")
    val restTimeRecommendations: String?, // JSON object with different intensities
    
    @ColumnInfo(name = "frequency_recommendations")
    val frequencyRecommendations: String?, // "1-2x/week", "2-3x/week", etc.
    
    @ColumnInfo(name = "progression_methods")
    val progressionMethods: String?, // JSON array of ways to progress
    
    @ColumnInfo(name = "regression_options")
    val regressionOptions: String?, // JSON array of easier variations
    
    // Exercise variations and alternatives
    @ColumnInfo(name = "exercise_variations")
    val exerciseVariations: String?, // JSON array of exercise variation IDs
    
    @ColumnInfo(name = "similar_exercises")
    val similarExercises: String?, // JSON array of similar exercise IDs
    
    @ColumnInfo(name = "prerequisite_exercises")
    val prerequisiteExercises: String?, // JSON array of exercises to master first
    
    @ColumnInfo(name = "progression_exercises")
    val progressionExercises: String?, // JSON array of next-level exercises
    
    // Tracking and measurement
    @ColumnInfo(name = "measurement_type")
    val measurementType: String, // "weight_reps", "time", "distance", "bodyweight", "assisted"
    
    @ColumnInfo(name = "weight_increment_suggestions")
    val weightIncrementSuggestions: String?, // JSON object with beginner/intermediate/advanced
    
    @ColumnInfo(name = "supports_1rm_calculation")
    val supports1rmCalculation: Boolean = true,
    
    @ColumnInfo(name = "supports_volume_tracking")
    val supportsVolumeTracking: Boolean = true,
    
    @ColumnInfo(name = "trackable_metrics")
    val trackableMetrics: String?, // JSON array: "weight", "reps", "time", "distance", "pace"
    
    // Safety and contraindications
    @ColumnInfo(name = "contraindications")
    val contraindications: String?, // JSON array of when not to perform
    
    @ColumnInfo(name = "injury_concerns")
    val injuryConcerns: String?, // JSON array of potential injury areas
    
    @ColumnInfo(name = "mobility_requirements")
    val mobilityRequirements: String?, // JSON array of required mobility
    
    @ColumnInfo(name = "warmup_recommendations")
    val warmupRecommendations: String?, // Specific warmup guidance
    
    // Media and instruction
    @ColumnInfo(name = "instruction_video_url")
    val instructionVideoUrl: String?,
    
    @ColumnInfo(name = "demonstration_gif_url")
    val demonstrationGifUrl: String?,
    
    @ColumnInfo(name = "form_images")
    val formImages: String?, // JSON array of instructional image URLs
    
    @ColumnInfo(name = "muscle_diagram_url")
    val muscleDiagramUrl: String?,
    
    @ColumnInfo(name = "instruction_text")
    val instructionText: String?, // Step-by-step written instructions
    
    // Data source and verification
    @ColumnInfo(name = "data_source")
    val dataSource: String, // "built_in", "user_created", "imported"
    
    @ColumnInfo(name = "created_by_user_id")
    val createdByUserId: String?, // For user-created exercises
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = true,
    
    @ColumnInfo(name = "verified_by")
    val verifiedBy: String?, // "admin", "expert", "community"
    
    // Usage and popularity
    @ColumnInfo(name = "usage_count")
    val usageCount: Long = 0,
    
    @ColumnInfo(name = "popularity_score")
    val popularityScore: Float = 0.0f,
    
    @ColumnInfo(name = "average_user_rating")
    val averageUserRating: Float?,
    
    @ColumnInfo(name = "rating_count")
    val ratingCount: Int = 0,
    
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: LocalDateTime?,
    
    // Tags and categorization
    @ColumnInfo(name = "exercise_tags")
    val exerciseTags: String?, // JSON array: "powerlifting", "bodybuilding", "functional", etc.
    
    @ColumnInfo(name = "sport_specific")
    val sportSpecific: String?, // JSON array of relevant sports
    
    @ColumnInfo(name = "training_goals")
    val trainingGoals: String?, // JSON array: "strength", "hypertrophy", "endurance", "power"
    
    // Administrative
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false,
    
    @ColumnInfo(name = "requires_approval")
    val requiresApproval: Boolean = false,
    
    // Version control for updates
    @ColumnInfo(name = "version")
    val version: Int = 1,
    
    @ColumnInfo(name = "last_reviewed_at")
    val lastReviewedAt: LocalDateTime?,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime
)