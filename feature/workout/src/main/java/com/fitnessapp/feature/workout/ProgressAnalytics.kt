package com.fitnessapp.feature.workout

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

/**
 * Progress Analytics - Strong App inspired advanced progress tracking
 * Features:
 * - 1RM calculations (Brzycki, Epley, McGlothin formulas)
 * - Volume progression tracking
 * - Personal records tracking
 * - Strength standards comparison
 * - Progress graphs and charts
 * - Body weight correlation
 * - Training load analysis
 */

data class PersonalRecord(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val exerciseName: String,
    val type: PRType,
    val value: Double, // Weight for strength PRs, time for endurance, etc.
    val reps: Int = 1,
    val bodyWeight: Double? = null,
    val date: Date = Date(),
    val workoutId: String,
    val notes: String = ""
)

enum class PRType {
    ONE_REP_MAX, THREE_REP_MAX, FIVE_REP_MAX, TEN_REP_MAX,
    MAX_VOLUME, MAX_REPS, LONGEST_SET, FASTEST_TIME,
    BODYWEIGHT_RATIO, WILKS_SCORE
}

data class OneRepMaxCalculation(
    val exerciseId: String,
    val estimated1RM: Double,
    val weight: Double,
    val reps: Int,
    val formula: OneRMFormula,
    val date: Date,
    val confidence: Double // 0.0 to 1.0 based on rep range accuracy
)

enum class OneRMFormula {
    BRZYCKI, // 1RM = weight / (1.0278 - 0.0278 × reps)
    EPLEY, // 1RM = weight × (1 + reps/30)
    MCGLOTHIN, // 1RM = 100 × weight / (101.3 - 2.67123 × reps)
    LOMBARDI, // 1RM = weight × reps^0.10
    MAYHEW, // 1RM = 100 × weight / (52.2 + 41.9 × e^(-0.055 × reps))
    OCONNER, // 1RM = weight × (1 + 0.025 × reps)
    WATHEN // 1RM = 100 × weight / (48.8 + 53.8 × e^(-0.075 × reps))
}

data class VolumeProgression(
    val date: Date,
    val totalVolume: Double, // Weight × Reps × Sets
    val workoutVolume: Double, // Volume for specific workout
    val exerciseVolume: Map<String, Double>, // Volume per exercise
    val intensityLoad: Double, // Average % of 1RM used
    val tonnage: Double // Total weight moved
)

data class StrengthProgression(
    val exerciseId: String,
    val exerciseName: String,
    val measurements: List<StrengthMeasurement>,
    val currentEstimated1RM: Double,
    val strengthGain: Double, // % increase over time period
    val velocityOfGain: Double, // Strength gain per week
    val plateau: PlateauAnalysis?
)

data class StrengthMeasurement(
    val date: Date,
    val weight: Double,
    val reps: Int,
    val estimated1RM: Double,
    val rpe: Int?,
    val bodyWeight: Double?
)

data class PlateauAnalysis(
    val detectedDate: Date,
    val durationDays: Int,
    val suggestion: String,
    val confidence: Double
)

data class TrainingLoad(
    val date: Date,
    val acuteLoad: Double, // 7-day average
    val chronicLoad: Double, // 28-day average
    val acuteChronicRatio: Double, // Injury risk indicator
    val totalSets: Int,
    val totalReps: Int,
    val averageIntensity: Double
)

data class BodyweightCorrelation(
    val exerciseId: String,
    val bodyweightMultiplier: Double, // e.g., 1.5x bodyweight squat
    val strengthToWeightRatio: Double,
    val progressionRate: Double,
    val strengthStandard: StrengthStandard
)

enum class StrengthStandard {
    UNTRAINED, NOVICE, INTERMEDIATE, ADVANCED, ELITE
}

/**
 * Progress Analytics handles all workout progress calculations and tracking
 */
class ProgressAnalytics {
    
    private val _personalRecords = MutableStateFlow<List<PersonalRecord>>(emptyList())
    val personalRecords: StateFlow<List<PersonalRecord>> = _personalRecords.asStateFlow()
    
    private val _volumeProgression = MutableStateFlow<List<VolumeProgression>>(emptyList())
    val volumeProgression: StateFlow<List<VolumeProgression>> = _volumeProgression.asStateFlow()
    
    private val _strengthProgression = MutableStateFlow<Map<String, StrengthProgression>>(emptyMap())
    val strengthProgression: StateFlow<Map<String, StrengthProgression>> = _strengthProgression.asStateFlow()
    
    private val _trainingLoad = MutableStateFlow<List<TrainingLoad>>(emptyList())
    val trainingLoad: StateFlow<List<TrainingLoad>> = _trainingLoad.asStateFlow()
    
