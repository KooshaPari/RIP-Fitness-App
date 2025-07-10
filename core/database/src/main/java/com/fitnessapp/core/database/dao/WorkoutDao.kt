package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.WorkoutEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Workout entities
 */
@Dao
interface WorkoutDao {
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: String): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY startTime DESC")
    suspend fun getWorkoutsForUser(userId: String): List<WorkoutEntity>
    
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY startTime DESC")
    fun getWorkoutsForUserFlow(userId: String): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND date(startTime) = :date ORDER BY startTime DESC")
    suspend fun getWorkoutsForUserAndDate(userId: String, date: LocalDate): List<WorkoutEntity>
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND date(startTime) BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    suspend fun getWorkoutsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<WorkoutEntity>
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveWorkout(userId: String): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    fun getActiveWorkoutFlow(userId: String): Flow<WorkoutEntity?>
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 1 ORDER BY startTime DESC LIMIT :limit")
    suspend fun getCompletedWorkoutsForUser(userId: String, limit: Int = 10): List<WorkoutEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)
    
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)
    
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
    
    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)
    
    @Query("UPDATE workouts SET isCompleted = :isCompleted, endTime = :endTime WHERE id = :id")
    suspend fun completeWorkout(id: String, isCompleted: Boolean, endTime: Long?)
}