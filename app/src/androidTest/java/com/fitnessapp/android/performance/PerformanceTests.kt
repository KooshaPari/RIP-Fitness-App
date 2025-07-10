package com.fitnessapp.android.performance

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fitnessapp.core.database.FitnessDatabase
import com.fitnessapp.feature.nutrition.AdaptiveAlgorithmEngine
import com.fitnessapp.feature.workout.ProgressAnalytics
import com.fitnessapp.testing.KMobileTestSetup
import com.fitnessapp.testing.TestData
import com.fitnessapp.testing.TestDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kmobile.performance.PerformanceMonitor
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PerformanceTests {
    
    @get:Rule
    val benchmarkRule = BenchmarkRule()
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val kmobileRule = KMobileTestSetup()
    
    @Inject
    lateinit var database: FitnessDatabase
    
    @Inject
    lateinit var nutritionEngine: AdaptiveAlgorithmEngine
    
    @Inject
    lateinit var progressAnalytics: ProgressAnalytics
    
    @Inject
    lateinit var performanceMonitor: PerformanceMonitor
    
    @Before
    fun setup() {
        hiltRule.inject()
        performanceMonitor.startSession("performance_tests")
    }
    
    @Test
    fun benchmarkAppStartup() {
        benchmarkRule.measureRepeated {
            // Measure cold start time
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            
            runWithTimingDisabled {
                // Setup before measurement
                performanceMonitor.startTrace("app_startup")
            }
            
            // Simulate app startup sequence
            val startTime = System.nanoTime()
            
            // Initialize core components
            val db = TestDatabase.createInMemoryDatabase()
            val engine = AdaptiveAlgorithmEngine()
            val analytics = ProgressAnalytics()
            
            // Load initial data
            runBlocking {
                TestDatabase.populateWithTestData(db)
            }
            
            val endTime = System.nanoTime()
            val startupTime = (endTime - startTime) / 1_000_000 // Convert to milliseconds
            
            runWithTimingDisabled {
                performanceMonitor.endTrace("app_startup")
                // Startup should be under 2.5 seconds on average
                assert(startupTime < 2500) { "App startup took ${startupTime}ms, should be under 2500ms" }
            }
        }
    }
    
    @Test
    fun benchmarkDatabaseOperations() {
        benchmarkRule.measureRepeated {
            runWithTimingDisabled {
                TestDatabase.populateWithTestData(database)
            }
            
            runBlocking {
                // Benchmark complex workout query
                val workouts = database.workoutDao().getWorkoutsWithDetails(userId = 1L, limit = 50)
                
                // Benchmark nutrition aggregation
                val nutritionSummary = database.nutritionDao().getDailyNutritionSummary(
                    userId = 1L,
                    date = System.currentTimeMillis()
                )
                
                // Benchmark health metrics query
                val healthMetrics = database.healthDao().getHealthMetricsSummary(
                    userId = 1L,
                    startTime = System.currentTimeMillis() - 86400000 * 7, // 7 days
                    endTime = System.currentTimeMillis()
                )
                
                assert(workouts.isNotEmpty())
                assert(nutritionSummary != null)
                assert(healthMetrics.isNotEmpty())
            }
        }
    }
    
    @Test
    fun benchmarkAlgorithmPerformance() {
        benchmarkRule.measureRepeated {
            val user = TestData.createTestUser()
            val weights = TestData.createTestWeights(30) // 30 days of data
            val workouts = TestData.createTestWorkouts(12) // 12 workouts
            
            // Benchmark BMR calculation
            val bmr = nutritionEngine.calculateBMR(
                age = user.age,
                gender = user.gender,
                heightCm = user.heightCm,
                weightKg = 75f
            )
            
            // Benchmark TDEE calculation
            val tdee = nutritionEngine.calculateTDEE(bmr, user.activityLevel)
            
            // Benchmark adaptive adjustment calculation
            val adjustment = nutritionEngine.calculateAdaptiveAdjustment(
                recentWeights = weights,
                currentCalories = 2000f,
                goalKgPerWeek = -0.5f
            )
            
            // Benchmark progress analytics
            val sets = (1..20).map { 
                TestData.createTestSet(weightKg = 80f + it, reps = 10)
            }
            val volume = progressAnalytics.calculateTrainingVolume(sets)
            val oneRepMax = progressAnalytics.calculateOneRepMax(80f, 10)
            
            assert(bmr > 0)
            assert(tdee > bmr)
            assert(adjustment.calorieAdjustment != 0f)
            assert(volume > 0)
            assert(oneRepMax > 80f)
        }
    }
    
    @Test
    fun benchmarkNetworkOperations() {
        benchmarkRule.measureRepeated {
            runBlocking {
                performanceMonitor.startTrace("network_operations")
                
                // Simulate network requests (using mocked responses)
                val searchTime = measureTimeMillis {
                    // Food search simulation
                    repeat(5) {
                        kotlinx.coroutines.delay(100) // Simulate network latency
                    }
                }
                
                val syncTime = measureTimeMillis {
                    // Health data sync simulation
                    repeat(10) {
                        kotlinx.coroutines.delay(50) // Simulate sync operations
                    }
                }
                
                performanceMonitor.endTrace("network_operations")
                
                // Network operations should complete quickly with good connection
                assert(searchTime < 1000) { "Food search took ${searchTime}ms" }
                assert(syncTime < 1000) { "Health sync took ${syncTime}ms" }
            }
        }
    }
    
    @Test
    fun benchmarkMemoryUsage() {
        benchmarkRule.measureRepeated {
            val runtime = Runtime.getRuntime()
            
            runWithTimingDisabled {
                // Force garbage collection before measurement
                System.gc()
                Thread.sleep(100)
            }
            
            val initialMemory = runtime.totalMemory() - runtime.freeMemory()
            
            // Perform memory-intensive operations
            val largeDataSet = (1..1000).map { 
                TestData.createTestWorkout(id = it.toLong())
            }
            
            val workoutDetails = largeDataSet.map { workout ->
                // Simulate loading workout details
                (1..10).map { exerciseIndex ->
                    TestData.createTestWorkoutExercise(
                        workoutId = workout.id,
                        exerciseId = exerciseIndex.toLong()
                    )
                }
            }
            
            val finalMemory = runtime.totalMemory() - runtime.freeMemory()
            val memoryUsed = (finalMemory - initialMemory) / 1024 / 1024 // MB
            
            runWithTimingDisabled {
                // Memory usage should be reasonable (under 100MB for test data)
                assert(memoryUsed < 100) { "Memory usage: ${memoryUsed}MB" }
                
                // Clean up
                largeDataSet.drop(largeDataSet.size)
                workoutDetails.drop(workoutDetails.size)
                System.gc()
            }
        }
    }
    
    @Test
    fun benchmarkBatteryUsage() {
        // Note: This test monitors battery usage patterns during intensive operations
        benchmarkRule.measureRepeated {
            performanceMonitor.startTrace("battery_usage")
            
            runWithTimingDisabled {
                performanceMonitor.startBatteryMonitoring()
            }
            
            // Simulate intensive operations
            runBlocking {
                // Database operations
                repeat(100) {
                    val workout = TestData.createTestWorkout()
                    database.workoutDao().insertWorkout(workout)
                }
                
                // Algorithm calculations
                repeat(50) {
                    nutritionEngine.calculateBMR(30, "male", 175f, 75f)
                    progressAnalytics.calculateOneRepMax(80f, 10)
                }
                
                // Network simulations
                repeat(20) {
                    kotlinx.coroutines.delay(100) // Simulate network requests
                }
            }
            
            runWithTimingDisabled {
                val batteryUsage = performanceMonitor.stopBatteryMonitoring()
                performanceMonitor.endTrace("battery_usage")
                
                // Battery usage should be minimal for these operations
                assert(batteryUsage.percentageUsed < 1.0) { 
                    "Battery usage: ${batteryUsage.percentageUsed}%" 
                }
            }
        }
    }
    
    @Test
    fun benchmarkUIRenderingPerformance() {
        benchmarkRule.measureRepeated {
            performanceMonitor.startTrace("ui_rendering")
            
            // Simulate UI rendering operations
            val renderTime = measureTimeMillis {
                // Simulate complex list rendering
                repeat(100) {
                    // Simulate view creation and binding
                    val workout = TestData.createTestWorkout()
                    val exercises = (1..5).map { 
                        TestData.createTestWorkoutExercise(workoutId = workout.id)
                    }
                    val sets = exercises.flatMap { exercise ->
                        (1..3).map { 
                            TestData.createTestSet(workoutExerciseId = exercise.id)
                        }
                    }
                    
                    // Simulate calculations for UI display
                    progressAnalytics.calculateTrainingVolume(sets)
                }
            }
            
            performanceMonitor.endTrace("ui_rendering")
            
            // UI rendering should be smooth (under 16ms per frame for 60fps)
            val avgRenderTime = renderTime / 100.0
            assert(avgRenderTime < 16) { 
                "Average render time: ${avgRenderTime}ms per item" 
            }
        }
    }
    
    @Test
    fun benchmarkDataSyncPerformance() {
        benchmarkRule.measureRepeated {
            performanceMonitor.startTrace("data_sync")
            
            runBlocking {
                // Simulate large data sync
                val syncTime = measureTimeMillis {
                    // Simulate downloading 1000 food items
                    val foods = (1..1000).map { 
                        TestData.createTestFood(id = it.toLong())
                    }
                    
                    // Batch insert to database
                    foods.chunked(100).forEach { batch ->
                        batch.forEach { food ->
                            database.nutritionDao().insertFood(food)
                        }
                    }
                    
                    // Simulate syncing 200 workouts
                    val workouts = (1..200).map { 
                        TestData.createTestWorkout(id = it.toLong())
                    }
                    
                    workouts.forEach { workout ->
                        database.workoutDao().insertWorkout(workout)
                    }
                }
                
                performanceMonitor.endTrace("data_sync")
                
                // Large sync should complete within reasonable time
                assert(syncTime < 10000) { "Data sync took ${syncTime}ms" }
            }
        }
    }
    
    private fun measureTimeMillis(block: () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
}