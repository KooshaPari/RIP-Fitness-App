package com.fitnessapp.feature.nutrition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import kotlin.math.*

/**
 * MacroFactor-inspired Adaptive Algorithm Engine
 * 
 * This engine dynamically calculates and adjusts metabolic rate based on:
 * - Body weight trends over time
 * - Food intake patterns 
 * - Activity levels
 * - Adaptive thermogenesis effects
 * 
 * Key Features:
 * - Dynamic TDEE calculation using exponential weighted moving averages
 * - Metabolic adaptation detection and adjustment
 * - Weekly coaching recommendations
 * - Plateau detection and intervention strategies
 */
class AdaptiveAlgorithmEngine {
    
    private val _metabolicData = MutableStateFlow(MetabolicData())
    val metabolicData: StateFlow<MetabolicData> = _metabolicData.asStateFlow()
    
    private val _weeklyCoaching = MutableStateFlow<WeeklyCoaching?>(null)
    val weeklyCoaching: StateFlow<WeeklyCoaching?> = _weeklyCoaching.asStateFlow()
    
    // Algorithm parameters inspired by MacroFactor's approach
    private val weightSmoothingFactor = 0.1 // For exponential smoothing
    private val intakeSmoothingFactor = 0.15
    private val minimumDataPoints = 14 // 2 weeks minimum
    private val adaptationThreshold = 0.85 // 85% of predicted TDEE triggers adaptation
    
    data class MetabolicData(
        val currentTdee: Double = 0.0,
        val baseTdee: Double = 0.0,
        val adaptationFactor: Double = 1.0,
        val confidence: Double = 0.0,
        val weeklyWeightChange: Double = 0.0,
        val weeklyIntakeAverage: Double = 0.0,
        val metabolicFlexibility: Double = 1.0,
        val lastCalculated: LocalDate = LocalDate.now()
    )
    
    data class WeeklyCoaching(
        val recommendation: CoachingRecommendation,
        val targetAdjustment: Double,
        val reasoning: String,
        val confidence: Double,
        val interventionRequired: Boolean = false
    )
    
    enum class CoachingRecommendation {
        MAINTAIN_CURRENT,
        INCREASE_CALORIES,
        DECREASE_CALORIES,
        REFEED_RECOMMENDED,
        DIET_BREAK_RECOMMENDED,
        INCREASE_ACTIVITY,
        METABOLIC_RESET
    }
    
    data class WeightDataPoint(
        val date: LocalDate,
        val weight: Double,
        val smoothedWeight: Double = weight
    )
    
    data class IntakeDataPoint(
        val date: LocalDate,
        val calories: Double,
        val macros: MacroBreakdown
    )
    
    data class MacroBreakdown(
        val protein: Double,
        val carbs: Double,
        val fat: Double,
        val fiber: Double
    )
    
    /**
     * Core adaptive TDEE calculation using exponential weighted moving averages
     * Inspired by MacroFactor's proprietary algorithm
     */
    suspend fun calculateAdaptiveTdee(
        weightHistory: List<WeightDataPoint>,
        intakeHistory: List<IntakeDataPoint>,
        userProfile: UserProfile
    ): MetabolicData {
        
        if (weightHistory.size < minimumDataPoints || intakeHistory.size < minimumDataPoints) {
            return _metabolicData.value.copy(
                confidence = 0.0,
                lastCalculated = LocalDate.now()
            )
        }
        
        // Step 1: Calculate smoothed weight trend
        val smoothedWeights = calculateExponentialSmoothing(
            weightHistory.map { it.weight },
            weightSmoothingFactor
        )
        
        // Step 2: Calculate average intake over recent period
        val recentIntake = intakeHistory.takeLast(14)
        val avgDailyIntake = recentIntake.map { it.calories }.average()
        
        // Step 3: Weight change analysis
        val weeklyWeightChange = calculateWeeklyWeightChange(smoothedWeights)
        
        // Step 4: Energy balance calculation
        val energyBalance = calculateEnergyBalance(weeklyWeightChange)
        
        // Step 5: Adaptive TDEE calculation
        val adaptiveTdee = avgDailyIntake - energyBalance
        
        // Step 6: Calculate base TDEE for comparison
        val baseTdee = calculateBaseTdee(userProfile)
        
        // Step 7: Metabolic adaptation factor
        val adaptationFactor = adaptiveTdee / baseTdee
        
        // Step 8: Confidence calculation based on data consistency
        val confidence = calculateConfidence(
            weightHistory,
            intakeHistory,
            smoothedWeights
        )
        
        // Step 9: Metabolic flexibility assessment
        val metabolicFlexibility = assessMetabolicFlexibility(
            intakeHistory,
            weightHistory
        )
        
        val newMetabolicData = MetabolicData(
            currentTdee = adaptiveTdee,
            baseTdee = baseTdee,
            adaptationFactor = adaptationFactor,
            confidence = confidence,
            weeklyWeightChange = weeklyWeightChange,
            weeklyIntakeAverage = avgDailyIntake,
            metabolicFlexibility = metabolicFlexibility,
            lastCalculated = LocalDate.now()
        )
        
        _metabolicData.value = newMetabolicData
        
        // Generate weekly coaching recommendation
        generateWeeklyCoaching(newMetabolicData, userProfile)
        
        return newMetabolicData
    }
    
