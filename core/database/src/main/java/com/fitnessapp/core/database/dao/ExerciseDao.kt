package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Exercise entities
 */
@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?
    
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    suspend fun getAllExercises(): List<ExerciseEntity>
    
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercisesFlow(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun searchExercisesByName(searchQuery: String, limit: Int = 50): List<ExerciseEntity>
    
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    suspend fun getExercisesByCategory(category: String): List<ExerciseEntity>
    
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategoryFlow(category: String): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE muscleGroup = :muscleGroup ORDER BY name ASC")
    suspend fun getExercisesByMuscleGroup(muscleGroup: String): List<ExerciseEntity>
    
    @Query("SELECT * FROM exercises WHERE isUserCreated = 1 ORDER BY name ASC")
    suspend fun getUserCreatedExercises(): List<ExerciseEntity>
    
    @Query("SELECT * FROM exercises WHERE isUserCreated = 1 ORDER BY name ASC")
    fun getUserCreatedExercisesFlow(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT DISTINCT category FROM exercises ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
    
    @Query("SELECT DISTINCT muscleGroup FROM exercises ORDER BY muscleGroup ASC")
    suspend fun getAllMuscleGroups(): List<String>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
    
    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
    
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
    
    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: String)
}