package com.fitnessapp.feature.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Watch Integration - Strong App inspired companion device support
 * Features:
 * - Real-time workout syncing
 * - Heart rate monitoring
 * - Quick exercise logging
 * - Rest timer on wrist
 * - Workout controls (start/pause/stop)
 * - Haptic feedback for form guidance
 * - Voice commands for hands-free logging
 * 
 * Note: For Android, this simulates Apple Watch functionality
 * In a real implementation, this would integrate with Wear OS
 */

data class WatchWorkoutData(
    val workoutId: String,
    val exerciseName: String,
    val currentSet: Int,
    val targetSets: Int,
    val lastSetWeight: Double,
    val lastSetReps: Int,
    val restTimeRemaining: Duration,
    val isRestTimerActive: Boolean,
    val heartRate: Int? = null,
    val workoutDuration: Duration,
    val totalVolume: Double
)

data class WatchExerciseEntry(
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val setNumber: Int,
    val timestamp: Date = Date(),
    val heartRate: Int? = null,
    val isQuickEntry: Boolean = true
)

data class WatchHeartRateData(
    val timestamp: Date,
    val heartRate: Int,
    val zone: HeartRateZone,
    val confidence: Double = 1.0
)

enum class HeartRateZone {
    RESTING, FAT_BURN, CARDIO, PEAK, MAXIMUM
}

data class WatchNotification(
    val id: String = UUID.randomUUID().toString(),
    val type: WatchNotificationType,
    val title: String,
    val message: String,
    val hapticPattern: HapticPattern = HapticPattern.SINGLE_TAP,
    val timestamp: Date = Date(),
    val isRead: Boolean = false
)

enum class WatchNotificationType {
    REST_TIMER_COMPLETE, EXERCISE_SUGGESTION, FORM_REMINDER, 
    PERSONAL_RECORD, WORKOUT_MILESTONE, HYDRATION_REMINDER
}

enum class HapticPattern {
    SINGLE_TAP, DOUBLE_TAP, TRIPLE_TAP, LONG_PRESS, 
    SUCCESS_PATTERN, WARNING_PATTERN, ERROR_PATTERN
}

data class VoiceCommand(
    val command: String,
    val parameters: Map<String, Any>,
    val confidence: Double,
    val timestamp: Date = Date()
)

/**
 * Watch Integration handles companion device communication and features
 */
class WatchIntegration {
    
    private val _isWatchConnected = MutableStateFlow(false)
    val isWatchConnected: StateFlow<Boolean> = _isWatchConnected.asStateFlow()
    
    private val _watchWorkoutData = MutableStateFlow<WatchWorkoutData?>(null)
    val watchWorkoutData: StateFlow<WatchWorkoutData?> = _watchWorkoutData.asStateFlow()
    
    private val _heartRateData = MutableStateFlow<List<WatchHeartRateData>>(emptyList())
    val heartRateData: StateFlow<List<WatchHeartRateData>> = _heartRateData.asStateFlow()
    
    private val _watchNotifications = MutableStateFlow<List<WatchNotification>>(emptyList())
    val watchNotifications: StateFlow<List<WatchNotification>> = _watchNotifications.asStateFlow()
    
    private val _quickEntryHistory = MutableStateFlow<List<WatchExerciseEntry>>(emptyList())
    val quickEntryHistory: StateFlow<List<WatchExerciseEntry>> = _quickEntryHistory.asStateFlow()
    
    private val _voiceCommands = MutableStateFlow<List<VoiceCommand>>(emptyList())
    val voiceCommands: StateFlow<List<VoiceCommand>> = _voiceCommands.asStateFlow()
    
    /**
     * Initialize watch connection (simulated for Android)
     */
    fun initializeWatchConnection() {
        // In real implementation, this would:
        // - Discover nearby watch devices
        // - Establish Bluetooth/WiFi connection
        // - Sync workout app to watch
        
        _isWatchConnected.value = true
        sendWatchNotification(
            WatchNotificationType.WORKOUT_MILESTONE,
            "Watch Connected",
            "Ready for workout tracking"
        )
    }
    
