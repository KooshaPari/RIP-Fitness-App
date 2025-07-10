package com.fitnessapp.feature.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Routine Manager - Strong App inspired workout routine management
 * Features:
 * - Custom routine creation
 * - Pre-built templates (5/3/1, PPL, Upper/Lower, etc.)
 * - Routine scheduling and planning
 * - Progressive overload tracking
 * - Superset and circuit support
 * - Routine sharing and export
 */

data class WorkoutRoutine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val exercises: List<RoutineExercise> = emptyList(),
    val estimatedDuration: Duration = 60.minutes,
    val difficulty: RoutineDifficulty = RoutineDifficulty.INTERMEDIATE,
    val category: RoutineCategory = RoutineCategory.STRENGTH,
    val tags: List<String> = emptyList(),
    val isTemplate: Boolean = false,
    val isCustom: Boolean = true,
    val createdBy: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val timesUsed: Int = 0,
    val averageRating: Double = 0.0,
    val notes: String = ""
)

data class RoutineExercise(
    val id: String = UUID.randomUUID().toString(),
    val exercise: Exercise,
    val sets: Int = 3,
    val reps: String = "8-12", // Can be range "8-12" or specific "5"
    val weight: Double? = null, // Starting weight
    val restTime: Duration = 120.minutes,
    val rpe: Int? = null, // Target RPE
    val tempo: String? = null, // e.g., "3-1-2-1" (eccentric-pause-concentric-pause)
    val notes: String = "",
    val order: Int = 0,
    val isSuperset: Boolean = false,
    val supersetGroup: String? = null,
    val progression: ExerciseProgression? = null
)

data class ExerciseProgression(
    val type: ProgressionType,
    val increment: Double, // Weight increment per week/session
    val repRange: IntRange = 8..12,
    val setIncrease: Int = 0, // Additional sets when progressing
    val frequency: ProgressionFrequency = ProgressionFrequency.WEEKLY
)

enum class ProgressionType {
    LINEAR_WEIGHT, // Add weight each session/week
    DOUBLE_PROGRESSION, // Increase reps, then weight
    PERCENTAGE_BASED, // Based on 1RM percentage
    VOLUME_PROGRESSION, // Increase total volume
    DENSITY_PROGRESSION, // Same work in less time
    RPE_PROGRESSION // Based on RPE targets
}

enum class ProgressionFrequency {
    DAILY, WEEKLY, BIWEEKLY, MONTHLY
}

