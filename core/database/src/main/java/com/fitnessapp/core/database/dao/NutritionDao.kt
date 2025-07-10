package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.NutritionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Nutrition entities
 */
@Dao
interface NutritionDao {
    
    @Query("SELECT * FROM nutrition WHERE id = :id")
    suspend fun getNutritionById(id: String): NutritionEntity?
    
    @Query("SELECT * FROM nutrition WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getNutritionForUserAndDate(userId: String, date: LocalDate): List<NutritionEntity>
    
    @Query("SELECT * FROM nutrition WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getNutritionForUserAndDateFlow(userId: String, date: LocalDate): Flow<List<NutritionEntity>>
    
    @Query("SELECT * FROM nutrition WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, timestamp DESC")
    suspend fun getNutritionForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<NutritionEntity>
    
    @Query("SELECT * FROM nutrition WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, timestamp DESC")
    fun getNutritionForUserAndDateRangeFlow(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<NutritionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionList(nutritionList: List<NutritionEntity>)
    
    @Update
    suspend fun updateNutrition(nutrition: NutritionEntity)
    
    @Delete
    suspend fun deleteNutrition(nutrition: NutritionEntity)
    
    @Query("DELETE FROM nutrition WHERE id = :id")
    suspend fun deleteNutritionById(id: String)
    
    @Query("DELETE FROM nutrition WHERE userId = :userId AND date = :date")
    suspend fun deleteNutritionForUserAndDate(userId: String, date: LocalDate)
}