    /**
     * Start workout session on watch
     */
    fun startWatchWorkout(workout: ActiveWorkout) {
        if (!_isWatchConnected.value) return
        
        val watchData = WatchWorkoutData(
            workoutId = workout.id,
            exerciseName = workout.exercises.firstOrNull()?.exercise?.name ?: "Workout",
            currentSet = 1,
            targetSets = workout.exercises.firstOrNull()?.targetSets ?: 3,
            lastSetWeight = 0.0,
            lastSetReps = 0,
            restTimeRemaining = 0.seconds,
            isRestTimerActive = false,
            workoutDuration = Duration.between(workout.startTime.toInstant(), Date().toInstant()),
            totalVolume = workout.totalVolume
        )
        
        _watchWorkoutData.value = watchData
        
        // Start heart rate monitoring
        startHeartRateMonitoring()
        
        sendWatchNotification(
            WatchNotificationType.WORKOUT_MILESTONE,
            "Workout Started",
            "Let's get strong!",
            HapticPattern.SUCCESS_PATTERN
        )
    }
    
    /**
     * Update watch with current exercise data
     */
    fun updateWatchExercise(
        exerciseName: String,
        currentSet: Int,
        targetSets: Int,
        lastWeight: Double,
        lastReps: Int
    ) {
        val currentData = _watchWorkoutData.value ?: return
        
        _watchWorkoutData.value = currentData.copy(
            exerciseName = exerciseName,
            currentSet = currentSet,
            targetSets = targetSets,
            lastSetWeight = lastWeight,
            lastSetReps = lastReps
        )
        
        // Send haptic feedback for set completion
        if (currentSet > currentData.currentSet) {
            sendHapticFeedback(HapticPattern.SUCCESS_PATTERN)
        }
    }
    
    /**
     * Update watch rest timer
     */
    fun updateWatchRestTimer(remainingTime: Duration, isActive: Boolean) {
        val currentData = _watchWorkoutData.value ?: return
        
        _watchWorkoutData.value = currentData.copy(
            restTimeRemaining = remainingTime,
            isRestTimerActive = isActive
        )
        
        // Send notification when rest timer completes
        if (remainingTime <= 0.seconds && isActive) {
            sendWatchNotification(
                WatchNotificationType.REST_TIMER_COMPLETE,
                "Rest Complete",
                "Ready for next set!",
                HapticPattern.DOUBLE_TAP
            )
        }
    }
    
    /**
     * Quick log exercise from watch (Strong app feature)
     */
    fun quickLogFromWatch(
        exerciseId: String,
        exerciseName: String,
        weight: Double,
        reps: Int,
        setNumber: Int
    ): WatchExerciseEntry {
        val entry = WatchExerciseEntry(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            weight = weight,
            reps = reps,
            setNumber = setNumber,
            heartRate = getCurrentHeartRate()
        )
        
        val currentHistory = _quickEntryHistory.value.toMutableList()
        currentHistory.add(0, entry) // Add to beginning
        _quickEntryHistory.value = currentHistory.take(50) // Keep last 50 entries
        
        // Send confirmation feedback
        sendHapticFeedback(HapticPattern.SUCCESS_PATTERN)
        
        return entry
    }
    
    /**
     * Process voice command from watch
     */
    fun processVoiceCommand(command: String): VoiceCommand? {
        val processedCommand = parseVoiceCommand(command)
        
        if (processedCommand != null) {
            val currentCommands = _voiceCommands.value.toMutableList()
            currentCommands.add(0, processedCommand)
            _voiceCommands.value = currentCommands.take(20)
            
            // Execute command if confidence is high enough
            if (processedCommand.confidence > 0.7) {
                executeVoiceCommand(processedCommand)
            }
        }
        
        return processedCommand
    }
    
    /**
     * Start heart rate monitoring
     */
    fun startHeartRateMonitoring() {
        if (!_isWatchConnected.value) return
        
        // In real implementation, this would:
        // - Start heart rate sensor on watch
        // - Stream data continuously
        // - Calculate heart rate zones
        
        // Simulated heart rate data
        generateSimulatedHeartRateData()
    }
    
    /**
     * Get current heart rate
     */
    fun getCurrentHeartRate(): Int? {
        return _heartRateData.value.lastOrNull()?.heartRate
    }
    