enum class RoutineDifficulty {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

enum class RoutineCategory {
    STRENGTH, HYPERTROPHY, POWERLIFTING, BODYBUILDING, 
    POWERBUILDING, ATHLETICISM, CONDITIONING, FLEXIBILITY
}

data class RoutineSchedule(
    val routineId: String,
    val dayOfWeek: DayOfWeek,
    val weekNumber: Int = 1, // For programs with different weeks
    val isRestDay: Boolean = false,
    val notes: String = ""
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

/**
 * Routine Manager handles workout routine creation and management
 */
class RoutineManager {
    
    private val _customRoutines = MutableStateFlow<List<WorkoutRoutine>>(emptyList())
    val customRoutines: StateFlow<List<WorkoutRoutine>> = _customRoutines.asStateFlow()
    
    private val _templateRoutines = MutableStateFlow<List<WorkoutRoutine>>(emptyList())
    val templateRoutines: StateFlow<List<WorkoutRoutine>> = _templateRoutines.asStateFlow()
    
    private val _weeklySchedule = MutableStateFlow<Map<DayOfWeek, RoutineSchedule?>>(emptyMap())
    val weeklySchedule: StateFlow<Map<DayOfWeek, RoutineSchedule?>> = _weeklySchedule.asStateFlow()
    
    private val _favoriteRoutines = MutableStateFlow<Set<String>>(emptySet())
    val favoriteRoutines: StateFlow<Set<String>> = _favoriteRoutines.asStateFlow()
    
    init {
        loadTemplateRoutines()
    }
    
    /**
     * Create a new custom routine
     */
    fun createRoutine(
        name: String,
        description: String = "",
        exercises: List<RoutineExercise> = emptyList()
    ): String {
        val routine = WorkoutRoutine(
            name = name,
            description = description,
            exercises = exercises,
            estimatedDuration = calculateEstimatedDuration(exercises),
            isCustom = true,
            createdAt = Date()
        )
        
        val currentRoutines = _customRoutines.value.toMutableList()
        currentRoutines.add(routine)
        _customRoutines.value = currentRoutines
        
        return routine.id
    }
    
    /**
     * Add exercise to routine
     */
    fun addExerciseToRoutine(
        routineId: String,
        exercise: Exercise,
        sets: Int = 3,
        reps: String = "8-12",
        restTime: Duration = 120.minutes
    ) {
        val routineExercise = RoutineExercise(
            exercise = exercise,
            sets = sets,
            reps = reps,
            restTime = restTime,
            order = getNextOrder(routineId)
        )
        
        updateRoutine(routineId) { routine ->
            routine.copy(
                exercises = routine.exercises + routineExercise,
                estimatedDuration = calculateEstimatedDuration(routine.exercises + routineExercise),
                modifiedAt = Date()
            )
        }
    }
    
    /**
     * Remove exercise from routine
     */
    fun removeExerciseFromRoutine(routineId: String, exerciseId: String) {
        updateRoutine(routineId) { routine ->
            val updatedExercises = routine.exercises.filter { it.id != exerciseId }
                .mapIndexed { index, exercise -> exercise.copy(order = index) }
            
            routine.copy(
                exercises = updatedExercises,
                estimatedDuration = calculateEstimatedDuration(updatedExercises),
                modifiedAt = Date()
            )
        }
    }
    
    /**
     * Reorder exercises in routine
     */
    fun reorderExercises(routineId: String, newOrder: List<String>) {
        updateRoutine(routineId) { routine ->
            val exerciseMap = routine.exercises.associateBy { it.id }
            val reorderedExercises = newOrder.mapIndexedNotNull { index, exerciseId ->
                exerciseMap[exerciseId]?.copy(order = index)
            }
            
            routine.copy(
                exercises = reorderedExercises,
                modifiedAt = Date()
            )
        }
    }
    
    /**
     * Create superset within routine
     */
    fun createSuperset(
        routineId: String, 
        exerciseIds: List<String>, 
        supersetName: String = "Superset"
    ) {
        updateRoutine(routineId) { routine ->
            val updatedExercises = routine.exercises.map { exercise ->
                if (exercise.id in exerciseIds) {
                    exercise.copy(
                        isSuperset = true,
                        supersetGroup = supersetName
                    )
                } else {
                    exercise
                }
            }
            
            routine.copy(
                exercises = updatedExercises,
                modifiedAt = Date()
            )
        }
    }
    
    /**
     * Set progression scheme for exercise in routine
     */
    fun setExerciseProgression(
        routineId: String,
        exerciseId: String,
        progression: ExerciseProgression
    ) {
        updateRoutine(routineId) { routine ->
            val updatedExercises = routine.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(progression = progression)
                } else {
                    exercise
                }
            }
            
            routine.copy(
                exercises = updatedExercises,
                modifiedAt = Date()
            )
        }
    }
    
    /**
     * Schedule routine for specific day
     */
    fun scheduleRoutine(routineId: String, dayOfWeek: DayOfWeek, weekNumber: Int = 1) {
        val schedule = RoutineSchedule(
            routineId = routineId,
            dayOfWeek = dayOfWeek,
            weekNumber = weekNumber
        )
        
        val currentSchedule = _weeklySchedule.value.toMutableMap()
        currentSchedule[dayOfWeek] = schedule
        _weeklySchedule.value = currentSchedule
    }
    
