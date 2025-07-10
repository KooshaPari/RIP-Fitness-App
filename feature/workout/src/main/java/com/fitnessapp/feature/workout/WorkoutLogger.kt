package com.fitnessapp.feature.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Workout Logger - Strong App inspired real-time workout tracking
 * Features:
 * - Live set/rep/weight logging
 * - Rest timer integration
 * - Auto-progression suggestions
 * - Plate calculator
 * - Previous workout data display
 * - Superset support
 */

data class WorkoutSet(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val setNumber: Int,
    val reps: Int = 0,
    val weight: Double = 0.0,
    val restTime: Duration = 0.seconds,
    val rpe: Int? = null, // Rate of Perceived Exertion (1-10)
    val notes: String = "",
    val isCompleted: Boolean = false,
    val isWarmup: Boolean = false,
    val timestamp: Date = Date(),
    val previousSetData: PreviousSetData? = null
)

data class PreviousSetData(
    val weight: Double,
    val reps: Int,
    val date: Date
)

data class WorkoutExercise(
    val id: String = UUID.randomUUID().toString(),
    val exercise: Exercise,
    val sets: List<WorkoutSet> = emptyList(),
    val targetSets: Int = 3,
    val targetReps: String = "8-12", // Can be range like "8-12" or specific like "5"
    val targetWeight: Double? = null,
    val restTime: Duration = 120.seconds,
    val isSuperset: Boolean = false,
    val supersetGroup: String? = null,
    val notes: String = "",
    val order: Int = 0
)

data class ActiveWorkout(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exercises: List<WorkoutExercise> = emptyList(),
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val totalDuration: Duration = 0.seconds,
    val totalVolume: Double = 0.0, // Total weight x reps
    val notes: String = "",
    val templateId: String? = null,
    val isCompleted: Boolean = false
)

/**
 * Workout Logger handles real-time workout tracking
 * Provides Strong app functionality for logging exercises
 */
class WorkoutLogger {
    
    private val _activeWorkout = MutableStateFlow<ActiveWorkout?>(null)
    val activeWorkout: StateFlow<ActiveWorkout?> = _activeWorkout.asStateFlow()
    
    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()
    
    private val _currentSetIndex = MutableStateFlow(0)
    val currentSetIndex: StateFlow<Int> = _currentSetIndex.asStateFlow()
    
    private val _isRestTimerActive = MutableStateFlow(false)
    val isRestTimerActive: StateFlow<Boolean> = _isRestTimerActive.asStateFlow()
    
    private val _restTimeRemaining = MutableStateFlow(0.seconds)
    val restTimeRemaining: StateFlow<Duration> = _restTimeRemaining.asStateFlow()
    
    private val _plateCalculation = MutableStateFlow<PlateCalculation?>(null)
    val plateCalculation: StateFlow<PlateCalculation?> = _plateCalculation.asStateFlow()
    
    /**
     * Start a new workout
     */
    fun startWorkout(workoutName: String, templateId: String? = null) {
        val workout = ActiveWorkout(
            name = workoutName,
            templateId = templateId,
            startTime = Date()
        )
        _activeWorkout.value = workout
        _currentExerciseIndex.value = 0
        _currentSetIndex.value = 0
    }
    
    /**
     * Add exercise to current workout
     */
    fun addExercise(exercise: Exercise, targetSets: Int = 3, targetReps: String = "8-12") {
        val currentWorkout = _activeWorkout.value ?: return
        
        val workoutExercise = WorkoutExercise(
            exercise = exercise,
            targetSets = targetSets,
            targetReps = targetReps,
            order = currentWorkout.exercises.size,
            sets = (1..targetSets).map { setNumber ->
                WorkoutSet(
                    exerciseId = exercise.id,
                    setNumber = setNumber,
                    previousSetData = getPreviousSetData(exercise.id, setNumber)
                )
            }
        )
        
        val updatedExercises = currentWorkout.exercises + workoutExercise
        _activeWorkout.value = currentWorkout.copy(exercises = updatedExercises)
    }
    
    /**
     * Log a set (Strong app style quick logging)
     */
    fun logSet(
        exerciseIndex: Int,
        setIndex: Int,
        weight: Double,
        reps: Int,
        rpe: Int? = null,
        notes: String = ""
    ) {
        val currentWorkout = _activeWorkout.value ?: return
        val exercises = currentWorkout.exercises.toMutableList()
        
        if (exerciseIndex >= exercises.size || setIndex >= exercises[exerciseIndex].sets.size) return
        
        val exercise = exercises[exerciseIndex]
        val sets = exercise.sets.toMutableList()
        
        val updatedSet = sets[setIndex].copy(
            weight = weight,
            reps = reps,
            rpe = rpe,
            notes = notes,
            isCompleted = true,
            timestamp = Date()
        )
        
        sets[setIndex] = updatedSet
        exercises[exerciseIndex] = exercise.copy(sets = sets)
        
        val updatedWorkout = currentWorkout.copy(
            exercises = exercises,
            totalVolume = calculateTotalVolume(exercises)
        )
        
        _activeWorkout.value = updatedWorkout
        
        // Auto-start rest timer
        startRestTimer(exercise.restTime)
        
        // Calculate plates for next set
        calculatePlates(weight)
    }
    
