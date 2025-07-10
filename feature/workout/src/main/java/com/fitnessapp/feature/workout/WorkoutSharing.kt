package com.fitnessapp.feature.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

/**
 * Workout Sharing - Strong App inspired workout and routine sharing system
 * Features:
 * - Share workout routines with friends
 * - Community routine library
 * - Workout achievement sharing
 * - Progress photo sharing
 * - Social feed with workout updates
 * - Challenge creation and participation
 * - Leaderboards and competitions
 */

data class SharedWorkout(
    val id: String = UUID.randomUUID().toString(),
    val workoutId: String,
    val workoutName: String,
    val exercises: List<WorkoutExercise>,
    val totalVolume: Double,
    val duration: kotlin.time.Duration,
    val personalRecords: List<PersonalRecord>,
    val sharedBy: String, // User ID
    val sharedByName: String,
    val sharedAt: Date = Date(),
    val description: String = "",
    val tags: List<String> = emptyList(),
    val isPublic: Boolean = true,
    val likes: Int = 0,
    val comments: List<WorkoutComment> = emptyList(),
    val achievements: List<WorkoutAchievement> = emptyList()
)

data class SharedRoutine(
    val id: String = UUID.randomUUID().toString(),
    val routine: WorkoutRoutine,
    val sharedBy: String,
    val sharedByName: String,
    val sharedAt: Date = Date(),
    val description: String = "",
    val difficulty: RoutineDifficulty,
    val category: RoutineCategory,
    val estimatedDuration: kotlin.time.Duration,
    val downloads: Int = 0,
    val rating: Double = 0.0,
    val reviews: List<RoutineReview> = emptyList(),
    val successStories: List<SuccessStory> = emptyList(),
    val isVerified: Boolean = false // Verified by trainers
)

data class WorkoutComment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val text: String,
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val replies: List<WorkoutComment> = emptyList()
)

data class WorkoutAchievement(
    val id: String = UUID.randomUUID().toString(),
    val type: AchievementType,
    val title: String,
    val description: String,
    val icon: String,
    val unlockedAt: Date,
    val rarity: AchievementRarity
)

enum class AchievementType {
    PERSONAL_RECORD, VOLUME_MILESTONE, CONSISTENCY_STREAK,
    FIRST_TIME, PLATE_MILESTONE, REP_MILESTONE, WEIGHT_MILESTONE
}

enum class AchievementRarity {
    COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
}

data class RoutineReview(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val rating: Int, // 1-5 stars
    val text: String,
    val timestamp: Date = Date(),
    val helpful: Int = 0,
    val experienceLevel: ExperienceLevel
)

enum class ExperienceLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

data class SuccessStory(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val title: String,
    val story: String,
    val beforePhoto: String? = null,
    val afterPhoto: String? = null,
    val duration: String, // "6 months", "1 year", etc.
    val achievements: List<String>,
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val isVerified: Boolean = false
)

data class WorkoutChallenge(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val type: ChallengeType,
    val goal: ChallengeGoal,
    val startDate: Date,
    val endDate: Date,
    val participants: List<ChallengeParticipant> = emptyList(),
    val createdBy: String,
    val rules: List<String>,
    val prizes: List<String> = emptyList(),
    val isActive: Boolean = true
)

enum class ChallengeType {
    VOLUME_CHALLENGE, // Most total volume
    CONSISTENCY_CHALLENGE, // Most workout days
    STRENGTH_CHALLENGE, // Biggest strength gains
    PERSONAL_RECORD_CHALLENGE, // Most PRs achieved
    SPECIFIC_EXERCISE_CHALLENGE // Focus on one exercise
}

data class ChallengeGoal(
    val type: String, // "total_volume", "workout_count", "pr_count"
    val target: Double,
    val unit: String // "lbs", "days", "records"
)

data class ChallengeParticipant(
    val userId: String,
    val userName: String,
    val currentProgress: Double,
    val joinedAt: Date,
    val lastUpdate: Date,
    val rank: Int = 0
)

data class SocialFeedItem(
    val id: String = UUID.randomUUID().toString(),
    val type: FeedItemType,
    val userId: String,
    val userName: String,
    val userPhoto: String? = null,
    val content: String,
    val timestamp: Date = Date(),
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val workoutData: SharedWorkout? = null,
    val routineData: SharedRoutine? = null,
    val achievementData: WorkoutAchievement? = null
)

enum class FeedItemType {
    WORKOUT_COMPLETED, PERSONAL_RECORD, ROUTINE_SHARED,
    ACHIEVEMENT_UNLOCKED, CHALLENGE_JOINED, MILESTONE_REACHED
}