    /**
     * Calculate 1RM using multiple formulas (Strong app feature)
     */
    fun calculateOneRepMax(
        weight: Double, 
        reps: Int, 
        formula: OneRMFormula = OneRMFormula.BRZYCKI
    ): Double {
        return when (formula) {
            OneRMFormula.BRZYCKI -> {
                if (reps == 1) weight else weight / (1.0278 - 0.0278 * reps)
            }
            OneRMFormula.EPLEY -> {
                weight * (1 + reps / 30.0)
            }
            OneRMFormula.MCGLOTHIN -> {
                100 * weight / (101.3 - 2.67123 * reps)
            }
            OneRMFormula.LOMBARDI -> {
                weight * reps.toDouble().pow(0.10)
            }
            OneRMFormula.OCONNER -> {
                weight * (1 + 0.025 * reps)
            }
            OneRMFormula.WATHEN -> {
                100 * weight / (48.8 + 53.8 * kotlin.math.exp(-0.075 * reps))
            }
            OneRMFormula.MAYHEW -> {
                100 * weight / (52.2 + 41.9 * kotlin.math.exp(-0.055 * reps))
            }
        }
    }
    
    /**
     * Get best estimated 1RM using multiple formulas
     */
    fun getBestEstimated1RM(weight: Double, reps: Int): OneRepMaxCalculation {
        val calculations = OneRMFormula.values().map { formula ->
            val estimated1RM = calculateOneRepMax(weight, reps, formula)
            val confidence = getFormulaConfidence(reps, formula)
            
            OneRepMaxCalculation(
                exerciseId = "",
                estimated1RM = estimated1RM,
                weight = weight,
                reps = reps,
                formula = formula,
                date = Date(),
                confidence = confidence
            )
        }
        
        // Return calculation with highest confidence for rep range
        return calculations.maxByOrNull { it.confidence }!!
    }
    
    /**
     * Update personal records after workout
     */
    fun updatePersonalRecords(completedWorkout: ActiveWorkout) {
        val newPRs = mutableListOf<PersonalRecord>()
        
        completedWorkout.exercises.forEach { workoutExercise ->
            workoutExercise.sets.filter { it.isCompleted }.forEach { set ->
                // Check for various PR types
                checkForPR(workoutExercise.exercise, set, completedWorkout.id)?.let { pr ->
                    newPRs.add(pr)
                }
            }
        }
        
        if (newPRs.isNotEmpty()) {
            val currentPRs = _personalRecords.value.toMutableList()
            currentPRs.addAll(newPRs)
            _personalRecords.value = currentPRs
        }
        
        // Update strength progressions
        updateStrengthProgressions(completedWorkout)
        
        // Update volume progression
        updateVolumeProgression(completedWorkout)
        
        // Update training load
        updateTrainingLoad(completedWorkout)
    }
    
    /**
     * Check if a set represents a new personal record
     */
    private fun checkForPR(exercise: Exercise, set: WorkoutSet, workoutId: String): PersonalRecord? {
        val existingPRs = _personalRecords.value.filter { it.exerciseId == exercise.id }
        
        // Check 1RM PR
        val estimated1RM = calculateOneRepMax(set.weight, set.reps)
        val current1RMPR = existingPRs.filter { it.type == PRType.ONE_REP_MAX }.maxByOrNull { it.value }
        
        if (current1RMPR == null || estimated1RM > current1RMPR.value) {
            return PersonalRecord(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                type = PRType.ONE_REP_MAX,
                value = estimated1RM,
                reps = set.reps,
                date = set.timestamp,
                workoutId = workoutId
            )
        }
        
        // Check max volume PR (weight × reps)
        val volume = set.weight * set.reps
        val currentVolumePR = existingPRs.filter { it.type == PRType.MAX_VOLUME }.maxByOrNull { it.value }
        
        if (currentVolumePR == null || volume > currentVolumePR.value) {
            return PersonalRecord(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                type = PRType.MAX_VOLUME,
                value = volume,
                reps = set.reps,
                date = set.timestamp,
                workoutId = workoutId
            )
        }
        
        // Check rep PRs for specific weights
        val maxReps = existingPRs.filter { 
            it.type == PRType.MAX_REPS && kotlin.math.abs(it.value - set.weight) < 0.1 
        }.maxByOrNull { it.reps }
        
        if (maxReps == null || set.reps > maxReps.reps) {
            return PersonalRecord(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                type = PRType.MAX_REPS,
                value = set.weight,
                reps = set.reps,
                date = set.timestamp,
                workoutId = workoutId
            )
        }
        
        return null
    }
    
    /**
     * Get strength progression for exercise
     */
    fun getStrengthProgression(exerciseId: String): StrengthProgression? {
        return _strengthProgression.value[exerciseId]
    }
    