    /**
     * Get heart rate zone analysis
     */
    fun getHeartRateZoneAnalysis(duration: Duration = Duration.ofMinutes(60)): Map<HeartRateZone, Duration> {
        val cutoffTime = Date(System.currentTimeMillis() - duration.toMillis())
        val recentData = _heartRateData.value.filter { it.timestamp.after(cutoffTime) }
        
        return recentData.groupBy { it.zone }
            .mapValues { (_, data) -> Duration.ofSeconds(data.size.toLong()) }
    }
    
    /**
     * Send notification to watch
     */
    fun sendWatchNotification(
        type: WatchNotificationType,
        title: String,
        message: String,
        hapticPattern: HapticPattern = HapticPattern.SINGLE_TAP
    ) {
        if (!_isWatchConnected.value) return
        
        val notification = WatchNotification(
            type = type,
            title = title,
            message = message,
            hapticPattern = hapticPattern
        )
        
        val currentNotifications = _watchNotifications.value.toMutableList()
        currentNotifications.add(0, notification)
        _watchNotifications.value = currentNotifications.take(10) // Keep last 10
        
        // Send haptic feedback
        sendHapticFeedback(hapticPattern)
    }
    
    /**
     * Send haptic feedback to watch
     */
    fun sendHapticFeedback(pattern: HapticPattern) {
        if (!_isWatchConnected.value) return
        
        // In real implementation, this would send haptic commands to watch
        // Platform-specific implementation for Wear OS vibration patterns
    }
    
    /**
     * Mark notification as read
     */
    fun markNotificationAsRead(notificationId: String) {
        val notifications = _watchNotifications.value.toMutableList()
        val index = notifications.indexOfFirst { it.id == notificationId }
        
        if (index != -1) {
            notifications[index] = notifications[index].copy(isRead = true)
            _watchNotifications.value = notifications
        }
    }
    
    /**
     * Get workout summary for watch display
     */
    fun getWatchWorkoutSummary(): WatchWorkoutSummary {
        val currentData = _watchWorkoutData.value
        val heartRateStats = getHeartRateStats()
        
        return WatchWorkoutSummary(
            workoutDuration = currentData?.workoutDuration ?: 0.seconds,
            totalVolume = currentData?.totalVolume ?: 0.0,
            averageHeartRate = heartRateStats.average.toInt(),
            maxHeartRate = heartRateStats.max,
            caloriesBurned = estimateCaloriesBurned(),
            totalSets = getTotalSetsCompleted()
        )
    }
    
    /**
     * Sync quick entries back to main app
     */
    fun syncQuickEntriesToMainApp(): List<WatchExerciseEntry> {
        val entriesToSync = _quickEntryHistory.value.filter { !it.isQuickEntry }
        
        // Mark entries as synced
        val updatedHistory = _quickEntryHistory.value.map { entry ->
            if (entry in entriesToSync) {
                entry.copy(isQuickEntry = false)
            } else entry
        }
        _quickEntryHistory.value = updatedHistory
        
        return entriesToSync
    }
    
    /**
     * Handle watch disconnection
     */
    fun handleWatchDisconnection() {
        _isWatchConnected.value = false
        _watchWorkoutData.value = null
        
        // Save any pending data
        syncQuickEntriesToMainApp()
    }
    
    /**
     * Get watch complication data (for watch face display)
     */
    fun getWatchComplicationData(): WatchComplicationData {
        val currentData = _watchWorkoutData.value
        val heartRate = getCurrentHeartRate()
        
        return WatchComplicationData(
            mainText = currentData?.exerciseName ?: "No Workout",
            subText = if (currentData != null) {
                "Set ${currentData.currentSet}/${currentData.targetSets}"
            } else "",
            heartRate = heartRate,
            restTimeRemaining = currentData?.restTimeRemaining?.inWholeSeconds?.toInt() ?: 0
        )
    }
    