    /**
     * Get routine for specific day
     */
    fun getRoutineForDay(dayOfWeek: DayOfWeek): WorkoutRoutine? {
        val schedule = _weeklySchedule.value[dayOfWeek] ?: return null
        return getAllRoutines().find { it.id == schedule.routineId }
    }
    
    /**
     * Get all routines (custom + templates)
     */
    fun getAllRoutines(): List<WorkoutRoutine> {
        return _customRoutines.value + _templateRoutines.value
    }
    
    /**
     * Search routines by name or tags
     */
    fun searchRoutines(query: String): List<WorkoutRoutine> {
        return getAllRoutines().filter { routine ->
            routine.name.contains(query, ignoreCase = true) ||
            routine.description.contains(query, ignoreCase = true) ||
            routine.tags.any { it.contains(query, ignoreCase = true) }
        }
    }
    
    /**
     * Filter routines by criteria
     */
    fun filterRoutines(
        category: RoutineCategory? = null,
        difficulty: RoutineDifficulty? = null,
        maxDuration: Duration? = null,
        muscleGroups: List<MuscleGroup> = emptyList()
    ): List<WorkoutRoutine> {
        return getAllRoutines().filter { routine ->
            (category == null || routine.category == category) &&
            (difficulty == null || routine.difficulty == difficulty) &&
            (maxDuration == null || routine.estimatedDuration <= maxDuration) &&
            (muscleGroups.isEmpty() || routine.exercises.any { exercise ->
                exercise.exercise.primaryMuscles.any { it in muscleGroups }
            })
        }
    }
    
    /**
     * Toggle favorite routine
     */
    fun toggleFavorite(routineId: String) {
        val currentFavorites = _favoriteRoutines.value.toMutableSet()
        if (routineId in currentFavorites) {
            currentFavorites.remove(routineId)
        } else {
            currentFavorites.add(routineId)
        }
        _favoriteRoutines.value = currentFavorites
    }
    
    /**
     * Duplicate routine (for customization)
     */
    fun duplicateRoutine(routineId: String, newName: String): String? {
        val originalRoutine = getAllRoutines().find { it.id == routineId } ?: return null
        
        val duplicatedRoutine = originalRoutine.copy(
            id = UUID.randomUUID().toString(),
            name = newName,
            isCustom = true,
            isTemplate = false,
            createdAt = Date(),
            modifiedAt = Date(),
            timesUsed = 0
        )
        
        val currentRoutines = _customRoutines.value.toMutableList()
        currentRoutines.add(duplicatedRoutine)
        _customRoutines.value = currentRoutines
        
        return duplicatedRoutine.id
    }
    
    /**
     * Export routine to share (Strong app feature)
     */
    fun exportRoutine(routineId: String): RoutineExport? {
        val routine = getAllRoutines().find { it.id == routineId } ?: return null
        
        return RoutineExport(
            routine = routine,
            exerciseDetails = routine.exercises.map { it.exercise },
            exportDate = Date(),
            appVersion = "1.0.0"
        )
    }
    
    /**
     * Import routine from export
     */
    fun importRoutine(routineExport: RoutineExport): String {
        val importedRoutine = routineExport.routine.copy(
            id = UUID.randomUUID().toString(),
            isCustom = true,
            createdAt = Date(),
            modifiedAt = Date()
        )
        
        val currentRoutines = _customRoutines.value.toMutableList()
        currentRoutines.add(importedRoutine)
        _customRoutines.value = currentRoutines
        
        return importedRoutine.id
    }
    
    /**
     * Calculate estimated workout duration
     */
    private fun calculateEstimatedDuration(exercises: List<RoutineExercise>): Duration {
        var totalTime = 0.minutes
        
        exercises.forEach { exercise ->
            // Time per set (assume 30-60 seconds per set)
            val setTime = 45.minutes
            // Rest time between sets
            val restTime = exercise.restTime
            
            totalTime += (setTime * exercise.sets) + (restTime * (exercise.sets - 1))
        }
        
        // Add 10 minutes for setup and transitions
        return totalTime + 10.minutes
    }
    
