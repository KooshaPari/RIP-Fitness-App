package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.QuickAddEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for QuickAdd entities
 */
@Dao
interface QuickAddDao {
    
    @Query("SELECT * FROM quick_adds WHERE id = :id")
    suspend fun getQuickAddById(id: String): QuickAddEntity?
    
    @Query("SELECT * FROM quick_adds WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getQuickAddsForUserAndDate(userId: String, date: LocalDate): List<QuickAddEntity>
    
    @Query("SELECT * FROM quick_adds WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getQuickAddsForUserAndDateFlow(userId: String, date: LocalDate): Flow<List<QuickAddEntity>>
    
    @Query("SELECT * FROM quick_adds WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, timestamp DESC")
    suspend fun getQuickAddsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<QuickAddEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickAdd(quickAdd: QuickAddEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuickAdds(quickAdds: List<QuickAddEntity>)
    
    @Update
    suspend fun updateQuickAdd(quickAdd: QuickAddEntity)
    
    @Delete
    suspend fun deleteQuickAdd(quickAdd: QuickAddEntity)
    
    @Query("DELETE FROM quick_adds WHERE id = :id")
    suspend fun deleteQuickAddById(id: String)
    
    @Query("DELETE FROM quick_adds WHERE userId = :userId AND date = :date")
    suspend fun deleteQuickAddsForUserAndDate(userId: String, date: LocalDate)
}