package com.fitnessapp.testing

import com.fitnessapp.core.database.entities.*
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Test data factory for creating consistent test objects
 */
object TestData {
    
    // User Test Data
    fun createTestUser(
        id: Long = 1L,
        name: String = "Test User",
        email: String = "test@example.com",
        age: Int = 30,
        gender: String = "male",
        heightCm: Float = 175f,
        activityLevel: String = "moderate"
    ) = UserEntity(
        id = id,
        name = name,
        email = email,
        age = age,
        gender = gender,
        heightCm = heightCm,
        activityLevel = activityLevel,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    fun createTestUserGoals(
        userId: Long = 1L,
        weightGoalKg: Float = 70f,
        dailyCalorieGoal: Int = 2000,
        proteinGoalG: Float = 150f,
        workoutGoalPerWeek: Int = 4
    ) = UserGoalsEntity(
        userId = userId,
        weightGoalKg = weightGoalKg,
        dailyCalorieGoal = dailyCalorieGoal,
        proteinGoalG = proteinGoalG,
        carbGoalG = 250f,
        fatGoalG = 75f,
        workoutGoalPerWeek = workoutGoalPerWeek,
        updatedAt = LocalDateTime.now()
    )
    
    // Weight Test Data
    fun createTestWeight(
        id: Long = 1L,
        userId: Long = 1L,
        weightKg: Float = 75f,
        recordedAt: LocalDateTime = LocalDateTime.now()
    ) = WeightEntity(
        id = id,
        userId = userId,
        weightKg = weightKg,
        recordedAt = recordedAt,
        source = "manual",
        notes = "Test weight entry"
    )
    
    // Exercise Test Data
    fun createTestExercise(
        id: Long = 1L,
        name: String = "Bench Press",
        category: String = "chest",
        muscleGroups: List<String> = listOf("chest", "triceps", "shoulders")
    ) = ExerciseEntity(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        description = "Test exercise description",
        instructions = "Test exercise instructions",
        equipment = "barbell",
        difficulty = "intermediate",
        imageUrl = "https://example.com/exercise.jpg"
    )
    
    // Workout Test Data
    fun createTestWorkout(
        id: Long = 1L,
        userId: Long = 1L,
        name: String = "Push Day",
        startTime: LocalDateTime = LocalDateTime.now().minusHours(1)
    ) = WorkoutEntity(
        id = id,
        userId = userId,
        name = name,
        startTime = startTime,
        endTime = LocalDateTime.now(),
        notes = "Test workout",
        totalVolume = 5000f,
        averageHeartRate = 140
    )
    
    fun createTestWorkoutExercise(
        id: Long = 1L,
        workoutId: Long = 1L,
        exerciseId: Long = 1L,
        orderIndex: Int = 0
    ) = WorkoutExerciseEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        orderIndex = orderIndex,
        notes = "Test workout exercise"
    )
    
    fun createTestSet(
        id: Long = 1L,
        workoutExerciseId: Long = 1L,
        setNumber: Int = 1,
        reps: Int = 10,
        weightKg: Float = 80f
    ) = SetEntity(
        id = id,
        workoutExerciseId = workoutExerciseId,
        setNumber = setNumber,
        reps = reps,
        weightKg = weightKg,
        isCompleted = true,
        restTimeSeconds = 120,
        rpe = 8
    )
    
    // Routine Test Data
    fun createTestRoutine(
        id: Long = 1L,
        userId: Long = 1L,
        name: String = "Push Pull Legs",
        description: String = "Test routine"
    ) = RoutineEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        isActive = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    // Food Test Data
    fun createTestFood(
        id: Long = 1L,
        name: String = "Chicken Breast",
        brand: String = "Generic",
        caloriesPerServing: Float = 165f
    ) = FoodEntity(
        id = id,
        name = name,
        brand = brand,
        caloriesPerServing = caloriesPerServing,
        proteinPerServing = 31f,
        carbsPerServing = 0f,
        fatPerServing = 3.6f,
        servingSize = "100g",
        servingUnit = "grams",
        barcode = "1234567890123",
        category = "meat"
    )
    
