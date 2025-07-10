package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.WorkoutExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for WorkoutExercise entities
 */
@Dao
interface WorkoutExerciseDao {
    
    @Query("SELECT * FROM workout_exercises WHERE id = :id")
    suspend fun getWorkoutExerciseById(id: String): WorkoutExerciseEntity?
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    suspend fun getExercisesForWorkout(workoutId: String): List<WorkoutExerciseEntity>
    
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    fun getExercisesForWorkoutFlow(workoutId: String): Flow<List<WorkoutExerciseEntity>>
    
    @Query("SELECT * FROM workout_exercises WHERE exerciseId = :exerciseId ORDER BY createdAt DESC")
    suspend fun getWorkoutsForExercise(exerciseId: String): List<WorkoutExerciseEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(workoutExercises: List<WorkoutExerciseEntity>)
    
    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    
    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExerciseEntity)
    
    @Query("DELETE FROM workout_exercises WHERE id = :id")
    suspend fun deleteWorkoutExerciseById(id: String)
    
    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: String)
}