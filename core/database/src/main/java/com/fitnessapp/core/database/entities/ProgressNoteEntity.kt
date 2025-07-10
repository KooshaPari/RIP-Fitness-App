package com.fitnessapp.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Progress notes entity for detailed fitness journey documentation.
 * 
 * Stores written observations, reflections, and insights about
 * training progress, challenges, and achievements.
 * 
 * Features:
 * - Structured and free-form notes
 * - Progress insights and observations
 * - Goal tracking and reflection
 * - Coach and trainer communication
 */
@Entity(
    tableName = "progress_notes",
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
        Index(value = ["note_type"]),
        Index(value = ["category"]),
        Index(value = ["is_milestone"]),
        Index(value = ["created_at"]),
        Index(value = ["user_id", "date"]) // Composite for user's daily notes
    ]
)
data class ProgressNoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "note_id")
    val noteId: String,
    
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    // Basic note information
    @ColumnInfo(name = "date")
    val date: LocalDate,
    
    @ColumnInfo(name = "note_type")
    val noteType: String, // "daily_reflection", "workout_notes", "diet_notes", "milestone", "challenge", "achievement"
    
    @ColumnInfo(name = "category")
    val category: String?, // "training", "nutrition", "recovery", "mindset", "lifestyle", "health"
    
    @ColumnInfo(name = "title")
    val title: String?,
    
    @ColumnInfo(name = "content")
    val content: String,
    
    // Structured reflection fields
    @ColumnInfo(name = "energy_level")
    val energyLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "motivation_level")
    val motivationLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "stress_level")
    val stressLevel: Int?, // 1-10 scale
    
    @ColumnInfo(name = "sleep_quality")
    val sleepQuality: Int?, // 1-10 scale
    
    @ColumnInfo(name = "overall_mood")
    val overallMood: String?, // "excellent", "good", "neutral", "low", "poor"
    
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: Int?, // 1-10 scale
    
    // Progress observations
    @ColumnInfo(name = "physical_observations")
    val physicalObservations: String?, // Changes noticed in body/performance
    
    @ColumnInfo(name = "strength_changes")
    val strengthChanges: String?, // Strength improvements or declines
    
    @ColumnInfo(name = "endurance_changes")
    val enduranceChanges: String?, // Endurance improvements
    
    @ColumnInfo(name = "body_composition_notes")
    val bodyCompositionNotes: String?, // Visual or measured changes
    
    @ColumnInfo(name = "measurement_changes")
    val measurementChanges: String?, // Weight, measurements, etc.
    
    @ColumnInfo(name = "performance_highlights")
    val performanceHighlights: String?, // Best performances, PRs, etc.
    
    // Challenges and obstacles
    @ColumnInfo(name = "challenges_faced")
    val challengesFaced: String?, // What difficulties were encountered
    
    @ColumnInfo(name = "obstacles_overcome")
    val obstaclesOvercome: String?, // How challenges were handled
    
    @ColumnInfo(name = "areas_for_improvement")
    val areasForImprovement: String?, // What needs work
    
    @ColumnInfo(name = "setbacks_encountered")
    val setbacksEncountered: String?, // Any setbacks or regressions
    
    @ColumnInfo(name = "lessons_learned")
    val lessonsLearned: String?, // Key insights gained
    
    // Achievements and wins
    @ColumnInfo(name = "achievements")
    val achievements: String?, // What was accomplished
    
    @ColumnInfo(name = "personal_records")
    val personalRecords: String?, // New PRs or bests
    
    @ColumnInfo(name = "milestone_reached")
    val milestoneReached: String?, // Major milestones achieved
    
    @ColumnInfo(name = "habits_formed")
    val habitsFormed: String?, // New positive habits
    
    @ColumnInfo(name = "breakthrough_moments")
    val breakthroughMoments: String?, // Significant realizations
    
    // Goal progress
    @ColumnInfo(name = "goal_progress_notes")
    val goalProgressNotes: String?, // Progress toward specific goals
    
    @ColumnInfo(name = "goal_adjustments")
    val goalAdjustments: String?, // Changes made to goals
    
    @ColumnInfo(name = "new_goals_set")
    val newGoalsSet: String?, // New goals established
    
    @ColumnInfo(name = "goal_completion")
    val goalCompletion: String?, // Goals that were completed
    
    // Training specific notes
    @ColumnInfo(name = "workout_quality")
    val workoutQuality: String?, // "excellent", "good", "average", "poor", "skipped"
    
    @ColumnInfo(name = "training_intensity")
    val trainingIntensity: String?, // "high", "medium", "low", "recovery"
    
    @ColumnInfo(name = "exercise_form_notes")
    val exerciseFormNotes: String?, // Notes about technique improvements
    
    @ColumnInfo(name = "new_exercises_tried")
    val newExercisesTried: String?, // Exercises attempted for first time
    
    @ColumnInfo(name = "favorite_exercises")
    val favoriteExercises: String?, // Exercises particularly enjoyed
    
    @ColumnInfo(name = "challenging_exercises")
    val challengingExercises: String?, // Exercises that were difficult
    
    // Nutrition specific notes
    @ColumnInfo(name = "diet_adherence")
    val dietAdherence: String?, // "excellent", "good", "fair", "poor"
    
    @ColumnInfo(name = "hunger_levels")
    val hungerLevels: String?, // "very_high", "high", "normal", "low", "very_low"
    
    @ColumnInfo(name = "cravings_experienced")
    val cravingsExperienced: String?, // Types of cravings
    
    @ColumnInfo(name = "new_foods_tried")
    val newFoodsTried: String?, // New foods or recipes
    
    @ColumnInfo(name = "meal_satisfaction")
    val mealSatisfaction: String?, // How satisfying meals were
    
    @ColumnInfo(name = "eating_challenges")
    val eatingChallenges: String?, // Difficulties with nutrition plan
    
    // Recovery and wellness
    @ColumnInfo(name = "recovery_quality")
    val recoveryQuality: String?, // "excellent", "good", "fair", "poor"
    
    @ColumnInfo(name = "soreness_levels")
    val sorenessLevels: String?, // Muscle soreness experienced
    
    @ColumnInfo(name = "injury_status")
    val injuryStatus: String?, // Any injuries or pain
    
    @ColumnInfo(name = "wellness_practices")
    val wellnessPractices: String?, // Recovery activities done
    
    @ColumnInfo(name = "stress_management")
    val stressManagement: String?, // How stress was handled
    
    // Lifestyle factors
    @ColumnInfo(name = "life_events")
    val lifeEvents: String?, // Major life events affecting fitness
    
    @ColumnInfo(name = "schedule_challenges")
    val scheduleChallenges: String?, // Time management issues
    
    @ColumnInfo(name = "social_influences")
    val socialInfluences: String?, // How social situations affected progress
    
    @ColumnInfo(name = "environmental_factors")
    val environmentalFactors: String?, // External factors affecting progress
    
    @ColumnInfo(name = "travel_impact")
    val travelImpact: String?, // How travel affected routine
    
    // Mental and emotional aspects
    @ColumnInfo(name = "mindset_shifts")
    val mindsetShifts: String?, // Changes in thinking patterns
    
    @ColumnInfo(name = "emotional_state")
    val emotionalState: String?, // Overall emotional wellbeing
    
    @ColumnInfo(name = "self_talk_notes")
    val selfTalkNotes: String?, // Internal dialogue observations
    
    @ColumnInfo(name = "visualization_practice")
    val visualizationPractice: String?, // Mental rehearsal activities
    
    @ColumnInfo(name = "meditation_notes")
    val meditationNotes: String?, // Mindfulness practice observations
    
    // Future planning
    @ColumnInfo(name = "upcoming_challenges")
    val upcomingChallenges: String?, // Anticipated difficulties
    
    @ColumnInfo(name = "strategies_planned")
    val strategiesPlanned: String?, // Plans to address challenges
    
    @ColumnInfo(name = "next_week_focus")
    val nextWeekFocus: String?, // What to focus on next
    
    @ColumnInfo(name = "adjustments_needed")
    val adjustmentsNeeded: String?, // Changes to make to plan
    
    @ColumnInfo(name = "experiments_to_try")
    val experimentsToTry: String?, // New things to test
    
    // Milestone and achievement tracking
    @ColumnInfo(name = "is_milestone")
    val isMilestone: Boolean = false,
    
    @ColumnInfo(name = "milestone_type")
    val milestoneType: String?, // "weight_goal", "strength_goal", "habit_streak", "program_completion"
    
    @ColumnInfo(name = "celebration_planned")
    val celebrationPlanned: String?, // How achievement will be celebrated
    
    @ColumnInfo(name = "gratitude_notes")
    val gratitudeNotes: String?, // Things to be grateful for
    
    // Social and support
    @ColumnInfo(name = "support_received")
    val supportReceived: String?, // Help or encouragement received
    
    @ColumnInfo(name = "support_needed")
    val supportNeeded: String?, // Areas where support is needed
    
    @ColumnInfo(name = "coach_feedback")
    val coachFeedback: String?, // Feedback from trainer/coach
    
    @ColumnInfo(name = "peer_interactions")
    val peerInteractions: String?, // Interactions with fitness community
    
    @ColumnInfo(name = "inspiration_sources")
    val inspirationSources: String?, // What provided motivation
    
    // Sharing and privacy
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
    
    @ColumnInfo(name = "shared_with_coach")
    val sharedWithCoach: Boolean = false,
    
    @ColumnInfo(name = "shared_with_community")
    val sharedWithCommunity: Boolean = false,
    
    @ColumnInfo(name = "privacy_level")
    val privacyLevel: String = "private", // "private", "coach_only", "friends", "public"
    
    // Tags and categorization
    @ColumnInfo(name = "tags")
    val tags: String?, // JSON array of tags for organization
    
    @ColumnInfo(name = "themes")
    val themes: String?, // JSON array of recurring themes
    
    @ColumnInfo(name = "keywords")
    val keywords: String?, // JSON array for searchability
    
    // Media attachments
    @ColumnInfo(name = "photo_urls")
    val photoUrls: String?, // JSON array of attached photos
    
    @ColumnInfo(name = "video_urls")
    val videoUrls: String?, // JSON array of attached videos
    
    @ColumnInfo(name = "voice_note_url")
    val voiceNoteUrl: String?, // Audio reflection
    
    // Metadata
    @ColumnInfo(name = "word_count")
    val wordCount: Int?,
    
    @ColumnInfo(name = "writing_time_minutes")
    val writingTimeMinutes: Int?, // How long spent writing
    
    @ColumnInfo(name = "mood_when_writing")
    val moodWhenWriting: String?, // Emotional state while writing
    
    @ColumnInfo(name = "prompt_used")
    val promptUsed: String?, // If guided by specific prompts
    
    // Timestamps
    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: LocalDateTime,
    
    @ColumnInfo(name = "last_viewed_at")
    val lastViewedAt: LocalDateTime? // Last time note was read
)