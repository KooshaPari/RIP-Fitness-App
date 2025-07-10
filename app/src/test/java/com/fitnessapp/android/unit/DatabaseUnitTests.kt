package com.fitnessapp.android.unit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.fitnessapp.core.database.FitnessDatabase
import com.fitnessapp.core.database.dao.*
import com.fitnessapp.testing.TestData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseUnitTests {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: FitnessDatabase
    private lateinit var userDao: UserDao
    private lateinit var workoutDao: WorkoutDao
    private lateinit var nutritionDao: NutritionDao
    private lateinit var healthDao: HealthDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var weightDao: WeightDao
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitnessDatabase::class.java
        ).allowMainThreadQueries().build()
        
        userDao = database.userDao()
        workoutDao = database.workoutDao()
        nutritionDao = database.nutritionDao()
        healthDao = database.healthDao()
        exerciseDao = database.exerciseDao()
        weightDao = database.weightDao()
    }
    
    @After
    fun teardown() {
        database.close()
    }
    
    @Test
    fun `insert and retrieve user`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        
        // When
        val userId = userDao.insertUser(user)
        val retrievedUser = userDao.getUserById(userId)
        
        // Then
        assertThat(retrievedUser).isNotNull()
        assertThat(retrievedUser?.name).isEqualTo(user.name)
        assertThat(retrievedUser?.email).isEqualTo(user.email)
    }
    
    @Test
    fun `insert and retrieve user goals`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        val goals = TestData.createTestUserGoals(userId = userId)
        
        // When
        userDao.insertUserGoals(goals)
        val retrievedGoals = userDao.getUserGoals(userId)
        
        // Then
        assertThat(retrievedGoals).isNotNull()
        assertThat(retrievedGoals?.weightGoalKg).isEqualTo(goals.weightGoalKg)
        assertThat(retrievedGoals?.dailyCalorieGoal).isEqualTo(goals.dailyCalorieGoal)
    }
    
    @Test
    fun `insert and retrieve weights`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        val weights = TestData.createTestWeights(5, userId)
        
        // When
        weights.forEach { weight ->
            weightDao.insertWeight(weight)
        }
        val retrievedWeights = weightDao.getWeightHistory(userId, 10)
        
        // Then
        assertThat(retrievedWeights).hasSize(5)
        assertThat(retrievedWeights.first().weightKg).isEqualTo(weights.last().weightKg)
    }
    
    @Test
    fun `insert workout with exercises and sets`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        val exercise = TestData.createTestExercise()
        val exerciseId = exerciseDao.insertExercise(exercise)
        val workout = TestData.createTestWorkout(userId = userId)
        
        // When
        val workoutId = workoutDao.insertWorkout(workout)
        val workoutExercise = TestData.createTestWorkoutExercise(
            workoutId = workoutId,
            exerciseId = exerciseId
        )
        val workoutExerciseId = workoutDao.insertWorkoutExercise(workoutExercise)
        
        val sets = listOf(
            TestData.createTestSet(workoutExerciseId = workoutExerciseId, setNumber = 1),
            TestData.createTestSet(workoutExerciseId = workoutExerciseId, setNumber = 2),
            TestData.createTestSet(workoutExerciseId = workoutExerciseId, setNumber = 3)
        )
        sets.forEach { set ->
            workoutDao.insertSet(set)
        }
        
        // Then
        val workoutWithDetails = workoutDao.getWorkoutWithDetails(workoutId)
        assertThat(workoutWithDetails).isNotNull()
        assertThat(workoutWithDetails?.exercises).hasSize(1)
        assertThat(workoutWithDetails?.exercises?.first()?.sets).hasSize(3)
    }
    
    @Test
    fun `insert and retrieve meals with nutrition`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        val food = TestData.createTestFood()
        val foodId = nutritionDao.insertFood(food)
        val meal = TestData.createTestMeal(userId = userId)
        
        // When
        val mealId = nutritionDao.insertMeal(meal)
        val retrievedMeal = nutritionDao.getMealById(mealId)
        
        // Then
        assertThat(retrievedMeal).isNotNull()
        assertThat(retrievedMeal?.name).isEqualTo(meal.name)
        assertThat(retrievedMeal?.totalCalories).isEqualTo(meal.totalCalories)
    }
    
    @Test
    fun `insert and retrieve health metrics`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        val healthMetrics = listOf(
            TestData.createTestHealthMetric(userId = userId, type = "heart_rate", value = 72f),
            TestData.createTestHealthMetric(userId = userId, type = "steps", value = 8500f),
            TestData.createTestHealthMetric(userId = userId, type = "sleep_hours", value = 7.5f)
        )
        
        // When
        healthMetrics.forEach { metric ->
            healthDao.insertHealthMetric(metric)
        }
        val retrievedMetrics = healthDao.getHealthMetricsByType(userId, "heart_rate", 10)
        
        // Then
        assertThat(retrievedMetrics).hasSize(1)
        assertThat(retrievedMetrics.first().value).isEqualTo(72f)
    }
    
    @Test
    fun `test database migration scenario`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        
        // When - Insert data before simulated migration
        val weight = TestData.createTestWeight(userId = userId)
        weightDao.insertWeight(weight)
        
        // Simulate data retrieval after migration
        val retrievedWeight = weightDao.getLatestWeight(userId)
        
        // Then
        assertThat(retrievedWeight).isNotNull()
        assertThat(retrievedWeight?.weightKg).isEqualTo(weight.weightKg)
    }
    
    @Test
    fun `test complex query performance`() = runBlocking {
        // Given
        val user = TestData.createTestUser()
        val userId = userDao.insertUser(user)
        
        // Insert large dataset
        val weights = TestData.createTestWeights(100, userId)
        weights.forEach { weight ->
            weightDao.insertWeight(weight)
        }
        
        // When
        val startTime = System.currentTimeMillis()
        val recentWeights = weightDao.getWeightHistory(userId, 30)
        val endTime = System.currentTimeMillis()
        
        // Then
        assertThat(recentWeights).hasSize(30)
        assertThat(endTime - startTime).isLessThan(100) // Should complete within 100ms
    }
}