    /**
     * Get volume progression over time period
     */
    fun getVolumeProgression(days: Int = 90): List<VolumeProgression> {
        val cutoffDate = Date(System.currentTimeMillis() - days.days.inWholeMilliseconds)
        return _volumeProgression.value.filter { it.date.after(cutoffDate) }
    }
    
    /**
     * Calculate bodyweight ratios for strength standards
     */
    fun calculateBodyweightRatios(bodyWeight: Double): Map<String, BodyweightCorrelation> {
        return _personalRecords.value
            .filter { it.type == PRType.ONE_REP_MAX }
            .groupBy { it.exerciseId }
            .mapValues { (exerciseId, prs) ->
                val maxPR = prs.maxByOrNull { it.value }!!
                val multiplier = maxPR.value / bodyWeight
                
                BodyweightCorrelation(
                    exerciseId = exerciseId,
                    bodyweightMultiplier = multiplier,
                    strengthToWeightRatio = multiplier,
                    progressionRate = calculateProgressionRate(exerciseId),
                    strengthStandard = getStrengthStandard(exerciseId, multiplier)
                )
            }
    }
    
    /**
     * Detect training plateaus
     */
    fun detectPlateaus(): List<PlateauAnalysis> {
        return _strengthProgression.value.values.mapNotNull { progression ->
            val recentMeasurements = progression.measurements.takeLast(8) // Last 8 workouts
            
            if (recentMeasurements.size >= 6) {
                val trend = calculateTrend(recentMeasurements.map { it.estimated1RM })
                
                if (kotlin.math.abs(trend) < 0.5) { // Less than 0.5% improvement
                    PlateauAnalysis(
                        detectedDate = Date(),
                        durationDays = 28, // Estimate
                        suggestion = generatePlateauSuggestion(progression),
                        confidence = 0.8
                    )
                } else null
            } else null
        }
    }
    
    /**
     * Calculate training load and injury risk
     */
    fun getCurrentTrainingLoad(): TrainingLoad? {
        return _trainingLoad.value.lastOrNull()
    }
    
    /**
     * Get workout summary with PRs and progress
     */
    fun getWorkoutSummary(workoutId: String): WorkoutSummary {
        val prs = _personalRecords.value.filter { it.workoutId == workoutId }
        val volumeData = _volumeProgression.value.find { 
            // This would need workout date correlation in real implementation
            true 
        }
        
        return WorkoutSummary(
            workoutId = workoutId,
            personalRecords = prs,
            totalVolume = volumeData?.totalVolume ?: 0.0,
            estimatedCaloriesBurned = calculateCaloriesBurned(volumeData?.totalVolume ?: 0.0),
            strengthGains = calculateWorkoutStrengthGains(workoutId)
        )
    }
    
    // Helper methods
    private fun getFormulaConfidence(reps: Int, formula: OneRMFormula): Double {
        return when (formula) {
            OneRMFormula.BRZYCKI -> when (reps) {
                in 1..1 -> 1.0
                in 2..6 -> 0.9
                in 7..12 -> 0.8
                else -> 0.6
            }
            OneRMFormula.EPLEY -> when (reps) {
                in 1..10 -> 0.85
                else -> 0.7
            }
            else -> 0.75 // Default confidence for other formulas
        }
    }
    
    private fun updateStrengthProgressions(workout: ActiveWorkout) {
        val progressions = _strengthProgression.value.toMutableMap()
        
        workout.exercises.forEach { workoutExercise ->
            val exerciseId = workoutExercise.exercise.id
            val bestSet = workoutExercise.sets.filter { it.isCompleted }
                .maxByOrNull { calculateOneRepMax(it.weight, it.reps) }
            
            if (bestSet != null) {
                val measurement = StrengthMeasurement(
                    date = bestSet.timestamp,
                    weight = bestSet.weight,
                    reps = bestSet.reps,
                    estimated1RM = calculateOneRepMax(bestSet.weight, bestSet.reps),
                    rpe = bestSet.rpe,
                    bodyWeight = null // Would get from user profile
                )
                
                val existingProgression = progressions[exerciseId]
                if (existingProgression != null) {
                    val updatedMeasurements = existingProgression.measurements + measurement
                    progressions[exerciseId] = existingProgression.copy(
                        measurements = updatedMeasurements,
                        currentEstimated1RM = measurement.estimated1RM,
                        strengthGain = calculateStrengthGain(updatedMeasurements),
                        velocityOfGain = calculateVelocityOfGain(updatedMeasurements)
                    )
                } else {
                    progressions[exerciseId] = StrengthProgression(
                        exerciseId = exerciseId,
                        exerciseName = workoutExercise.exercise.name,
                        measurements = listOf(measurement),
                        currentEstimated1RM = measurement.estimated1RM,
                        strengthGain = 0.0,
                        velocityOfGain = 0.0,
                        plateau = null
                    )
                }
            }
        }
        
        _strengthProgression.value = progressions
    }
    
