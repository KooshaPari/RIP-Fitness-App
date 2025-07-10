package com.fitnessapp.feature.workout

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Rest Timer System - Strong App inspired advanced rest timing
 * Features:
 * - Multiple simultaneous timers
 * - Auto-start on set completion
 * - Custom timer durations
 * - Progressive rest suggestions
 * - Audio/vibration notifications
 * - Timer history tracking
 * - Quick time adjustments (+15s, +30s, +60s)
 */

data class RestTimer(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val targetDuration: Duration,
    val remainingTime: Duration,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val startTime: Date? = null,
    val completionTime: Date? = null,
    val notificationSettings: TimerNotificationSettings = TimerNotificationSettings()
)

data class TimerNotificationSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val speechEnabled: Boolean = false,
    val countdownWarning: Duration = 10.seconds, // Warning at 10s remaining
    val intervalNotifications: List<Duration> = listOf(30.seconds, 15.seconds, 10.seconds)
)

data class RestTimerPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val duration: Duration,
    val exerciseTypes: List<ExerciseCategory> = emptyList(),
    val isDefault: Boolean = false
)

data class TimerSession(
    val workoutId: String,
    val timers: List<RestTimer>,
    val totalRestTime: Duration,
    val averageRestTime: Duration,
    val longestRest: Duration,
    val shortestRest: Duration
)

/**
 * Rest Timer System handles workout rest period timing
 */
class RestTimerSystem {
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private val _activeTimers = MutableStateFlow<List<RestTimer>>(emptyList())
    val activeTimers: StateFlow<List<RestTimer>> = _activeTimers.asStateFlow()
    
    private val _timerPresets = MutableStateFlow<List<RestTimerPreset>>(emptyList())
    val timerPresets: StateFlow<List<RestTimerPreset>> = _timerPresets.asStateFlow()
    
    private val _timerHistory = MutableStateFlow<List<TimerSession>>(emptyList())
    val timerHistory: StateFlow<List<TimerSession>> = _timerHistory.asStateFlow()
    
    private val _notificationSettings = MutableStateFlow(TimerNotificationSettings())
    val notificationSettings: StateFlow<TimerNotificationSettings> = _notificationSettings.asStateFlow()
    
    private val timerJobs = mutableMapOf<String, Job>()
    
    init {
        loadDefaultPresets()
    }
    
    /**
     * Start a rest timer (Strong app style auto-start)
     */
    fun startTimer(
        exerciseId: String,
        exerciseName: String,
        setNumber: Int,
        duration: Duration = getRecommendedRestTime(exerciseId),
        autoStart: Boolean = true
    ): String {
        val timer = RestTimer(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            setNumber = setNumber,
            targetDuration = duration,
            remainingTime = duration,
            isActive = autoStart,
            startTime = if (autoStart) Date() else null
        )
        
        val currentTimers = _activeTimers.value.toMutableList()
        currentTimers.add(timer)
        _activeTimers.value = currentTimers
        
        if (autoStart) {
            startTimerCountdown(timer.id)
        }
        
        return timer.id
    }
    
    /**
     * Start timer countdown coroutine
     */
    private fun startTimerCountdown(timerId: String) {
        val job = scope.launch {
            val timer = _activeTimers.value.find { it.id == timerId } ?: return@launch
            var remainingTime = timer.remainingTime
            
            while (remainingTime > 0.seconds && !timer.isPaused) {
                delay(1000) // Update every second
                
                remainingTime -= 1.seconds
                updateTimerState(timerId) { it.copy(remainingTime = remainingTime) }
                
                // Check for notification triggers
                checkNotificationTriggers(timerId, remainingTime)
            }
            
            // Timer completed
            if (remainingTime <= 0.seconds) {
                completeTimer(timerId)
            }
        }
        
        timerJobs[timerId] = job
    }
    
    /**
     * Pause/Resume timer
     */
    fun toggleTimerPause(timerId: String) {
        val timer = _activeTimers.value.find { it.id == timerId } ?: return
        
        if (timer.isPaused) {
            resumeTimer(timerId)
        } else {
            pauseTimer(timerId)
        }
    }
    
    /**
     * Pause timer
     */
    fun pauseTimer(timerId: String) {
        updateTimerState(timerId) { it.copy(isPaused = true) }
        timerJobs[timerId]?.cancel()
    }
    
    /**
     * Resume timer
     */
    fun resumeTimer(timerId: String) {
        updateTimerState(timerId) { it.copy(isPaused = false) }
        startTimerCountdown(timerId)
    }
    