/**
 * Workout Sharing handles social features and community sharing
 */
class WorkoutSharing {
    
    private val _sharedWorkouts = MutableStateFlow<List<SharedWorkout>>(emptyList())
    val sharedWorkouts: StateFlow<List<SharedWorkout>> = _sharedWorkouts.asStateFlow()
    
    private val _sharedRoutines = MutableStateFlow<List<SharedRoutine>>(emptyList())
    val sharedRoutines: StateFlow<List<SharedRoutine>> = _sharedRoutines.asStateFlow()
    
    private val _socialFeed = MutableStateFlow<List<SocialFeedItem>>(emptyList())
    val socialFeed: StateFlow<List<SocialFeedItem>> = _socialFeed.asStateFlow()
    
    private val _activeChallenges = MutableStateFlow<List<WorkoutChallenge>>(emptyList())
    val activeChallenges: StateFlow<List<WorkoutChallenge>> = _activeChallenges.asStateFlow()
    
    private val _myAchievements = MutableStateFlow<List<WorkoutAchievement>>(emptyList())
    val myAchievements: StateFlow<List<WorkoutAchievement>> = _myAchievements.asStateFlow()
    
    private val _followers = MutableStateFlow<List<UserProfile>>(emptyList())
    val followers: StateFlow<List<UserProfile>> = _followers.asStateFlow()
    
    private val _following = MutableStateFlow<List<UserProfile>>(emptyList())
    val following: StateFlow<List<UserProfile>> = _following.asStateFlow()
    
    /**
     * Share a completed workout (Strong app feature)
     */
    fun shareWorkout(
        workout: ActiveWorkout,
        description: String = "",
        tags: List<String> = emptyList(),
        achievements: List<WorkoutAchievement> = emptyList()
    ): String {
        val sharedWorkout = SharedWorkout(
            workoutId = workout.id,
            workoutName = workout.name,
            exercises = workout.exercises,
            totalVolume = workout.totalVolume,
            duration = workout.totalDuration,
            personalRecords = emptyList(), // Would get from ProgressAnalytics
            sharedBy = getCurrentUserId(),
            sharedByName = getCurrentUserName(),
            description = description,
            tags = tags,
            achievements = achievements
        )
        
        val currentShared = _sharedWorkouts.value.toMutableList()
        currentShared.add(0, sharedWorkout) // Add to beginning
        _sharedWorkouts.value = currentShared
        
        // Add to social feed
        addToSocialFeed(
            type = FeedItemType.WORKOUT_COMPLETED,
            content = "Completed workout: ${workout.name}",
            workoutData = sharedWorkout
        )
        
        return sharedWorkout.id
    }
    
    /**
     * Share a workout routine
     */
    fun shareRoutine(
        routine: WorkoutRoutine,
        description: String = ""
    ): String {
        val sharedRoutine = SharedRoutine(
            routine = routine,
            sharedBy = getCurrentUserId(),
            sharedByName = getCurrentUserName(),
            description = description,
            difficulty = routine.difficulty,
            category = routine.category,
            estimatedDuration = routine.estimatedDuration
        )
        
        val currentShared = _sharedRoutines.value.toMutableList()
        currentShared.add(0, sharedRoutine)
        _sharedRoutines.value = currentShared
        
        // Add to social feed
        addToSocialFeed(
            type = FeedItemType.ROUTINE_SHARED,
            content = "Shared routine: ${routine.name}",
            routineData = sharedRoutine
        )
        
        return sharedRoutine.id
    }
    
    /**
     * Like a shared workout
     */
    fun likeWorkout(workoutId: String) {
        val workouts = _sharedWorkouts.value.toMutableList()
        val index = workouts.indexOfFirst { it.id == workoutId }
        
        if (index != -1) {
            workouts[index] = workouts[index].copy(likes = workouts[index].likes + 1)
            _sharedWorkouts.value = workouts
        }
    }
    
    /**
     * Comment on a shared workout
     */
    fun commentOnWorkout(
        workoutId: String,
        text: String
    ): String {
        val comment = WorkoutComment(
            userId = getCurrentUserId(),
            userName = getCurrentUserName(),
            text = text
        )
        
        val workouts = _sharedWorkouts.value.toMutableList()
        val index = workouts.indexOfFirst { it.id == workoutId }
        
        if (index != -1) {
            val updatedComments = workouts[index].comments + comment
            workouts[index] = workouts[index].copy(comments = updatedComments)
            _sharedWorkouts.value = workouts
        }
        
        return comment.id
    }
    