    // Meal Test Data
    fun createTestMeal(
        id: Long = 1L,
        userId: Long = 1L,
        name: String = "Lunch",
        mealTime: LocalDateTime = LocalDateTime.now()
    ) = MealEntity(
        id = id,
        userId = userId,
        name = name,
        mealTime = mealTime,
        totalCalories = 450f,
        totalProtein = 35f,
        totalCarbs = 20f,
        totalFat = 25f
    )
    
    // Health Metric Test Data
    fun createTestHealthMetric(
        id: Long = 1L,
        userId: Long = 1L,
        type: String = "heart_rate",
        value: Float = 72f
    ) = HealthMetricEntity(
        id = id,
        userId = userId,
        type = type,
        value = value,
        unit = "bpm",
        recordedAt = LocalDateTime.now(),
        source = "health_connect"
    )
    
    // Body Measurement Test Data
    fun createTestBodyMeasurement(
        id: Long = 1L,
        userId: Long = 1L,
        type: String = "body_fat",
        value: Float = 15f
    ) = BodyMeasurementEntity(
        id = id,
        userId = userId,
        type = type,
        value = value,
        unit = "percentage",
        recordedAt = LocalDateTime.now(),
        notes = "Test measurement"
    )
    
    // Progress Photo Test Data
    fun createTestProgressPhoto(
        id: Long = 1L,
        userId: Long = 1L,
        imageUrl: String = "/storage/progress/test.jpg"
    ) = ProgressPhotoEntity(
        id = id,
        userId = userId,
        imageUrl = imageUrl,
        takenAt = LocalDateTime.now(),
        type = "front",
        notes = "Test progress photo"
    )
    
    // Quick Add Test Data
    fun createTestQuickAdd(
        id: Long = 1L,
        userId: Long = 1L,
        name: String = "Protein Shake",
        calories: Float = 200f
    ) = QuickAddEntity(
        id = id,
        userId = userId,
        name = name,
        calories = calories,
        protein = 25f,
        carbs = 10f,
        fat = 5f,
        isActive = true,
        createdAt = LocalDateTime.now()
    )
    
    // Recipe Test Data
    fun createTestRecipe(
        id: Long = 1L,
        userId: Long = 1L,
        name: String = "Protein Pancakes",
        servings: Int = 2
    ) = RecipeEntity(
        id = id,
        userId = userId,
        name = name,
        description = "Healthy protein pancakes",
        instructions = "Mix ingredients and cook",
        servings = servings,
        prepTimeMinutes = 10,
        cookTimeMinutes = 15,
        totalCalories = 400f,
        totalProtein = 40f,
        totalCarbs = 30f,
        totalFat = 10f,
        createdAt = LocalDateTime.now()
    )
    
    // List extensions for creating multiple test objects
    fun createTestWeights(count: Int, userId: Long = 1L): List<WeightEntity> {
        return (1..count).map { index ->
            createTestWeight(
                id = index.toLong(),
                userId = userId,
                weightKg = 75f + (index * 0.5f),
                recordedAt = LocalDateTime.now().minusDays(index.toLong())
            )
        }
    }
    
    fun createTestWorkouts(count: Int, userId: Long = 1L): List<WorkoutEntity> {
        return (1..count).map { index ->
            createTestWorkout(
                id = index.toLong(),
                userId = userId,
                name = "Workout $index",
                startTime = LocalDateTime.now().minusDays(index.toLong())
            )
        }
    }
    
    fun createTestMeals(count: Int, userId: Long = 1L): List<MealEntity> {
        return (1..count).map { index ->
            createTestMeal(
                id = index.toLong(),
                userId = userId,
                name = "Meal $index",
                mealTime = LocalDateTime.now().minusHours(index.toLong())
            )
        }
    }
}