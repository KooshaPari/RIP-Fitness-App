package com.fitnessapp.testing

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fitnessapp.core.database.FitnessDatabase
import kotlinx.coroutines.runBlocking

/**
 * Test database helper for creating in-memory databases for testing
 */
object TestDatabase {
    
    fun createInMemoryDatabase(): FitnessDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitnessDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()
    }
    
    fun populateWithTestData(database: FitnessDatabase) = runBlocking {
        // Create test user
        val user = TestData.createTestUser()
        val userId = database.userDao().insertUser(user)
        
        // Create user goals
        val goals = TestData.createTestUserGoals(userId = userId)
        database.userDao().insertUserGoals(goals)
        
        // Create test exercises
        val exercises = listOf(
            TestData.createTestExercise(1L, "Bench Press", "chest"),
            TestData.createTestExercise(2L, "Squat", "legs"),
            TestData.createTestExercise(3L, "Deadlift", "back"),
            TestData.createTestExercise(4L, "Pull Up", "back"),
            TestData.createTestExercise(5L, "Push Up", "chest")
        )
        exercises.forEach { exercise ->
            database.exerciseDao().insertExercise(exercise)
        }
        
        // Create test weights
        val weights = TestData.createTestWeights(7, userId)
        weights.forEach { weight ->
            database.weightDao().insertWeight(weight)
        }
        
        // Create test workouts
        val workouts = TestData.createTestWorkouts(5, userId)
        workouts.forEach { workout ->
            val workoutId = database.workoutDao().insertWorkout(workout)
            
            // Add exercises to workout
            val workoutExercise = TestData.createTestWorkoutExercise(
                workoutId = workoutId,
                exerciseId = exercises.random().id
            )
            val workoutExerciseId = database.workoutDao().insertWorkoutExercise(workoutExercise)
            
            // Add sets
            repeat(3) { setIndex ->
                val set = TestData.createTestSet(
                    workoutExerciseId = workoutExerciseId,
                    setNumber = setIndex + 1
                )
                database.workoutDao().insertSet(set)
            }
        }
        
        // Create test foods
        val foods = listOf(
            TestData.createTestFood(1L, "Chicken Breast", "Generic", 165f),
            TestData.createTestFood(2L, "Brown Rice", "Generic", 111f),
            TestData.createTestFood(3L, "Broccoli", "Fresh", 25f),
            TestData.createTestFood(4L, "Banana", "Fresh", 89f),
            TestData.createTestFood(5L, "Almonds", "Generic", 576f)
        )
        foods.forEach { food ->
            database.nutritionDao().insertFood(food)
        }
        
        // Create test meals
        val meals = TestData.createTestMeals(3, userId)
        meals.forEach { meal ->
            database.nutritionDao().insertMeal(meal)
        }
        
        // Create test health metrics
        val healthMetrics = listOf(
            TestData.createTestHealthMetric(1L, userId, "heart_rate", 72f),
            TestData.createTestHealthMetric(2L, userId, "steps", 8500f),
            TestData.createTestHealthMetric(3L, userId, "sleep_hours", 7.5f),
            TestData.createTestHealthMetric(4L, userId, "calories_burned", 2100f)
        )
        healthMetrics.forEach { metric ->
            database.healthDao().insertHealthMetric(metric)
        }
        
        // Create test body measurements
        val bodyMeasurements = listOf(
            TestData.createTestBodyMeasurement(1L, userId, "body_fat", 15f),
            TestData.createTestBodyMeasurement(2L, userId, "muscle_mass", 65f),
            TestData.createTestBodyMeasurement(3L, userId, "waist", 85f),
            TestData.createTestBodyMeasurement(4L, userId, "chest", 105f)
        )
        bodyMeasurements.forEach { measurement ->
            database.healthDao().insertBodyMeasurement(measurement)
        }
    }
    
    suspend fun clearDatabase(database: FitnessDatabase) {
        database.clearAllTables()
    }
}