    /**
     * Add time to timer (Strong app quick adjustments)
     */
    fun addTimeToTimer(timerId: String, additionalTime: Duration) {
        updateTimerState(timerId) { timer ->
            timer.copy(
                remainingTime = timer.remainingTime + additionalTime,
                targetDuration = timer.targetDuration + additionalTime
            )
        }
    }
    
    /**
     * Skip timer (mark as completed immediately)
     */
    fun skipTimer(timerId: String) {
        completeTimer(timerId)
    }
    
    /**
     * Complete timer
     */
    private fun completeTimer(timerId: String) {
        updateTimerState(timerId) { timer ->
            timer.copy(
                isActive = false,
                isCompleted = true,
                remainingTime = 0.seconds,
                completionTime = Date()
            )
        }
        
        timerJobs[timerId]?.cancel()
        timerJobs.remove(timerId)
        
        // Play completion notification
        playCompletionNotification(timerId)
        
        // Remove from active timers after delay
        scope.launch {
            delay(5000) // Keep visible for 5 seconds
            removeTimer(timerId)
        }
    }
    
    /**
     * Remove timer from active list
     */
    fun removeTimer(timerId: String) {
        val currentTimers = _activeTimers.value.toMutableList()
        currentTimers.removeIf { it.id == timerId }
        _activeTimers.value = currentTimers
        
        timerJobs[timerId]?.cancel()
        timerJobs.remove(timerId)
    }
    
    /**
     * Get recommended rest time based on exercise and previous performance
     */
    fun getRecommendedRestTime(exerciseId: String): Duration {
        // In real implementation, this would analyze:
        // - Exercise type (compound vs isolation)
        // - Previous set performance (RPE, weight)
        // - User's fitness level
        // - Training goal (strength vs hypertrophy)
        
        return when {
            // Heavy compound movements
            isCompoundExercise(exerciseId) -> 180.seconds
            // Isolation exercises
            else -> 90.seconds
        }
    }
    
    /**
     * Create timer preset
     */
    fun createPreset(
        name: String, 
        duration: Duration, 
        exerciseTypes: List<ExerciseCategory> = emptyList()
    ): String {
        val preset = RestTimerPreset(
            name = name,
            duration = duration,
            exerciseTypes = exerciseTypes
        )
        
        val currentPresets = _timerPresets.value.toMutableList()
        currentPresets.add(preset)
        _timerPresets.value = currentPresets
        
        return preset.id
    }
    
    /**
     * Get timer presets for exercise category
     */
    fun getPresetsForExercise(category: ExerciseCategory): List<RestTimerPreset> {
        return _timerPresets.value.filter { preset ->
            preset.exerciseTypes.isEmpty() || category in preset.exerciseTypes
        }
    }
    
    /**
     * Update notification settings
     */
    fun updateNotificationSettings(settings: TimerNotificationSettings) {
        _notificationSettings.value = settings
    }
    
    /**
     * Get timer statistics for workout
     */
    fun getTimerStatistics(workoutId: String): TimerSession? {
        val session = _timerHistory.value.find { it.workoutId == workoutId }
        return session
    }
    
    /**
     * Start workout timer session
     */
    fun startWorkoutTimerSession(workoutId: String) {
        // Initialize timer session tracking
        val session = TimerSession(
            workoutId = workoutId,
            timers = emptyList(),
            totalRestTime = 0.seconds,
            averageRestTime = 0.seconds,
            longestRest = 0.seconds,
            shortestRest = Duration.INFINITE
        )
        
        val currentHistory = _timerHistory.value.toMutableList()
        currentHistory.add(session)
        _timerHistory.value = currentHistory
    }
    
    /**
     * End workout timer session
     */
    fun endWorkoutTimerSession(workoutId: String) {
        val sessions = _timerHistory.value.toMutableList()
        val sessionIndex = sessions.indexOfFirst { it.workoutId == workoutId }
        
        if (sessionIndex != -1) {
            val completedTimers = _activeTimers.value.filter { it.isCompleted }
            val totalRestTime = completedTimers.sumOf { 
                (it.targetDuration - it.remainingTime).inWholeSeconds 
            }.seconds
            
            val updatedSession = sessions[sessionIndex].copy(
                timers = completedTimers,
                totalRestTime = totalRestTime,
                averageRestTime = if (completedTimers.isNotEmpty()) {
                    totalRestTime / completedTimers.size
                } else 0.seconds,
                longestRest = completedTimers.maxOfOrNull { it.targetDuration } ?: 0.seconds,
                shortestRest = completedTimers.minOfOrNull { it.targetDuration } ?: 0.seconds
            )
            
            sessions[sessionIndex] = updatedSession
            _timerHistory.value = sessions
        }
        
        // Clear active timers
        _activeTimers.value = emptyList()
        timerJobs.values.forEach { it.cancel() }
        timerJobs.clear()
    }
    
