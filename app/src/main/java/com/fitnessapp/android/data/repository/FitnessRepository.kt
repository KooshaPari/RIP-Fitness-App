package com.fitnessapp.android.data.repository

import com.fitnessapp.core.database.entities.WorkoutEntity
import com.fitnessapp.core.database.entities.NutritionEntity
import com.fitnessapp.core.database.entities.HealthMetricEntity
import com.fitnessapp.core.database.entities.UserEntity
import com.fitnessapp.core.database.entities.UserGoalsEntity
import com.fitnessapp.core.database.FitnessDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central repository interface for all fitness data
 */
interface FitnessRepository {
    // User data
    suspend fun getUser(): UserEntity?
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    
    // User goals
    suspend fun getUserGoals(): UserGoalsEntity?
    suspend fun insertUserGoals(goals: UserGoalsEntity)
    suspend fun updateUserGoals(goals: UserGoalsEntity)
    
    // Workouts
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    fun getRecentWorkouts(limit: Int = 10): Flow<List<WorkoutEntity>>
    suspend fun insertWorkout(workout: WorkoutEntity): Long
    suspend fun updateWorkout(workout: WorkoutEntity)
    suspend fun deleteWorkout(workout: WorkoutEntity)
    
    // Nutrition
    fun getAllNutrition(): Flow<List<NutritionEntity>>
    fun getTodayNutrition(): Flow<List<NutritionEntity>>
    suspend fun insertNutrition(nutrition: NutritionEntity): Long
    suspend fun updateNutrition(nutrition: NutritionEntity)
    suspend fun deleteNutrition(nutrition: NutritionEntity)
    
    // Health metrics
    fun getAllHealthMetrics(): Flow<List<HealthMetricEntity>>
    fun getRecentHealthMetrics(limit: Int = 30): Flow<List<HealthMetricEntity>>
    suspend fun insertHealthMetric(healthMetric: HealthMetricEntity): Long
    suspend fun updateHealthMetric(healthMetric: HealthMetricEntity)
    suspend fun deleteHealthMetric(healthMetric: HealthMetricEntity)
    
    // Dashboard data
    fun getDashboardData(): Flow<DashboardData>
}

/**
 * Data class for dashboard summary
 */
data class DashboardData(
    val recentWorkouts: List<WorkoutEntity>,
    val todayNutrition: List<NutritionEntity>,
    val recentHealthMetrics: List<HealthMetricEntity>,
    val user: UserEntity?,
    val userGoals: UserGoalsEntity?
)

/**
 * Implementation of FitnessRepository
 */
@Singleton
class FitnessRepositoryImpl @Inject constructor(
    private val database: FitnessDatabase
) : FitnessRepository {
    
    private val userDao = database.userDao()
    private val workoutDao = database.workoutDao()
    private val nutritionDao = database.nutritionDao()
    private val healthMetricDao = database.healthMetricDao()
    
    override suspend fun getUser(): UserEntity? {
        return userDao.getCurrentUser()
    }
    
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }
    
    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }
    
    override suspend fun getUserGoals(): UserGoalsEntity? {
        return database.userGoalsDao().getGoalsForUser("1") // Default user ID for now
    }
    
    override suspend fun insertUserGoals(goals: UserGoalsEntity) {
        database.userGoalsDao().insertGoals(goals)
    }
    
    override suspend fun updateUserGoals(goals: UserGoalsEntity) {
        database.userGoalsDao().updateGoals(goals)
    }
    
    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getWorkoutsForUserFlow("1") // Default user ID for now
    }
    
    override fun getRecentWorkouts(limit: Int): Flow<List<WorkoutEntity>> {
        return workoutDao.getCompletedWorkoutsForUser("1", limit).let {
            kotlinx.coroutines.flow.flowOf(emptyList()) // TODO: Implement properly
        }
    }
    
    override suspend fun insertWorkout(workout: WorkoutEntity): Long {
        workoutDao.insertWorkout(workout)
        return 1L // TODO: Return actual ID
    }
    
    override suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }
    
    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }
    
    override fun getAllNutrition(): Flow<List<NutritionEntity>> {
        return kotlinx.coroutines.flow.flowOf(emptyList()) // TODO: Implement properly
    }
    
    override fun getTodayNutrition(): Flow<List<NutritionEntity>> {
        return nutritionDao.getNutritionForUserAndDateFlow("1", java.time.LocalDate.now())
    }
    
    override suspend fun insertNutrition(nutrition: NutritionEntity): Long {
        nutritionDao.insertNutrition(nutrition)
        return 1L // TODO: Return actual ID
    }
    
    override suspend fun updateNutrition(nutrition: NutritionEntity) {
        nutritionDao.updateNutrition(nutrition)
    }
    
    override suspend fun deleteNutrition(nutrition: NutritionEntity) {
        nutritionDao.deleteNutrition(nutrition)
    }
    
    override fun getAllHealthMetrics(): Flow<List<HealthMetricEntity>> {
        return healthMetricDao.getHealthMetricsForUserFlow("1")
    }
    
    override fun getRecentHealthMetrics(limit: Int): Flow<List<HealthMetricEntity>> {
        return healthMetricDao.getHealthMetricsForUserFlow("1")
    }
    
    override suspend fun insertHealthMetric(healthMetric: HealthMetricEntity): Long {
        healthMetricDao.insertHealthMetric(healthMetric)
        return 1L // TODO: Return actual ID
    }
    
    override suspend fun updateHealthMetric(healthMetric: HealthMetricEntity) {
        healthMetricDao.updateHealthMetric(healthMetric)
    }
    
    override suspend fun deleteHealthMetric(healthMetric: HealthMetricEntity) {
        healthMetricDao.deleteHealthMetric(healthMetric)
    }
    
    override fun getDashboardData(): Flow<DashboardData> {
        return kotlinx.coroutines.flow.combine(
            getRecentWorkouts(5),
            getTodayNutrition(),
            getRecentHealthMetrics(7)
        ) { workouts, nutrition, healthMetrics ->
            DashboardData(
                recentWorkouts = workouts,
                todayNutrition = nutrition,
                recentHealthMetrics = healthMetrics,
                user = getUser(),
                userGoals = getUserGoals()
            )
        }
    }
}