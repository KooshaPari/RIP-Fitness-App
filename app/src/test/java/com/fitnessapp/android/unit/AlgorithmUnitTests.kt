package com.fitnessapp.android.unit

import com.fitnessapp.feature.nutrition.AdaptiveAlgorithmEngine
import com.fitnessapp.feature.workout.ProgressAnalytics
import com.fitnessapp.testing.TestData
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlgorithmUnitTests {
    
    private lateinit var nutritionEngine: AdaptiveAlgorithmEngine
    private lateinit var progressAnalytics: ProgressAnalytics
    
    @Before
    fun setup() {
        nutritionEngine = AdaptiveAlgorithmEngine()
        progressAnalytics = ProgressAnalytics()
    }
    
    @Test
    fun `calculate BMR for male user`() {
        // Given
        val user = TestData.createTestUser(
            age = 30,
            gender = "male",
            heightCm = 175f
        )
        val currentWeight = 75f
        
        // When
        val bmr = nutritionEngine.calculateBMR(
            age = user.age,
            gender = user.gender,
            heightCm = user.heightCm,
            weightKg = currentWeight
        )
        
        // Then - Mifflin-St Jeor equation for males
        val expectedBMR = (10 * currentWeight) + (6.25 * user.heightCm) - (5 * user.age) + 5
        assertThat(bmr).isWithin(1f).of(expectedBMR)
    }
    
    @Test
    fun `calculate BMR for female user`() {
        // Given
        val user = TestData.createTestUser(
            age = 25,
            gender = "female",
            heightCm = 165f
        )
        val currentWeight = 60f
        
        // When
        val bmr = nutritionEngine.calculateBMR(
            age = user.age,
            gender = user.gender,
            heightCm = user.heightCm,
            weightKg = currentWeight
        )
        
        // Then - Mifflin-St Jeor equation for females
        val expectedBMR = (10 * currentWeight) + (6.25 * user.heightCm) - (5 * user.age) - 161
        assertThat(bmr).isWithin(1f).of(expectedBMR)
    }
    
    @Test
    fun `calculate TDEE with activity level`() {
        // Given
        val bmr = 1500f
        val activityLevel = "moderate" // 1.55 multiplier
        
        // When
        val tdee = nutritionEngine.calculateTDEE(bmr, activityLevel)
        
        // Then
        val expectedTDEE = bmr * 1.55f
        assertThat(tdee).isWithin(1f).of(expectedTDEE)
    }
    
    @Test
    fun `calculate calorie deficit for weight loss`() {
        // Given
        val tdee = 2000f
        val weightLossGoalKgPerWeek = 0.5f // 0.5kg per week
        
        // When
        val dailyCalorieGoal = nutritionEngine.calculateCalorieGoal(
            tdee = tdee,
            weightGoalKgPerWeek = -weightLossGoalKgPerWeek // negative for loss
        )
        
        // Then - 1kg fat = 7700 calories, so 0.5kg = 3850 calories per week = 550 per day
        val expectedGoal = tdee - 550f
        assertThat(dailyCalorieGoal).isWithin(10f).of(expectedGoal)
    }
    
    @Test
    fun `calculate macro distribution`() {
        // Given
        val dailyCalories = 2000f
        val proteinRatio = 0.3f // 30%
        val fatRatio = 0.25f // 25%
        val carbRatio = 0.45f // 45%
        
        // When
        val macros = nutritionEngine.calculateMacroDistribution(
            dailyCalories = dailyCalories,
            proteinRatio = proteinRatio,
            fatRatio = fatRatio,
            carbRatio = carbRatio
        )
        
        // Then
        assertThat(macros.proteinGrams).isWithin(1f).of(150f) // 600 cal / 4 cal/g
        assertThat(macros.fatGrams).isWithin(1f).of(55.6f) // 500 cal / 9 cal/g
        assertThat(macros.carbGrams).isWithin(1f).of(225f) // 900 cal / 4 cal/g
    }
    
    @Test
    fun `calculate one rep max using Epley formula`() {
        // Given
        val weight = 100f // kg
        val reps = 5
        
        // When
        val oneRepMax = progressAnalytics.calculateOneRepMax(weight, reps)
        
        // Then - Epley formula: 1RM = weight * (1 + reps/30)
        val expectedORM = weight * (1 + reps / 30f)
        assertThat(oneRepMax).isWithin(0.1f).of(expectedORM)
    }
    
    @Test
    fun `calculate training volume`() {
        // Given
        val sets = listOf(
            TestData.createTestSet(weightKg = 80f, reps = 10),
            TestData.createTestSet(weightKg = 85f, reps = 8),
            TestData.createTestSet(weightKg = 90f, reps = 6)
        )
        
        // When
        val totalVolume = progressAnalytics.calculateTrainingVolume(sets)
        
        // Then
        val expectedVolume = (80f * 10) + (85f * 8) + (90f * 6)
        assertThat(totalVolume).isWithin(0.1f).of(expectedVolume)
    }
    
    @Test
    fun `calculate progressive overload recommendation`() {
        // Given
        val currentWeight = 80f
        val currentReps = 10
        val targetReps = 10
        val overloadPercentage = 2.5f // 2.5% increase
        
        // When
        val recommendation = progressAnalytics.calculateProgressiveOverload(
            currentWeight = currentWeight,
            currentReps = currentReps,
            targetReps = targetReps,
            overloadPercentage = overloadPercentage
        )
        
        // Then
        val expectedWeight = currentWeight * (1 + overloadPercentage / 100)
        assertThat(recommendation.recommendedWeight).isWithin(0.1f).of(expectedWeight)
    }
    
    @Test
    fun `calculate workout intensity`() {
        // Given
        val sets = listOf(
            TestData.createTestSet(weightKg = 80f, reps = 10),
            TestData.createTestSet(weightKg = 85f, reps = 8),
            TestData.createTestSet(weightKg = 90f, reps = 6)
        )
        val oneRepMax = 100f
        
        // When
        val intensity = progressAnalytics.calculateWorkoutIntensity(sets, oneRepMax)
        
        // Then
        // Average intensity should be (80+85+90)/3 / 100 = 85%
        assertThat(intensity).isWithin(1f).of(85f)
    }
    
    @Test
    fun `adaptive nutrition adjustment based on progress`() {
        // Given
        val weights = TestData.createTestWeights(7) // One week of weights
        val currentCalories = 2000f
        val weightGoalKgPerWeek = -0.5f // Weight loss goal
        
        // When
        val adjustment = nutritionEngine.calculateAdaptiveAdjustment(
            recentWeights = weights,
            currentCalories = currentCalories,
            goalKgPerWeek = weightGoalKgPerWeek
        )
        
        // Then
        // Should provide calorie adjustment based on actual vs expected progress
        assertThat(adjustment.calorieAdjustment).isNotEqualTo(0f)
        assertThat(adjustment.reason).isNotEmpty()
    }
    
    @Test
    fun `calculate estimated time to goal`() {
        // Given
        val currentWeight = 80f
        val goalWeight = 75f
        val avgWeightLossPerWeek = 0.3f
        
        // When
        val weeksToGoal = progressAnalytics.calculateTimeToGoal(
            currentValue = currentWeight,
            goalValue = goalWeight,
            avgProgressPerWeek = avgWeightLossPerWeek
        )
        
        // Then
        val expectedWeeks = (currentWeight - goalWeight) / avgWeightLossPerWeek
        assertThat(weeksToGoal).isWithin(0.1f).of(expectedWeeks)
    }
    
    @Test
    fun `test edge cases and boundary conditions`() {
        // Test zero and negative values
        assertThat(nutritionEngine.calculateBMR(0, "male", 175f, 75f)).isEqualTo(0f)
        assertThat(progressAnalytics.calculateOneRepMax(0f, 10)).isEqualTo(0f)
        assertThat(progressAnalytics.calculateTrainingVolume(emptyList())).isEqualTo(0f)
        
        // Test extremely high values
        val highBMR = nutritionEngine.calculateBMR(100, "male", 250f, 200f)
        assertThat(highBMR).isGreaterThan(3000f)
        
        // Test single rep max
        val singleRepMax = progressAnalytics.calculateOneRepMax(100f, 1)
        assertThat(singleRepMax).isEqualTo(100f)
    }
}