    /**
     * Download and save a shared routine
     */
    fun downloadRoutine(routineId: String): WorkoutRoutine? {
        val sharedRoutine = _sharedRoutines.value.find { it.id == routineId } ?: return null
        
        // Increment download count
        val routines = _sharedRoutines.value.toMutableList()
        val index = routines.indexOfFirst { it.id == routineId }
        
        if (index != -1) {
            routines[index] = routines[index].copy(downloads = routines[index].downloads + 1)
            _sharedRoutines.value = routines
        }
        
        return sharedRoutine.routine.copy(
            id = UUID.randomUUID().toString(), // New ID for user's copy
            isCustom = true,
            createdAt = Date()
        )
    }
    
    /**
     * Rate and review a shared routine
     */
    fun reviewRoutine(
        routineId: String,
        rating: Int,
        text: String,
        experienceLevel: ExperienceLevel
    ): String {
        val review = RoutineReview(
            userId = getCurrentUserId(),
            userName = getCurrentUserName(),
            rating = rating,
            text = text,
            experienceLevel = experienceLevel
        )
        
        val routines = _sharedRoutines.value.toMutableList()
        val index = routines.indexOfFirst { it.id == routineId }
        
        if (index != -1) {
            val routine = routines[index]
            val updatedReviews = routine.reviews + review
            val newRating = updatedReviews.map { it.rating }.average()
            
            routines[index] = routine.copy(
                reviews = updatedReviews,
                rating = newRating
            )
            _sharedRoutines.value = routines
        }
        
        return review.id
    }
    