    /**
     * Exponential weighted moving average for noise reduction
     */
    private fun calculateExponentialSmoothing(
        values: List<Double>,
        alpha: Double
    ): List<Double> {
        if (values.isEmpty()) return emptyList()
        
        val smoothed = mutableListOf<Double>()
        smoothed.add(values.first())
        
        for (i in 1 until values.size) {
            val smoothedValue = alpha * values[i] + (1 - alpha) * smoothed[i - 1]
            smoothed.add(smoothedValue)
        }
        
        return smoothed
    }
    
    /**
     * Calculate weekly weight change rate
     */
    private fun calculateWeeklyWeightChange(smoothedWeights: List<Double>): Double {
        if (smoothedWeights.size < 7) return 0.0
        
        val recent = smoothedWeights.takeLast(7).average()
        val previous = smoothedWeights.dropLast(7).takeLast(7).average()
        
        return recent - previous
    }
    
    /**
     * Convert weight change to energy balance (calories)
     * 1 lb fat = ~3500 calories, 1 kg = ~7700 calories
     */
    private fun calculateEnergyBalance(weeklyWeightChange: Double): Double {
        // Assuming weight change in kg, convert to daily caloric equivalent
        return (weeklyWeightChange * 7700) / 7
    }
    
    /**
     * Calculate base TDEE using multiple equations and average
     */
    private fun calculateBaseTdee(userProfile: UserProfile): Double {
        val bmr = when (userProfile.sex) {
            UserProfile.Sex.MALE -> {
                // Mifflin-St Jeor for males
                88.362 + (13.397 * userProfile.weight) + 
                (4.799 * userProfile.height) - (5.677 * userProfile.age)
            }
            UserProfile.Sex.FEMALE -> {
                // Mifflin-St Jeor for females  
                447.593 + (9.247 * userProfile.weight) + 
                (3.098 * userProfile.height) - (4.330 * userProfile.age)
            }
        }
        
        // Apply activity factor
        return bmr * userProfile.activityFactor
    }
    
    /**
     * Calculate algorithm confidence based on data consistency
     */
    private fun calculateConfidence(
        weightHistory: List<WeightDataPoint>,
        intakeHistory: List<IntakeDataPoint>,
        smoothedWeights: List<Double>
    ): Double {
        
        // Factor 1: Data completeness (0-0.4)
        val completeness = min(
            (weightHistory.size / 28.0),
            (intakeHistory.size / 28.0)
        ) * 0.4
        
        // Factor 2: Weight data consistency (0-0.3)
        val weightVariability = if (weightHistory.size > 7) {
            val recentWeights = weightHistory.takeLast(7).map { it.weight }
            val stdDev = calculateStandardDeviation(recentWeights)
            max(0.0, 0.3 - (stdDev / 2.0)) // Lower variability = higher confidence
        } else 0.0
        
        // Factor 3: Intake consistency (0-0.3)
        val intakeConsistency = if (intakeHistory.size > 7) {
            val recentIntakes = intakeHistory.takeLast(7).map { it.calories }
            val stdDev = calculateStandardDeviation(recentIntakes)
            max(0.0, 0.3 - (stdDev / 500.0)) // Lower variability = higher confidence
        } else 0.0
        
        return min(1.0, completeness + weightVariability + intakeConsistency)
    }
    
    /**
     * Assess metabolic flexibility based on response to intake variations
     */
    private fun assessMetabolicFlexibility(
        intakeHistory: List<IntakeDataPoint>,
        weightHistory: List<WeightDataPoint>
    ): Double {
        if (intakeHistory.size < 21 || weightHistory.size < 21) return 1.0
        
        // Analyze correlation between intake changes and weight response
        val intakeChanges = intakeHistory.zipWithNext { current, next ->
            next.calories - current.calories
        }
        
        val weightChanges = weightHistory.zipWithNext { current, next ->
            next.weight - current.weight
        }
        
        // Higher flexibility = appropriate weight response to intake changes
        val correlation = calculateCorrelation(intakeChanges, weightChanges)
        
        return max(0.5, min(1.5, 1.0 + (correlation * 0.5)))
    }
    