    // Helper methods
    private fun parseVoiceCommand(command: String): VoiceCommand? {
        val lowercaseCommand = command.lowercase()
        
        return when {
            lowercaseCommand.contains("log") && lowercaseCommand.contains("set") -> {
                // Parse "log set 135 pounds 8 reps"
                val weightMatch = Regex("""(\d+(?:\.\d+)?)\s*(?:pounds?|lbs?|kg|kilograms?)""").find(lowercaseCommand)
                val repsMatch = Regex("""(\d+)\s*(?:reps?|repetitions?)""").find(lowercaseCommand)
                
                if (weightMatch != null && repsMatch != null) {
                    VoiceCommand(
                        command = "log_set",
                        parameters = mapOf(
                            "weight" to weightMatch.groupValues[1].toDouble(),
                            "reps" to repsMatch.groupValues[1].toInt()
                        ),
                        confidence = 0.9
                    )
                } else null
            }
            lowercaseCommand.contains("start") && lowercaseCommand.contains("timer") -> {
                VoiceCommand(
                    command = "start_timer",
                    parameters = emptyMap(),
                    confidence = 0.85
                )
            }
            lowercaseCommand.contains("next") && lowercaseCommand.contains("exercise") -> {
                VoiceCommand(
                    command = "next_exercise",
                    parameters = emptyMap(),
                    confidence = 0.8
                )
            }
            else -> null
        }
    }
    
    private fun executeVoiceCommand(command: VoiceCommand) {
        when (command.command) {
            "log_set" -> {
                val weight = command.parameters["weight"] as? Double ?: return
                val reps = command.parameters["reps"] as? Int ?: return
                
                // This would trigger the main app to log the set
                sendWatchNotification(
                    WatchNotificationType.WORKOUT_MILESTONE,
                    "Set Logged",
                    "$weight lbs x $reps reps"
                )
            }
            "start_timer" -> {
                // This would trigger rest timer start
                sendWatchNotification(
                    WatchNotificationType.REST_TIMER_COMPLETE,
                    "Timer Started",
                    "Rest timer activated"
                )
            }
            "next_exercise" -> {
                // This would move to next exercise
                sendWatchNotification(
                    WatchNotificationType.EXERCISE_SUGGESTION,
                    "Next Exercise",
                    "Moving to next exercise"
                )
            }
        }
    }
    
    private fun generateSimulatedHeartRateData() {
        // Simulate heart rate data for demonstration
        val baseHeartRate = 70
        val workoutHeartRate = 140
        
        val heartRateData = WatchHeartRateData(
            timestamp = Date(),
            heartRate = workoutHeartRate + (-10..10).random(),
            zone = calculateHeartRateZone(workoutHeartRate)
        )
        
        val currentData = _heartRateData.value.toMutableList()
        currentData.add(heartRateData)
        _heartRateData.value = currentData.takeLast(1000) // Keep last 1000 readings
    }
    
    private fun calculateHeartRateZone(heartRate: Int): HeartRateZone {
        // Simplified heart rate zone calculation
        return when {
            heartRate < 100 -> HeartRateZone.RESTING
            heartRate < 120 -> HeartRateZone.FAT_BURN
            heartRate < 150 -> HeartRateZone.CARDIO
            heartRate < 180 -> HeartRateZone.PEAK
            else -> HeartRateZone.MAXIMUM
        }
    }
    
    private fun getHeartRateStats(): HeartRateStats {
        val heartRates = _heartRateData.value.map { it.heartRate }
        
        return HeartRateStats(
            average = heartRates.average(),
            min = heartRates.minOrNull() ?: 0,
            max = heartRates.maxOrNull() ?: 0,
            current = heartRates.lastOrNull() ?: 0
        )
    }
    
    private fun estimateCaloriesBurned(): Int {
        val currentData = _watchWorkoutData.value ?: return 0
        val durationMinutes = currentData.workoutDuration.inWholeMinutes
        
        // Simplified calorie calculation
        // Real implementation would use heart rate data, body weight, etc.
        return (durationMinutes * 8).toInt() // ~8 calories per minute for strength training
    }
    
    private fun getTotalSetsCompleted(): Int {
        return _quickEntryHistory.value.distinctBy { "${it.exerciseId}_${it.setNumber}" }.size
    }
}

data class WatchWorkoutSummary(
    val workoutDuration: Duration,
    val totalVolume: Double,
    val averageHeartRate: Int,
    val maxHeartRate: Int,
    val caloriesBurned: Int,
    val totalSets: Int
)

data class WatchComplicationData(
    val mainText: String,
    val subText: String,
    val heartRate: Int?,
    val restTimeRemaining: Int
)

data class HeartRateStats(
    val average: Double,
    val min: Int,
    val max: Int,
    val current: Int
)