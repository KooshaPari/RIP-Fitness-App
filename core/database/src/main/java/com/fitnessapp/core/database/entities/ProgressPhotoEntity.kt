package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Progress photo entity for visual fitness tracking.
 * 
 * Stores progress photos with comprehensive metadata for
 * tracking visual changes in body composition and physique.
 * 
 * Features:
 * - Multiple photo angles and poses
 * - Standardized photo conditions
 * - Progress comparison tools
 * - Privacy and sharing controls
 */
@Entity(
    tableName = "progress_photos",
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
        Index(value = ["photo_type"]),
        Index(value = ["body_angle"]),
        Index(value = ["is_comparison_baseline"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite for user's daily photos
    ]
)
data class ProgressPhotoEntity(
    @PrimaryKey
    @ColumnInfo(name = "photo_id")
    val photoId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic photo information
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "photo_type")
    val photoType: String, // "progress", "comparison", "milestone", "transformation", "pose"
    
    @ColumnInfo(name = "body_angle")
    val bodyAngle: String, // "front", "back", "side_left", "side_right", "three_quarter", "detail"
    
    @ColumnInfo(name = "photo_url")
    val photoUrl: String,
    
    @ColumnInfo(name = "thumbnail_url")
    val thumbnailUrl: String?,
    
    @ColumnInfo(name = "compressed_url")
    val compressedUrl: String?, // For faster loading
    
    // Photo metadata
    @ColumnInfo(name = "file_size_bytes")
    val fileSizeBytes: Long?,
    
    @ColumnInfo(name = "image_width")
    val imageWidth: Int?,
    
    @ColumnInfo(name = "image_height")
    val imageHeight: Int?,
    
    @ColumnInfo(name = "image_format")
    val imageFormat: String?, // "jpeg", "png", "heic"
    
    @ColumnInfo(name = "image_quality")
    val imageQuality: String?, // "high", "medium", "low"
    
    // Standardization for comparison
    @ColumnInfo(name = "pose_type")
    val poseType: String?, // "relaxed", "flexed", "most_muscular", "vacuum", "natural"
    
    @ColumnInfo(name = "clothing_type")
    val clothingType: String?, // "underwear", "swimwear", "gym_clothes", "shirtless", "sports_bra"
    
    @ColumnInfo(name = "lighting_conditions")
    val lightingConditions: String?, // "natural", "artificial", "studio", "gym", "bathroom"
    
    @ColumnInfo(name = "background_type")
    val backgroundType: String?, // "plain", "gym", "home", "outdoor", "studio"
    
    @ColumnInfo(name = "camera_distance")
    val cameraDistance: String?, // "close", "medium", "full_body", "far"
    
    @ColumnInfo(name = "camera_height")
    val cameraHeight: String?, // "eye_level", "chest_level", "waist_level", "low", "high"
    
    // Timing and conditions
    @ColumnInfo(name = "time_of_day")
    val timeOfDay: String?, // "morning", "afternoon", "evening", "post_workout", "fasted"
    
    @ColumnInfo(name = "fasting_status")
    val fastingStatus: Boolean?, // Whether taken while fasting
    
    @ColumnInfo(name = "pre_workout")
    val preWorkout: Boolean = false,
    
    @ColumnInfo(name = "post_workout")
    val postWorkout: Boolean = false,
    
    @ColumnInfo(name = "pump_status")
    val pumpStatus: String?, // "no_pump", "light_pump", "full_pump"
    
    @ColumnInfo(name = "hydration_status")
    val hydrationStatus: String?, // "dehydrated", "normal", "well_hydrated"
    
    @ColumnInfo(name = "carb_depletion")
    val carbDepletion: Boolean = false, // For bodybuilding prep
    
    // Body composition context
    @ColumnInfo(name = "weight_kg")
    val weightKg: Float?, // Weight when photo was taken
    
    @ColumnInfo(name = "body_fat_percentage")
    val bodyFatPercentage: Float?, // Estimated or measured body fat
    
    @ColumnInfo(name = "muscle_condition")
    val muscleCondition: String?, // "soft", "normal", "hard", "contest_ready"
    
    @ColumnInfo(name = "vascularity_level")
    val vascularityLevel: String?, // "none", "light", "moderate", "high", "extreme"
    
    @ColumnInfo(name = "muscle_definition")
    val muscleDefinition: String?, // "soft", "defined", "cut", "shredded"
    
    // Progress tracking
    @ColumnInfo(name = "is_baseline")
    val isBaseline: Boolean = false, // Starting point photo
    
    @ColumnInfo(name = "is_comparison_baseline")
    val isComparisonBaseline: Boolean = false, // Used for comparisons
    
    @ColumnInfo(name = "is_milestone")
    val isMilestone: Boolean = false, // Important progress milestone
    
    @ColumnInfo(name = "milestone_type")
    val milestoneType: String?, // "goal_weight", "body_fat_target", "contest_prep", "transformation"
    
    @ColumnInfo(name = "progress_period")
    val progressPeriod: String?, // "weekly", "monthly", "transformation", "prep"
    
    @ColumnInfo(name = "comparison_photo_ids")
    val comparisonPhotoIds: String?, // JSON array of related comparison photos
    
    // Goals and program context
    @ColumnInfo(name = "program_week")
    val programWeek: Int?, // Week of current program/cut/bulk
    
    @ColumnInfo(name = "training_phase")
    val trainingPhase: String?, // "bulk", "cut", "maintenance", "prep", "off_season"
    
    @ColumnInfo(name = "diet_phase")
    val dietPhase: String?, // "deficit", "surplus", "maintenance", "refeed"
    
    @ColumnInfo(name = "goal_context")
    val goalContext: String?, // "fat_loss", "muscle_gain", "recomposition", "contest_prep"
    
    // Visual analysis and AI features
    @ColumnInfo(name = "ai_analysis_completed")
    val aiAnalysisCompleted: Boolean = false,
    
    @ColumnInfo(name = "muscle_group_scores")
    val muscleGroupScores: String?, // JSON object with AI-assessed muscle development
    
    @ColumnInfo(name = "body_fat_estimate")
    val bodyFatEstimate: Float?, // AI-estimated body fat percentage
    
    @ColumnInfo(name = "symmetry_score")
    val symmetryScore: Float?, // AI-assessed body symmetry (0-100)
    
    @ColumnInfo(name = "posing_quality")
    val posingQuality: String?, // "poor", "good", "excellent" - AI assessment
    
    @ColumnInfo(name = "photo_quality_score")
    val photoQualityScore: Float?, // Technical photo quality (0-100)
    
    // User annotations and notes
    @ColumnInfo(name = "user_notes")
    val userNotes: String?,
    
    @ColumnInfo(name = "mood_when_taken")
    val moodWhenTaken: String?, // "confident", "motivated", "discouraged", "excited"
    
    @ColumnInfo(name = "satisfaction_rating")
    val satisfactionRating: Int?, // 1-10 satisfaction with progress
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 motivation level when taken
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: Int?, // 1-10 body confidence
    
    // Social and sharing
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "shared_with_trainer")
    val sharedWithTrainer: Boolean = false,
    
    @ColumnInfo(name = "shared_on_social")
    val sharedOnSocial: Boolean = false,
    
    @ColumnInfo(name = "privacy_level")
    val privacyLevel: String = "private", // "private", "trainer_only", "friends", "public"
    
    @ColumnInfo(name = "hashtags")
    val hashtags: String?, // JSON array of hashtags if shared
    
    @ColumnInfo(name = "share_count")
    val shareCount: Int = 0,
    
    @ColumnInfo(name = "like_count")
    val likeCount: Int = 0,
    
    // Technical metadata
    @ColumnInfo(name = "device_used")
    val deviceUsed: String?, // "iphone_14", "samsung_s23", "professional_camera"
    
    @ColumnInfo(name = "camera_app")
    val cameraApp: String?, // "native", "instagram", "vsco", "app_camera"
    
    @ColumnInfo(name = "has_flash")
    val hasFlash: Boolean = false,
    
    @ColumnInfo(name = "has_filter")
    val hasFilter: Boolean = false,
    
    @ColumnInfo(name = "filter_type")
    val filterType: String?, // Type of filter applied
    
    @ColumnInfo(name = "is_edited")
    val isEdited: Boolean = false,
    
    @ColumnInfo(name = "editing_app")
    val editingApp: String?, // App used for editing
    
    // Location and environment
    @ColumnInfo(name = "location")
    val location: String?, // "home", "gym", "studio", "outdoor", "bathroom"
    
    @ColumnInfo(name = "mirror_used")
    val mirrorUsed: Boolean = false,
    
    @ColumnInfo(name = "selfie_mode")
    val selfieMode: Boolean = false, // Front-facing camera
    
    @ColumnInfo(name = "timer_used")
    val timerUsed: Boolean = false,
    
    @ColumnInfo(name = "assistance_used")
    val assistanceUsed: Boolean = false, // Someone else took the photo
    
    // Validation and quality control
    @ColumnInfo(name = "is_valid_progress_photo")
    val isValidProgressPhoto: Boolean = true,
    
    @ColumnInfo(name = "quality_issues")
    val qualityIssues: String?, // JSON array of identified issues
    
    @ColumnInfo(name = "standardization_score")
    val standardizationScore: Float?, // How well it follows photo standards
    
    @ColumnInfo(name = "comparison_suitability")
    val comparisonSuitability: String?, // "excellent", "good", "fair", "poor"
    
    // Backup and storage
    @ColumnInfo(name = "cloud_backup_url")
    val cloudBackupUrl: String?,
    
    @ColumnInfo(name = "local_backup_path")
    val localBackupPath: String?,
    
    @ColumnInfo(name = "is_backed_up")
    val isBackedUp: Boolean = false,
    
    @ColumnInfo(name = "backup_service")
    val backupService: String?, // "google_photos", "icloud", "dropbox"
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "taken_at")
    val takenAt: LocalDateTime?, // Exact time photo was taken
    
    @ColumnInfo(name = "uploaded_at")
    val uploadedAt: LocalDateTime?, // When uploaded to server
    
    @ColumnInfo(name = "last_viewed_at")
    val lastViewedAt: LocalDateTime? // Last time user viewed this photo
)