    private fun updateVolumeProgression(workout: ActiveWorkout) {
        val totalVolume = workout.exercises.sumOf { exercise ->
            exercise.sets.filter { it.isCompleted }.sumOf { set ->
                set.weight * set.reps
            }
        }
        
        val exerciseVolume = workout.exercises.associate { exercise ->
            exercise.exercise.id to exercise.sets.filter { it.isCompleted }.sumOf { set ->
                set.weight * set.reps
            }
        }
        
        val volumeProgression = VolumeProgression(
            date = workout.endTime ?: Date(),
            totalVolume = totalVolume,
            workoutVolume = totalVolume,
            exerciseVolume = exerciseVolume,
            intensityLoad = calculateIntensityLoad(workout),
            tonnage = totalVolume
        )
        
        val currentProgression = _volumeProgression.value.toMutableList()
        currentProgression.add(volumeProgression)
        _volumeProgression.value = currentProgression
    }
    
    private fun updateTrainingLoad(workout: ActiveWorkout) {
        // Implementation for training load calculation
        // This would track acute vs chronic workload ratios
    }
    
    private fun calculateProgressionRate(exerciseId: String): Double {
        val progression = _strengthProgression.value[exerciseId] ?: return 0.0
        return progression.velocityOfGain
    }
    
    private fun getStrengthStandard(exerciseId: String, multiplier: Double): StrengthStandard {
        // This would use actual strength standards based on exercise type
        return when {
            multiplier < 0.5 -> StrengthStandard.UNTRAINED
            multiplier < 1.0 -> StrengthStandard.NOVICE
            multiplier < 1.5 -> StrengthStandard.INTERMEDIATE
            multiplier < 2.0 -> StrengthStandard.ADVANCED
            else -> StrengthStandard.ELITE
        }
    }
    
    private fun calculateTrend(values: List<Double>): Double {
        // Simple linear regression to detect trend
        if (values.size < 2) return 0.0
        
        val n = values.size
        val x = (0 until n).map { it.toDouble() }
        val y = values
        
        val sumX = x.sum()
        val sumY = y.sum()
        val sumXY = x.zip(y) { xi, yi -> xi * yi }.sum()
        val sumXX = x.map { it * it }.sum()
        
        val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        return slope * 100 // Convert to percentage
    }
    
    private fun generatePlateauSuggestion(progression: StrengthProgression): String {
        return when {
            progression.measurements.last().rpe?.let { it >= 9 } == true -> 
                "Consider deload week - RPE consistently high"
            progression.measurements.size > 10 -> 
                "Try different rep ranges or exercise variations"
            else -> 
                "Consider increasing training frequency or volume"
        }
    }
    
    private fun calculateStrengthGain(measurements: List<StrengthMeasurement>): Double {
        if (measurements.size < 2) return 0.0
        
        val first = measurements.first().estimated1RM
        val last = measurements.last().estimated1RM
        
        return ((last - first) / first) * 100
    }
    
    private fun calculateVelocityOfGain(measurements: List<StrengthMeasurement>): Double {
        if (measurements.size < 2) return 0.0
        
        val daysBetween = (measurements.last().date.time - measurements.first().date.time) / (1000 * 60 * 60 * 24)
        val totalGain = calculateStrengthGain(measurements)
        
        return totalGain / (daysBetween / 7.0) // Gain per week
    }
    
    private fun calculateIntensityLoad(workout: ActiveWorkout): Double {
        // Average RPE or % of 1RM across all sets
        val completedSets = workout.exercises.flatMap { it.sets }.filter { it.isCompleted }
        val rpeValues = completedSets.mapNotNull { it.rpe }
        
        return if (rpeValues.isNotEmpty()) {
            rpeValues.average()
        } else {
            // Estimate based on rep ranges if no RPE data
            7.5 // Default moderate intensity
        }
    }
    
    private fun calculateCaloriesBurned(volume: Double): Double {
        // Rough estimate: ~0.05 calories per kg lifted
        return volume * 0.05
    }
    
    private fun calculateWorkoutStrengthGains(workoutId: String): Map<String, Double> {
        // Calculate strength gains per exercise for this workout
        return emptyMap() // Simplified for now
    }
}

data class WorkoutSummary(
    val workoutId: String,
    val personalRecords: List<PersonalRecord>,
    val totalVolume: Double,
    val estimatedCaloriesBurned: Double,
    val strengthGains: Map<String, Double>
)