    /**
     * Get quick time adjustment options (Strong app feature)
     */
    fun getQuickTimeOptions(): List<Duration> {
        return listOf(15.seconds, 30.seconds, 60.seconds, 120.seconds)
    }
    
    // Helper methods
    private fun updateTimerState(timerId: String, update: (RestTimer) -> RestTimer) {
        val currentTimers = _activeTimers.value.toMutableList()
        val timerIndex = currentTimers.indexOfFirst { it.id == timerId }
        
        if (timerIndex != -1) {
            currentTimers[timerIndex] = update(currentTimers[timerIndex])
            _activeTimers.value = currentTimers
        }
    }
    
    private fun checkNotificationTriggers(timerId: String, remainingTime: Duration) {
        val settings = _notificationSettings.value
        val timer = _activeTimers.value.find { it.id == timerId } ?: return
        
        // Check for interval notifications
        if (remainingTime in settings.intervalNotifications) {
            playIntervalNotification(timerId, remainingTime)
        }
        
        // Check for countdown warning
        if (remainingTime == settings.countdownWarning) {
            playCountdownWarning(timerId)
        }
    }
    
    private fun playCompletionNotification(timerId: String) {
        val settings = _notificationSettings.value
        val timer = _activeTimers.value.find { it.id == timerId } ?: return
        
        if (settings.soundEnabled) {
            // Play completion sound
            playSound("timer_complete")
        }
        
        if (settings.vibrationEnabled) {
            // Trigger vibration
            triggerVibration(pattern = "complete")
        }
        
        if (settings.speechEnabled) {
            // Speak notification
            speak("Rest time complete for ${timer.exerciseName}")
        }
    }
    
    private fun playIntervalNotification(timerId: String, timeRemaining: Duration) {
        val settings = _notificationSettings.value
        
        if (settings.soundEnabled) {
            playSound("timer_interval")
        }
        
        if (settings.speechEnabled) {
            speak("${timeRemaining.inWholeSeconds} seconds remaining")
        }
    }
    
    private fun playCountdownWarning(timerId: String) {
        val settings = _notificationSettings.value
        
        if (settings.soundEnabled) {
            playSound("timer_warning")
        }
        
        if (settings.vibrationEnabled) {
            triggerVibration(pattern = "warning")
        }
    }
    
    private fun playSound(soundType: String) {
        // Platform-specific sound implementation
        // Would use MediaPlayer on Android
    }
    
    private fun triggerVibration(pattern: String) {
        // Platform-specific vibration implementation
        // Would use Vibrator service on Android
    }
    
    private fun speak(text: String) {
        // Platform-specific text-to-speech implementation
        // Would use TextToSpeech on Android
    }
    
    private fun isCompoundExercise(exerciseId: String): Boolean {
        // Check if exercise is compound movement
        // This would check against exercise database
        return true // Simplified
    }
    
    private fun loadDefaultPresets() {
        val defaultPresets = listOf(
            RestTimerPreset(
                name = "Strength Training",
                duration = 180.seconds,
                exerciseTypes = listOf(ExerciseCategory.POWERLIFTING),
                isDefault = true
            ),
            RestTimerPreset(
                name = "Hypertrophy",
                duration = 90.seconds,
                exerciseTypes = listOf(ExerciseCategory.CHEST, ExerciseCategory.BACK, ExerciseCategory.LEGS),
                isDefault = true
            ),
            RestTimerPreset(
                name = "Isolation",
                duration = 60.seconds,
                exerciseTypes = listOf(ExerciseCategory.BICEPS, ExerciseCategory.TRICEPS),
                isDefault = true
            ),
            RestTimerPreset(
                name = "Cardio",
                duration = 30.seconds,
                exerciseTypes = listOf(ExerciseCategory.CARDIO),
                isDefault = true
            )
        )
        
        _timerPresets.value = defaultPresets
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        timerJobs.values.forEach { it.cancel() }
        timerJobs.clear()
        scope.cancel()
    }
}