    /**
     * Search shared routines
     */
    fun searchSharedRoutines(
        query: String = "",
        category: RoutineCategory? = null,
        difficulty: RoutineDifficulty? = null,
        minRating: Double = 0.0,
        sortBy: RoutineSortOption = RoutineSortOption.MOST_RECENT
    ): List<SharedRoutine> {
        var filtered = _sharedRoutines.value
        
        // Text search
        if (query.isNotEmpty()) {
            filtered = filtered.filter { routine ->
                routine.routine.name.contains(query, ignoreCase = true) ||
                routine.description.contains(query, ignoreCase = true) ||
                routine.routine.tags.any { it.contains(query, ignoreCase = true) }
            }
        }
        
        // Filter by category
        category?.let { cat ->
            filtered = filtered.filter { it.category == cat }
        }
        
        // Filter by difficulty
        difficulty?.let { diff ->
            filtered = filtered.filter { it.difficulty == diff }
        }
        
        // Filter by rating
        filtered = filtered.filter { it.rating >= minRating }
        
        // Sort results
        return when (sortBy) {
            RoutineSortOption.MOST_RECENT -> filtered.sortedByDescending { it.sharedAt }
            RoutineSortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating }
            RoutineSortOption.MOST_DOWNLOADED -> filtered.sortedByDescending { it.downloads }
            RoutineSortOption.MOST_REVIEWED -> filtered.sortedByDescending { it.reviews.size }
        }
    }
    
    /**
     * Create a new challenge
     */
    fun createChallenge(
        name: String,
        description: String,
        type: ChallengeType,
        goal: ChallengeGoal,
        duration: kotlin.time.Duration,
        rules: List<String>
    ): String {
        val challenge = WorkoutChallenge(
            name = name,
            description = description,
            type = type,
            goal = goal,
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + duration.inWholeMilliseconds),
            createdBy = getCurrentUserId(),
            rules = rules
        )
        
        val currentChallenges = _activeChallenges.value.toMutableList()
        currentChallenges.add(challenge)
        _activeChallenges.value = currentChallenges
        
        return challenge.id
    }
    
    /**
     * Join a challenge
     */
    fun joinChallenge(challengeId: String): Boolean {
        val challenges = _activeChallenges.value.toMutableList()
        val index = challenges.indexOfFirst { it.id == challengeId }
        
        if (index != -1) {
            val challenge = challenges[index]
            val participant = ChallengeParticipant(
                userId = getCurrentUserId(),
                userName = getCurrentUserName(),
                currentProgress = 0.0,
                joinedAt = Date(),
                lastUpdate = Date()
            )
            
            val updatedParticipants = challenge.participants + participant
            challenges[index] = challenge.copy(participants = updatedParticipants)
            _activeChallenges.value = challenges
            
            // Add to social feed
            addToSocialFeed(
                type = FeedItemType.CHALLENGE_JOINED,
                content = "Joined challenge: ${challenge.name}"
            )
            
            return true
        }
        
        return false
    }
    
    /**
     * Update challenge progress
     */
    fun updateChallengeProgress(challengeId: String, progress: Double) {
        val challenges = _activeChallenges.value.toMutableList()
        val index = challenges.indexOfFirst { it.id == challengeId }
        
        if (index != -1) {
            val challenge = challenges[index]
            val participants = challenge.participants.toMutableList()
            val participantIndex = participants.indexOfFirst { it.userId == getCurrentUserId() }
            
            if (participantIndex != -1) {
                participants[participantIndex] = participants[participantIndex].copy(
                    currentProgress = progress,
                    lastUpdate = Date()
                )
                
                // Update rankings
                val rankedParticipants = participants.sortedByDescending { it.currentProgress }
                    .mapIndexed { rank, participant -> participant.copy(rank = rank + 1) }
                
                challenges[index] = challenge.copy(participants = rankedParticipants)
                _activeChallenges.value = challenges
            }
        }
    }
    
    /**
     * Unlock achievement
     */
    fun unlockAchievement(achievement: WorkoutAchievement) {
        val currentAchievements = _myAchievements.value.toMutableList()
        
        // Check if already unlocked
        if (currentAchievements.none { it.type == achievement.type && it.title == achievement.title }) {
            currentAchievements.add(achievement)
            _myAchievements.value = currentAchievements
            
            // Add to social feed
            addToSocialFeed(
                type = FeedItemType.ACHIEVEMENT_UNLOCKED,
                content = "Unlocked achievement: ${achievement.title}",
                achievementData = achievement
            )
        }
    }
    
    /**
     * Follow a user
     */
    fun followUser(userId: String, userName: String) {
        val userProfile = UserProfile(userId, userName)
        val currentFollowing = _following.value.toMutableList()
        
        if (currentFollowing.none { it.id == userId }) {
            currentFollowing.add(userProfile)
            _following.value = currentFollowing
        }
    }
    
    /**
     * Get social feed (Strong app social features)
     */
    fun getSocialFeed(limit: Int = 20): List<SocialFeedItem> {
        return _socialFeed.value.take(limit)
    }
    
    /**
     * Get trending routines
     */
    fun getTrendingRoutines(limit: Int = 10): List<SharedRoutine> {
        return _sharedRoutines.value
            .sortedByDescending { it.downloads + it.reviews.size * 2 + it.rating * 10 }
            .take(limit)
    }
    
    /**
     * Get challenge leaderboard
     */
    fun getChallengeLeaderboard(challengeId: String): List<ChallengeParticipant> {
        val challenge = _activeChallenges.value.find { it.id == challengeId }
        return challenge?.participants?.sortedByDescending { it.currentProgress } ?: emptyList()
    }
    
    // Helper methods
    private fun addToSocialFeed(
        type: FeedItemType,
        content: String,
        workoutData: SharedWorkout? = null,
        routineData: SharedRoutine? = null,
        achievementData: WorkoutAchievement? = null
    ) {
        val feedItem = SocialFeedItem(
            type = type,
            userId = getCurrentUserId(),
            userName = getCurrentUserName(),
            content = content,
            workoutData = workoutData,
            routineData = routineData,
            achievementData = achievementData
        )
        
        val currentFeed = _socialFeed.value.toMutableList()
        currentFeed.add(0, feedItem) // Add to beginning
        _socialFeed.value = currentFeed.take(100) // Keep last 100 items
    }
    
    private fun getCurrentUserId(): String {
        // Would get from user session/authentication
        return "current_user_id"
    }
    
    private fun getCurrentUserName(): String {
        // Would get from user profile
        return "Current User"
    }
    
    /**
     * Generate shareable workout summary
     */
    fun generateWorkoutSummary(workout: ActiveWorkout): WorkoutSummaryImage {
        return WorkoutSummaryImage(
            workoutName = workout.name,
            date = workout.startTime,
            duration = workout.totalDuration,
            totalVolume = workout.totalVolume,
            exerciseCount = workout.exercises.size,
            personalRecords = emptyList(), // Would get from analytics
            topExercises = workout.exercises.take(3).map { it.exercise.name }
        )
    }
}

enum class RoutineSortOption {
    MOST_RECENT, HIGHEST_RATED, MOST_DOWNLOADED, MOST_REVIEWED
}

data class UserProfile(
    val id: String,
    val name: String,
    val photo: String? = null,
    val stats: UserStats? = null
)

data class UserStats(
    val totalWorkouts: Int,
    val totalVolume: Double,
    val currentStreak: Int,
    val personalRecords: Int
)

data class WorkoutSummaryImage(
    val workoutName: String,
    val date: Date,
    val duration: kotlin.time.Duration,
    val totalVolume: Double,
    val exerciseCount: Int,
    val personalRecords: List<PersonalRecord>,
    val topExercises: List<String>
)