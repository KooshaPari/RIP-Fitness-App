package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Routine entity for workout templates and programs.
 * 
 * Stores workout routines/templates that can be reused, shared,
 * and followed as structured training programs. Supports both
 * user-created routines and professional training programs.
 * 
 * Strong app inspired features:
 * - Template-based workout creation
 * - Program periodization support
 * - Social sharing and discovery
 * - Progress tracking across routine cycles
 */
@Entity(
    tableName = "routines",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["created_by_user_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["created_by_user_id"]),
        Index(value = ["routine_name"]),
        Index(value = ["routine_category"]),
        Index(value = ["difficulty_level"]),
        Index(value = ["is_public"]),
        Index(value = ["is_featured"]),
        Index(value = ["usage_count"]),
        Index(value = ["average_rating"])
    ]
)
data class RoutineEntity(
    @PrimaryKey
    @ColumnInfo(name = "routine_id")
    val routineId: String,
    
    @ColumnInfo(name = "created_by_user_id")
    val createdByUserId: String?,
    
    // Basic routine information
    @ColumnInfo(name = "routine_name")
    val routineName: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "routine_category")
    val routineCategory: String, // "strength", "hypertrophy", "powerlifting", "bodybuilding", "cardio", "hybrid"
    
    @ColumnInfo(name = "training_style")
    val trainingStyle: String?, // "powerlifting", "bodybuilding", "crossfit", "strongman", "general_fitness"
    
    @ColumnInfo(name = "primary_goals")
    val primaryGoals: String?, // JSON array: "strength", "muscle_gain", "fat_loss", "endurance"
    
    // Program structure
    @ColumnInfo(name = "duration_weeks")
    val durationWeeks: Int?, // Total program length
    
    @ColumnInfo(name = "sessions_per_week")
    val sessionsPerWeek: Int,
    
    @ColumnInfo(name = "session_names")
    val sessionNames: String?, // JSON array of workout names
    
    @ColumnInfo(name = "session_order")
    val sessionOrder: String?, // JSON array defining the weekly schedule
    
    @ColumnInfo(name = "rest_days_pattern")
    val restDaysPattern: String?, // JSON array defining rest day placement
    
    // Difficulty and requirements
    @ColumnInfo(name = "difficulty_level")
    val difficultyLevel: String, // "beginner", "intermediate", "advanced", "expert"
    
    @ColumnInfo(name = "experience_required")
    val experienceRequired: String?, // "0-6_months", "6-12_months", "1-2_years", "2+_years"
    
    @ColumnInfo(name = "time_per_session_minutes")
    val timePerSessionMinutes: Int?,
    
    @ColumnInfo(name = "equipment_required")
    val equipmentRequired: String?, // JSON array of required equipment
    
    @ColumnInfo(name = "space_requirements")
    val spaceRequirements: String?, // "minimal", "moderate", "full_gym"
    
    // Program periodization
    @ColumnInfo(name = "periodization_type")
    val periodizationType: String?, // "linear", "undulating", "block", "conjugate", "none"
    
    @ColumnInfo(name = "progression_scheme")
    val progressionScheme: String?, // Description of how to progress
    
    @ColumnInfo(name = "deload_frequency")
    val deloadFrequency: Int?, // Deload every X weeks
    
    @ColumnInfo(name = "phases")
    val phases: String?, // JSON array of program phases
    
    @ColumnInfo(name = "mesocycle_length_weeks")
    val mesocycleLengthWeeks: Int?,
    
    // Exercise structure (stored as JSON for flexibility)
    @ColumnInfo(name = "workouts_json")
    val workoutsJson: String, // Complete workout structure
    
    @ColumnInfo(name = "exercise_selections")
    val exerciseSelections: String?, // JSON with exercise options/alternatives
    
    @ColumnInfo(name = "rep_schemes")
    val repSchemes: String?, // JSON with rep/set schemes for each exercise
    
    @ColumnInfo(name = "intensity_guidelines")
    val intensityGuidelines: String?, // RPE/percentage guidelines
    
    // Customization and adaptation
    @ColumnInfo(name = "is_customizable")
    val isCustomizable: Boolean = true,
    
    @ColumnInfo(name = "auto_adaptation_enabled")
    val autoAdaptationEnabled: Boolean = false,
    
    @ColumnInfo(name = "exercise_substitutions")
    val exerciseSubstitutions: String?, // JSON mapping exercise alternatives
    
    @ColumnInfo(name = "volume_adjustments")
    val volumeAdjustments: String?, // Guidelines for volume modifications
    
    // Target audience and characteristics
    @ColumnInfo(name = "target_gender")
    val targetGender: String?, // "male", "female", "any"
    
    @ColumnInfo(name = "target_age_range")
    val targetAgeRange: String?, // "18-25", "26-40", "40+", "any"
    
    @ColumnInfo(name = "target_body_type")
    val targetBodyType: String?, // "ectomorph", "mesomorph", "endomorph", "any"
    
    @ColumnInfo(name = "injury_considerations")
    val injuryConsiderations: String?, // JSON array of injury modifications
    
    // Social and sharing features
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "is_featured")
    val isFeatured: Boolean = false,
    
    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,
    
    @ColumnInfo(name = "verified_by")
    val verifiedBy: String?, // "coach", "expert", "admin"
    
    @ColumnInfo(name = "cost_credits")
    val costCredits: Int?, // Cost in app credits if premium
    
    // Usage and popularity metrics
    @ColumnInfo(name = "usage_count")
    val usageCount: Long = 0,
    
    @ColumnInfo(name = "completion_count")
    val completionCount: Long = 0,
    
    @ColumnInfo(name = "average_rating")
    val averageRating: Float?,
    
    @ColumnInfo(name = "rating_count")
    val ratingCount: Int = 0,
    
    @ColumnInfo(name = "save_count")
    val saveCount: Long = 0,
    
    @ColumnInfo(name = "share_count")
    val shareCount: Long = 0,
    
    // Success metrics
    @ColumnInfo(name = "average_strength_gain")
    val averageStrengthGain: Float?, // Average % strength increase
    
    @ColumnInfo(name = "average_muscle_gain_kg")
    val averageMuscleGainKg: Float?,
    
    @ColumnInfo(name = "average_fat_loss_kg")
    val averageFatLossKg: Float?,
    
    @ColumnInfo(name = "completion_rate")
    val completionRate: Float?, // % of users who complete the program
    
    @ColumnInfo(name = "satisfaction_score")
    val satisfactionScore: Float?, // 1-10 average satisfaction
    
    // Professional attribution
    @ColumnInfo(name = "author_name")
    val authorName: String?,
    
    @ColumnInfo(name = "author_credentials")
    val authorCredentials: String?,
    
    @ColumnInfo(name = "source")
    val source: String?, // "user_created", "professional", "book", "study", "app_generated"
    
    @ColumnInfo(name = "reference_links")
    val referenceLinks: String?, // JSON array of reference URLs
    
    @ColumnInfo(name = "scientific_backing")
    val scientificBacking: String?, // JSON array of supporting research
    
    // Media and presentation
    @ColumnInfo(name = "cover_image_url")
    val coverImageUrl: String?,
    
    @ColumnInfo(name = "demo_video_url")
    val demoVideoUrl: String?,
    
    @ColumnInfo(name = "instruction_videos")
    val instructionVideos: String?, // JSON array of instructional video URLs
    
    @ColumnInfo(name = "progress_photos")
    val progressPhotos: String?, // JSON array of example progress photos
    
    // Tags and categorization
    @ColumnInfo(name = "tags")
    val tags: String?, // JSON array: "beginner_friendly", "time_efficient", "home_gym", etc.
    
    @ColumnInfo(name = "muscle_groups_targeted")
    val muscleGroupsTargeted: String?, // JSON array of primary muscle groups
    
    @ColumnInfo(name = "training_modalities")
    val trainingModalities: String?, // JSON array: "barbell", "dumbbell", "bodyweight", etc.
    
    // Version control and updates
    @ColumnInfo(name = "version")
    val version: String = "1.0",
    
    @ColumnInfo(name = "parent_routine_id")
    val parentRoutineId: String?, // For routine variations/updates
    
    @ColumnInfo(name = "changelog")
    val changelog: String?, // What changed between versions
    
    @ColumnInfo(name = "last_updated_by")
    val lastUpdatedBy: String?,
    
    // Administrative
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "is_approved")
    val isApproved: Boolean = true,
    
    @ColumnInfo(name = "approval_date")
    val approvalDate: LocalDateTime?,
    
    @ColumnInfo(name = "requires_subscription")
    val requiresSubscription: Boolean = false,
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "published_at")
    val publishedAt: LocalDateTime?
)