    /**
     * Generate weekly coaching recommendations based on metabolic data
     */
    private suspend fun generateWeeklyCoaching(
        metabolicData: MetabolicData,
        userProfile: UserProfile
    ) {
        val recommendation = when {
            // Metabolic adaptation detected
            metabolicData.adaptationFactor < adaptationThreshold -> {
                if (userProfile.goal == UserProfile.Goal.FAT_LOSS) {
                    if (metabolicData.adaptationFactor < 0.75) {
                        CoachingRecommendation.DIET_BREAK_RECOMMENDED
                    } else {
                        CoachingRecommendation.REFEED_RECOMMENDED
                    }
                } else {
                    CoachingRecommendation.INCREASE_CALORIES
                }
            }
            
            // Plateau detection
            abs(metabolicData.weeklyWeightChange) < 0.1 && 
            userProfile.goal != UserProfile.Goal.MAINTENANCE -> {
                when (userProfile.goal) {
                    UserProfile.Goal.FAT_LOSS -> CoachingRecommendation.DECREASE_CALORIES
                    UserProfile.Goal.MUSCLE_GAIN -> CoachingRecommendation.INCREASE_CALORIES
                    else -> CoachingRecommendation.MAINTAIN_CURRENT
                }
            }
            
            // Rapid weight loss (>1% body weight per week)
            metabolicData.weeklyWeightChange < -userProfile.weight * 0.01 -> {
                CoachingRecommendation.INCREASE_CALORIES
            }
            
            // Rapid weight gain when not bulking
            metabolicData.weeklyWeightChange > userProfile.weight * 0.005 &&
            userProfile.goal != UserProfile.Goal.MUSCLE_GAIN -> {
                CoachingRecommendation.DECREASE_CALORIES
            }
            
            else -> CoachingRecommendation.MAINTAIN_CURRENT
        }
        
        val targetAdjustment = calculateTargetAdjustment(recommendation, metabolicData)
        val reasoning = generateReasoning(recommendation, metabolicData)
        val interventionRequired = recommendation in setOf(
            CoachingRecommendation.DIET_BREAK_RECOMMENDED,
            CoachingRecommendation.METABOLIC_RESET,
            CoachingRecommendation.REFEED_RECOMMENDED
        )
        
        _weeklyCoaching.value = WeeklyCoaching(
            recommendation = recommendation,
            targetAdjustment = targetAdjustment,
            reasoning = reasoning,
            confidence = metabolicData.confidence,
            interventionRequired = interventionRequired
        )
    }
    
    private fun calculateTargetAdjustment(
        recommendation: CoachingRecommendation,
        metabolicData: MetabolicData
    ): Double {
        return when (recommendation) {
            CoachingRecommendation.INCREASE_CALORIES -> 100.0 + (metabolicData.currentTdee * 0.05)
            CoachingRecommendation.DECREASE_CALORIES -> -(100.0 + (metabolicData.currentTdee * 0.05))
            CoachingRecommendation.REFEED_RECOMMENDED -> metabolicData.currentTdee * 0.15
            CoachingRecommendation.DIET_BREAK_RECOMMENDED -> metabolicData.baseTdee - metabolicData.weeklyIntakeAverage
            CoachingRecommendation.METABOLIC_RESET -> metabolicData.baseTdee * 1.1 - metabolicData.weeklyIntakeAverage
            else -> 0.0
        }
    }
    
    private fun generateReasoning(
        recommendation: CoachingRecommendation,
        metabolicData: MetabolicData
    ): String {
        return when (recommendation) {
            CoachingRecommendation.INCREASE_CALORIES -> 
                "Metabolic adaptation detected (${(metabolicData.adaptationFactor * 100).toInt()}% of baseline). Increasing calories to restore metabolic rate."
            
            CoachingRecommendation.DECREASE_CALORIES -> 
                "Weight loss has plateaued. Small caloric reduction recommended to resume progress."
            
            CoachingRecommendation.REFEED_RECOMMENDED -> 
                "Significant metabolic adaptation detected. Strategic refeed recommended to boost leptin and metabolic rate."
            
            CoachingRecommendation.DIET_BREAK_RECOMMENDED -> 
                "Severe metabolic adaptation detected. Full diet break recommended to restore hormonal balance."
            
            CoachingRecommendation.MAINTAIN_CURRENT -> 
                "Current approach is working well. Continue with present caloric intake and monitor progress."
            
            else -> "Algorithm recommendation based on current metabolic state and progress trends."
        }
    }
    
    // Utility functions
    private fun calculateStandardDeviation(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        return sqrt(variance)
    }
    
    private fun calculateCorrelation(x: List<Double>, y: List<Double>): Double {
        if (x.size != y.size || x.size < 2) return 0.0
        
        val meanX = x.average()
        val meanY = y.average()
        
        val numerator = x.zip(y).map { (xi, yi) -> (xi - meanX) * (yi - meanY) }.sum()
        val denomX = sqrt(x.map { (it - meanX).pow(2) }.sum())
        val denomY = sqrt(y.map { (it - meanY).pow(2) }.sum())
        
        return if (denomX * denomY != 0.0) numerator / (denomX * denomY) else 0.0
    }
    
    data class UserProfile(
        val age: Int,
        val sex: Sex,
        val height: Double, // cm
        val weight: Double, // kg
        val activityFactor: Double,
        val goal: Goal
    ) {
        enum class Sex { MALE, FEMALE }
        enum class Goal { FAT_LOSS, MUSCLE_GAIN, MAINTENANCE }
    }
}