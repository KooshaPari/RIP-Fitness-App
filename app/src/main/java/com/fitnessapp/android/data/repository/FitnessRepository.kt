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
        return userDao.getUser()
    }
    
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }
    
    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }
    
    override suspend fun getUserGoals(): UserGoalsEntity? {
        return userDao.getUserGoals()
    }
    
    override suspend fun insertUserGoals(goals: UserGoalsEntity) {
        userDao.insertUserGoals(goals)
    }
    
    override suspend fun updateUserGoals(goals: UserGoalsEntity) {
        userDao.updateUserGoals(goals)
    }
    
    override fun getAllWorkouts(): Flow<List<WorkoutEntity>> {
        return workoutDao.getAllWorkouts()
    }
    
    override fun getRecentWorkouts(limit: Int): Flow<List<WorkoutEntity>> {
        return workoutDao.getRecentWorkouts(limit)
    }
    
    override suspend fun insertWorkout(workout: WorkoutEntity): Long {
        return workoutDao.insertWorkout(workout)
    }
    
    override suspend fun updateWorkout(workout: WorkoutEntity) {
        workoutDao.updateWorkout(workout)
    }
    
    override suspend fun deleteWorkout(workout: WorkoutEntity) {
        workoutDao.deleteWorkout(workout)
    }
    
    override fun getAllNutrition(): Flow<List<NutritionEntity>> {
        return nutritionDao.getAllNutrition()
    }
    
    override fun getTodayNutrition(): Flow<List<NutritionEntity>> {
        return nutritionDao.getTodayNutrition()
    }
    
    override suspend fun insertNutrition(nutrition: NutritionEntity): Long {
        return nutritionDao.insertNutrition(nutrition)
    }
    
    override suspend fun updateNutrition(nutrition: NutritionEntity) {
        nutritionDao.updateNutrition(nutrition)
    }
    
    override suspend fun deleteNutrition(nutrition: NutritionEntity) {
        nutritionDao.deleteNutrition(nutrition)
    }
    
    override fun getAllHealthMetrics(): Flow<List<HealthMetricEntity>> {
        return healthMetricDao.getAllHealthMetrics()
    }
    
    override fun getRecentHealthMetrics(limit: Int): Flow<List<HealthMetricEntity>> {
        return healthMetricDao.getRecentHealthMetrics(limit)
    }
    
    override suspend fun insertHealthMetric(healthMetric: HealthMetricEntity): Long {
        return healthMetricDao.insertHealthMetric(healthMetric)
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