    /**
     * Start rest timer (Strong app feature)
     */
    fun startRestTimer(duration: Duration) {
        _isRestTimerActive.value = true
        _restTimeRemaining.value = duration
        
        // In a real implementation, this would use a Timer or Coroutine
        // to countdown and notify when rest is complete
    }
    
    /**
     * Skip rest timer
     */
    fun skipRestTimer() {
        _isRestTimerActive.value = false
        _restTimeRemaining.value = 0.seconds
    }
    
    /**
     * Add time to rest timer
     */
    fun addRestTime(additionalTime: Duration) {
        _restTimeRemaining.value = _restTimeRemaining.value + additionalTime
    }
    
    /**
     * Calculate plates needed for barbell (Strong app feature)
     */
    fun calculatePlates(weight: Double, barbellWeight: Double = 45.0) {
        val plateCalculation = PlateCalculator.calculatePlates(weight, barbellWeight)
        _plateCalculation.value = plateCalculation
    }
    
    /**
     * Move to next set
     */
    fun nextSet() {
        val currentWorkout = _activeWorkout.value ?: return
        val currentExercise = currentWorkout.exercises.getOrNull(_currentExerciseIndex.value) ?: return
        
        if (_currentSetIndex.value < currentExercise.sets.size - 1) {
            _currentSetIndex.value = _currentSetIndex.value + 1
        } else {
            nextExercise()
        }
    }
    
    /**
     * Move to next exercise
     */
    fun nextExercise() {
        val currentWorkout = _activeWorkout.value ?: return
        
        if (_currentExerciseIndex.value < currentWorkout.exercises.size - 1) {
            _currentExerciseIndex.value = _currentExerciseIndex.value + 1
            _currentSetIndex.value = 0
        }
    }
    
    /**
     * Complete current workout
     */
    fun completeWorkout(): ActiveWorkout? {
        val currentWorkout = _activeWorkout.value ?: return null
        
        val completedWorkout = currentWorkout.copy(
            endTime = Date(),
            totalDuration = Duration.between(
                currentWorkout.startTime.toInstant(),
                Date().toInstant()
            ),
            isCompleted = true
        )
        
        _activeWorkout.value = null
        _currentExerciseIndex.value = 0
        _currentSetIndex.value = 0
        _isRestTimerActive.value = false
        
        return completedWorkout
    }
    
    /**
     * Add superset exercises
     */
    fun createSuperset(exerciseIndices: List<Int>, supersetName: String = "Superset") {
        val currentWorkout = _activeWorkout.value ?: return
        val exercises = currentWorkout.exercises.toMutableList()
        
        exerciseIndices.forEach { index ->
            if (index < exercises.size) {
                exercises[index] = exercises[index].copy(
                    isSuperset = true,
                    supersetGroup = supersetName
                )
            }
        }
        
        _activeWorkout.value = currentWorkout.copy(exercises = exercises)
    }
    
    /**
     * Get previous set data for auto-suggestion
     */
    private fun getPreviousSetData(exerciseId: String, setNumber: Int): PreviousSetData? {
        // In a real implementation, this would query the database
        // for the user's last performance of this exercise/set
        return null
    }
    
    /**
     * Calculate total workout volume
     */
    private fun calculateTotalVolume(exercises: List<WorkoutExercise>): Double {
        return exercises.sumOf { exercise ->
            exercise.sets.filter { it.isCompleted }.sumOf { set ->
                set.weight * set.reps
            }
        }
    }
    
    /**
     * Get workout suggestions based on previous performance
     */
    fun getWorkoutSuggestions(): WorkoutSuggestions {
        val currentWorkout = _activeWorkout.value
        return WorkoutSuggestions(
            suggestedWeight = calculateSuggestedWeight(),
            suggestedReps = calculateSuggestedReps(),
            estimatedOneRepMax = calculateEstimatedOneRepMax()
        )
    }
    
    private fun calculateSuggestedWeight(): Double {
        // Implement progressive overload suggestion
        return 0.0
    }
    
    private fun calculateSuggestedReps(): Int {
        // Implement rep suggestion based on strength progression
        return 0
    }
    
    private fun calculateEstimatedOneRepMax(): Double {
        // Calculate 1RM using Brzycki formula or similar
        return 0.0
    }
}

data class WorkoutSuggestions(
    val suggestedWeight: Double,
    val suggestedReps: Int,
    val estimatedOneRepMax: Double
)

data class PlateCalculation(
    val totalWeight: Double,
    val platesPerSide: Map<Double, Int> // plate weight to count
)

/**
 * Plate Calculator for barbell exercises (Strong app feature)
 */
object PlateCalculator {
    
    private val availablePlates = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5) // Standard gym plates
    
    fun calculatePlates(targetWeight: Double, barbellWeight: Double = 45.0): PlateCalculation {
        val weightPerSide = (targetWeight - barbellWeight) / 2.0
        val platesPerSide = mutableMapOf<Double, Int>()
        
        var remainingWeight = weightPerSide
        
        for (plateWeight in availablePlates.sortedDescending()) {
            val plateCount = (remainingWeight / plateWeight).toInt()
            if (plateCount > 0) {
                platesPerSide[plateWeight] = plateCount
                remainingWeight -= plateCount * plateWeight
            }
        }
        
        return PlateCalculation(
            totalWeight = targetWeight,
            platesPerSide = platesPerSide
        )
    }
}