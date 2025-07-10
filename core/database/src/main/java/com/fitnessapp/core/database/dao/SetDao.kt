package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.SetEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Set entities
 */
@Dao
interface SetDao {
    
    @Query("SELECT * FROM sets WHERE id = :id")
    suspend fun getSetById(id: String): SetEntity?
    
    @Query("SELECT * FROM sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForWorkoutExercise(workoutExerciseId: String): List<SetEntity>
    
    @Query("SELECT * FROM sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setNumber ASC")
    fun getSetsForWorkoutExerciseFlow(workoutExerciseId: String): Flow<List<SetEntity>>
    
    @Query("SELECT * FROM sets WHERE exerciseId = :exerciseId ORDER BY createdAt DESC")
    suspend fun getSetsForExercise(exerciseId: String): List<SetEntity>
    
    @Query("SELECT * FROM sets WHERE exerciseId = :exerciseId AND isCompleted = 1 ORDER BY weight DESC, reps DESC LIMIT 1")
    suspend fun getPersonalRecordForExercise(exerciseId: String): SetEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<SetEntity>)
    
    @Update
    suspend fun updateSet(set: SetEntity)
    
    @Delete
    suspend fun deleteSet(set: SetEntity)
    
    @Query("DELETE FROM sets WHERE id = :id")
    suspend fun deleteSetById(id: String)
    
    @Query("DELETE FROM sets WHERE workoutExerciseId = :workoutExerciseId")
    suspend fun deleteSetsForWorkoutExercise(workoutExerciseId: String)
    
    @Query("UPDATE sets SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateSetCompletion(id: String, isCompleted: Boolean)
}