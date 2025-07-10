package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.WeightEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Weight entities
 */
@Dao
interface WeightDao {
    
    @Query("SELECT * FROM weights WHERE id = :id")
    suspend fun getWeightById(id: String): WeightEntity?
    
    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC")
    suspend fun getWeightsForUser(userId: String): List<WeightEntity>
    
    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC")
    fun getWeightsForUserFlow(userId: String): Flow<List<WeightEntity>>
    
    @Query("SELECT * FROM weights WHERE userId = :userId AND date = :date")
    suspend fun getWeightForUserAndDate(userId: String, date: LocalDate): WeightEntity?
    
    @Query("SELECT * FROM weights WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getWeightsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<WeightEntity>
    
    @Query("SELECT * FROM weights WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getWeightsForUserAndDateRangeFlow(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<WeightEntity>>
    
    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestWeightForUser(userId: String): WeightEntity?
    
    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLatestWeightForUserFlow(userId: String): Flow<WeightEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeights(weights: List<WeightEntity>)
    
    @Update
    suspend fun updateWeight(weight: WeightEntity)
    
    @Delete
    suspend fun deleteWeight(weight: WeightEntity)
    
    @Query("DELETE FROM weights WHERE id = :id")
    suspend fun deleteWeightById(id: String)
    
    @Query("DELETE FROM weights WHERE userId = :userId AND date = :date")
    suspend fun deleteWeightForUserAndDate(userId: String, date: LocalDate)
}