    /**
     * Get next order number for exercise in routine
     */
    private fun getNextOrder(routineId: String): Int {
        val routine = getAllRoutines().find { it.id == routineId } ?: return 0
        return routine.exercises.maxOfOrNull { it.order }?.plus(1) ?: 0
    }
    
    /**
     * Update routine helper function
     */
    private fun updateRoutine(routineId: String, update: (WorkoutRoutine) -> WorkoutRoutine) {
        val currentRoutines = _customRoutines.value.toMutableList()
        val routineIndex = currentRoutines.indexOfFirst { it.id == routineId }
        
        if (routineIndex != -1) {
            currentRoutines[routineIndex] = update(currentRoutines[routineIndex])
            _customRoutines.value = currentRoutines
        }
    }
    
    /**
     * Load template routines (Strong app style templates)
     */
    private fun loadTemplateRoutines() {
        val templates = listOf(
            createStrongLifts5x5Template(),
            createPushPullLegsTemplate(),
            createUpperLowerTemplate(),
            createFullBodyTemplate(),
            createPowerlifting531Template()
        )
        
        _templateRoutines.value = templates
    }
    
    // Template routine creation methods
    private fun createStrongLifts5x5Template(): WorkoutRoutine {
        // This would be implemented with actual exercise data
        return WorkoutRoutine(
            name = "StrongLifts 5x5",
            description = "Classic beginner strength program focusing on compound movements",
            category = RoutineCategory.STRENGTH,
            difficulty = RoutineDifficulty.BEGINNER,
            isTemplate = true,
            isCustom = false,
            estimatedDuration = 60.minutes,
            tags = listOf("beginner", "strength", "compound")
        )
    }
    
    private fun createPushPullLegsTemplate(): WorkoutRoutine {
        return WorkoutRoutine(
            name = "Push Pull Legs",
            description = "6-day split focusing on movement patterns",
            category = RoutineCategory.HYPERTROPHY,
            difficulty = RoutineDifficulty.INTERMEDIATE,
            isTemplate = true,
            isCustom = false,
            estimatedDuration = 90.minutes,
            tags = listOf("intermediate", "hypertrophy", "split")
        )
    }
    
    private fun createUpperLowerTemplate(): WorkoutRoutine {
        return WorkoutRoutine(
            name = "Upper Lower Split",
            description = "4-day split alternating upper and lower body",
            category = RoutineCategory.HYPERTROPHY,
            difficulty = RoutineDifficulty.INTERMEDIATE,
            isTemplate = true,
            isCustom = false,
            estimatedDuration = 75.minutes,
            tags = listOf("intermediate", "split", "balanced")
        )
    }
    
    private fun createFullBodyTemplate(): WorkoutRoutine {
        return WorkoutRoutine(
            name = "Full Body Workout",
            description = "3-day full body routine for maximum efficiency",
            category = RoutineCategory.STRENGTH,
            difficulty = RoutineDifficulty.BEGINNER,
            isTemplate = true,
            isCustom = false,
            estimatedDuration = 60.minutes,
            tags = listOf("beginner", "full-body", "efficient")
        )
    }
    
    private fun createPowerlifting531Template(): WorkoutRoutine {
        return WorkoutRoutine(
            name = "5/3/1 Powerlifting",
            description = "Jim Wendler's 5/3/1 program for strength",
            category = RoutineCategory.POWERLIFTING,
            difficulty = RoutineDifficulty.ADVANCED,
            isTemplate = true,
            isCustom = false,
            estimatedDuration = 90.minutes,
            tags = listOf("advanced", "powerlifting", "percentage")
        )
    }
}

data class RoutineExport(
    val routine: WorkoutRoutine,
    val exerciseDetails: List<Exercise>,
    val exportDate: Date,
    val appVersion: String
)