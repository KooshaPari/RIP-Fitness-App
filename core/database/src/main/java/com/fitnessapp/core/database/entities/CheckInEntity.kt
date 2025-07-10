package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Check-in entity for regular progress assessments.
 * 
 * Stores structured weekly/monthly check-ins that combine
 * quantitative metrics with qualitative assessments for
 * comprehensive progress tracking.
 * 
 * Features:
 * - Structured progress assessments
 * - Goal evaluation and adjustment
 * - Comprehensive progress scoring
 * - Trend analysis and insights
 */
@Entity(
    tableName = "check_ins",
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
        Index(value = ["check_in_type"]),
        Index(value = ["week_number"]),
        Index(value = ["is_completed"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "check_in_type", "week_number"]) // Composite for tracking
    ]
)
data class CheckInEntity(
    @PrimaryKey
    @ColumnInfo(name = "check_in_id")
    val checkInId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic check-in information
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "check_in_type")
    val checkInType: String, // "weekly", "bi_weekly", "monthly", "milestone", "emergency"
    
    @ColumnInfo(name = "week_number")
    val weekNumber: Int?, // Week of current program/phase
    
    @ColumnInfo(name = "phase_name")
    val phaseName: String?, // "Week 1-4: Foundation", "Cutting Phase", etc.
    
    @ColumnInfo(name = "program_name")
    val programName: String?, // Current program being followed
    
    // Weight and body composition
    @ColumnInfo(name = "current_weight_kg")
    val currentWeightKg: Float?,
    
    @ColumnInfo(name = "weight_change_from_last")
    val weightChangeFromLast: Float?, // Change since last check-in
    
    @ColumnInfo(name = "weight_trend")
    val weightTrend: String?, // "increasing", "decreasing", "stable"
    
    @ColumnInfo(name = "target_weight_kg")
    val targetWeightKg: Float?, // Current weight goal
    
    @ColumnInfo(name = "body_fat_percentage")
    val bodyFatPercentage: Float?,
    
    @ColumnInfo(name = "muscle_mass_kg")
    val muscleMassKg: Float?,
    
    @ColumnInfo(name = "body_measurements")
    val bodyMeasurements: String?, // JSON object with key measurements
    
    // Performance metrics
    @ColumnInfo(name = "strength_improvements")
    val strengthImprovements: String?, // JSON object with lift improvements
    
    @ColumnInfo(name = "cardio_improvements")
    val cardioImprovements: String?, // Endurance improvements
    
    @ColumnInfo(name = "new_personal_records")
    val newPersonalRecords: Int = 0, // Count of PRs since last check-in
    
    @ColumnInfo(name = "workout_consistency")
    val workoutConsistency: Float?, // Percentage of planned workouts completed
    
    @ColumnInfo(name = "total_volume_kg")
    val totalVolumeKg: Float?, // Total training volume for the period
    
    @ColumnInfo(name = "average_workout_rating")
    val averageWorkoutRating: Float?, // Average workout quality rating
    
    // Nutrition and adherence
    @ColumnInfo(name = "nutrition_adherence")
    val nutritionAdherence: Float?, // Percentage adherence to nutrition plan
    
    @ColumnInfo(name = "calorie_target_accuracy")
    val calorieTargetAccuracy: Float?, // How close to calorie targets
    
    @ColumnInfo(name = "macro_target_accuracy")
    val macroTargetAccuracy: Float?, // How close to macro targets
    
    @ColumnInfo(name = "meal_prep_consistency")
    val mealPrepConsistency: String?, // "excellent", "good", "fair", "poor"
    
    @ColumnInfo(name = "hydration_consistency")
    val hydrationConsistency: String?, // Water intake consistency
    
    @ColumnInfo(name = "supplement_adherence")
    val supplementAdherence: Float?, // Supplement protocol compliance
    
    // Recovery and wellness
    @ColumnInfo(name = "sleep_quality_average")
    val sleepQualityAverage: Float?, // Average sleep quality (1-10)
    
    @ColumnInfo(name = "sleep_duration_average")
    val sleepDurationAverage: Float?, // Average hours of sleep
    
    @ColumnInfo(name = "stress_level_average")
    val stressLevelAverage: Float?, // Average stress level (1-10)
    
    @ColumnInfo(name = "energy_level_average")
    val energyLevelAverage: Float?, // Average energy level (1-10)
    
    @ColumnInfo(name = "recovery_score_average")
    val recoveryScoreAverage: Float?, // Average recovery score
    
    @ColumnInfo(name = "injury_status")
    val injuryStatus: String?, // "none", "minor", "moderate", "significant"
    
    @ColumnInfo(name = "pain_areas")
    val painAreas: String?, // JSON array of body areas with pain
    
    // Subjective assessments
    @ColumnInfo(name = "overall_satisfaction")
    val overallSatisfaction: Int?, // 1-10 satisfaction with progress
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 current motivation
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: Int?, // 1-10 confidence in reaching goals
    
    @ColumnInfo(name = "body_image_satisfaction")
    val bodyImageSatisfaction: Int?, // 1-10 satisfaction with appearance
    
    @ColumnInfo(name = "program_enjoyment")
    val programEnjoyment: Int?, // 1-10 enjoyment of current program
    
    @ColumnInfo(name = "lifestyle_balance")
    val lifestyleBalance: Int?, // 1-10 how well fitness fits lifestyle
    
    // Goal progress assessment
    @ColumnInfo(name = "primary_goal_progress")
    val primaryGoalProgress: Float?, // 0-100% progress toward main goal
    
    @ColumnInfo(name = "secondary_goals_progress")
    val secondaryGoalsProgress: String?, // JSON object with multiple goal progress
    
    @ColumnInfo(name = "goals_on_track")
    val goalsOnTrack: Boolean?, // Whether goals are achievable on current timeline
    
    @ColumnInfo(name = "goal_adjustments_needed")
    val goalAdjustmentsNeeded: String?, // Suggested goal modifications
    
    @ColumnInfo(name = "new_goals_identified")
    val newGoalsIdentified: String?, // New goals to add
    
    // Challenges and obstacles
    @ColumnInfo(name = "biggest_challenges")
    val biggestChallenges: String?, // Main obstacles faced
    
    @ColumnInfo(name = "barriers_to_progress")
    val barriersToProgress: String?, // What's preventing better progress
    
    @ColumnInfo(name = "lifestyle_obstacles")
    val lifestyleObstacles: String?, // External factors affecting progress
    
    @ColumnInfo(name = "motivation_obstacles")
    val motivationObstacles: String?, // Mental/emotional barriers
    
    @ColumnInfo(name = "resource_limitations")
    val resourceLimitations: String?, // Time, money, equipment constraints
    
    // Successes and wins
    @ColumnInfo(name = "biggest_wins")
    val biggestWins: String?, // Major achievements since last check-in
    
    @ColumnInfo(name = "unexpected_improvements")
    val unexpectedImprovements: String?, // Surprising positive changes
    
    @ColumnInfo(name = "habit_improvements")
    val habitImprovements: String?, // Positive habit changes
    
    @ColumnInfo(name = "mindset_improvements")
    val mindsetImprovements: String?, // Mental/emotional improvements
    
    @ColumnInfo(name = "breakthrough_moments")
    val breakthroughMoments: String?, // Significant realizations
    
    // Program evaluation
    @ColumnInfo(name = "program_effectiveness")
    val programEffectiveness: Int?, // 1-10 how well current program is working
    
    @ColumnInfo(name = "exercise_preferences")
    val exercisePreferences: String?, // Favorite/least favorite exercises
    
    @ColumnInfo(name = "program_modifications")
    val programModifications: String?, // Suggested changes to program
    
    @ColumnInfo(name = "intensity_appropriateness")
    val intensityAppropriateness: String?, // "too_easy", "just_right", "too_hard"
    
    @ColumnInfo(name = "volume_appropriateness")
    val volumeAppropriateness: String?, // "too_low", "just_right", "too_high"
    
    // Future planning
    @ColumnInfo(name = "next_period_focus")
    val nextPeriodFocus: String?, // Main focus for upcoming period
    
    @ColumnInfo(name = "adjustments_planned")
    val adjustmentsPlanned: String?, // Changes to make going forward
    
    @ColumnInfo(name = "new_strategies")
    val newStrategies: String?, // New approaches to try
    
    @ColumnInfo(name = "support_needed")
    val supportNeeded: String?, // Areas where help is needed
    
    @ColumnInfo(name = "experiments_to_try")
    val experimentsToTry: String?, // Things to test in next period
    
    // Calculated scores and insights
    @ColumnInfo(name = "overall_progress_score")
    val overallProgressScore: Float?, // 0-100 composite progress score
    
    @ColumnInfo(name = "adherence_score")
    val adherenceScore: Float?, // 0-100 composite adherence score
    
    @ColumnInfo(name = "wellness_score")
    val wellnessScore: Float?, // 0-100 composite wellness score
    
    @ColumnInfo(name = "consistency_score")
    val consistencyScore: Float?, // 0-100 consistency score
    
    @ColumnInfo(name = "trend_analysis")
    val trendAnalysis: String?, // AI-generated trend insights
    
    @ColumnInfo(name = "recommendations")
    val recommendations: String?, // AI-generated recommendations
    
    // Coach and support team input
    @ColumnInfo(name = "coach_feedback")
    val coachFeedback: String?, // Trainer/coach observations
    
    @ColumnInfo(name = "coach_recommendations")
    val coachRecommendations: String?, // Professional recommendations
    
    @ColumnInfo(name = "support_team_notes")
    val supportTeamNotes: String?, // Notes from nutritionist, physio, etc.
    
    @ColumnInfo(name = "professional_assessments")
    val professionalAssessments: String?, // Medical/professional evaluations
    
    // Media and documentation
    @ColumnInfo(name = "progress_photos")
    val progressPhotos: String?, // JSON array of photo IDs
    
    @ColumnInfo(name = "measurement_photos")
    val measurementPhotos: String?, // JSON array of measurement photo IDs
    
    @ColumnInfo(name = "video_updates")
    val videoUpdates: String?, // JSON array of video update URLs
    
    @ColumnInfo(name = "chart_images")
    val chartImages: String?, // JSON array of progress chart images
    
    // Completion and validation
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "completion_percentage")
    val completionPercentage: Float = 0.0f, // How much of check-in was filled out
    
    @ColumnInfo(name = "requires_coach_review")
    val requiresCoachReview: Boolean = false,
    
    @ColumnInfo(name = "is_validated")
    val isValidated: Boolean = false,
    
    @ColumnInfo(name = "validated_by")
    val validatedBy: String?, // Who validated the check-in
    
    // Reminder and scheduling
    @ColumnInfo(name = "scheduled_date")
    val scheduledDate: LocalDate?, // When check-in was scheduled
    
    @ColumnInfo(name = "reminder_sent")
    val reminderSent: Boolean = false,
    
    @ColumnInfo(name = "completion_time_minutes")
    val completionTimeMinutes: Int?, // How long check-in took
    
    @ColumnInfo(name = "next_check_in_date")
    val nextCheckInDate: LocalDate?, // When next check-in is due
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "completed_at")
    val completedAt: LocalDateTime?, // When check-in was finished
    
    @ColumnInfo(name = "reviewed_at")
    val reviewedAt: LocalDateTime? // When coach/professional reviewed
)