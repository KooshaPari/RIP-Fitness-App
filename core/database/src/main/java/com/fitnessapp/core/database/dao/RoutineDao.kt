package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.RoutineEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Routine entities
 */
@Dao
interface RoutineDao {
    
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: String): RoutineEntity?
    
    @Query("SELECT * FROM routines WHERE userId = :userId ORDER BY name ASC")
    suspend fun getRoutinesForUser(userId: String): List<RoutineEntity>
    
    @Query("SELECT * FROM routines WHERE userId = :userId ORDER BY name ASC")
    fun getRoutinesForUserFlow(userId: String): Flow<List<RoutineEntity>>
    
    @Query("SELECT * FROM routines WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun searchRoutinesByName(searchQuery: String, limit: Int = 50): List<RoutineEntity>
    
    @Query("SELECT * FROM routines WHERE isFavorite = 1 ORDER BY name ASC")
    suspend fun getFavoriteRoutines(): List<RoutineEntity>
    
    @Query("SELECT * FROM routines WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRoutinesFlow(): Flow<List<RoutineEntity>>
    
    @Query("SELECT * FROM routines WHERE isTemplate = 1 ORDER BY name ASC")
    suspend fun getTemplateRoutines(): List<RoutineEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)
    
    @Update
    suspend fun updateRoutine(routine: RoutineEntity)
    
    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)
    
    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutineById(id: String)
    
    @Query("UPDATE routines SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateRoutineFavoriteStatus(id: String, isFavorite: Boolean)
}