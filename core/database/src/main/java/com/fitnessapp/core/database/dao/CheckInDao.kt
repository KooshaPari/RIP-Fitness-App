package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.CheckInEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for CheckIn entities
 */
@Dao
interface CheckInDao {
    
    @Query("SELECT * FROM check_ins WHERE id = :id")
    suspend fun getCheckInById(id: String): CheckInEntity?
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY date DESC")
    suspend fun getCheckInsForUser(userId: String): List<CheckInEntity>
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY date DESC")
    fun getCheckInsForUserFlow(userId: String): Flow<List<CheckInEntity>>
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId AND date = :date")
    suspend fun getCheckInForUserAndDate(userId: String, date: LocalDate): CheckInEntity?
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getCheckInsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<CheckInEntity>
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getCheckInsForUserAndDateRangeFlow(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<CheckInEntity>>
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestCheckInForUser(userId: String): CheckInEntity?
    
    @Query("SELECT * FROM check_ins WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLatestCheckInForUserFlow(userId: String): Flow<CheckInEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIns(checkIns: List<CheckInEntity>)
    
    @Update
    suspend fun updateCheckIn(checkIn: CheckInEntity)
    
    @Delete
    suspend fun deleteCheckIn(checkIn: CheckInEntity)
    
    @Query("DELETE FROM check_ins WHERE id = :id")
    suspend fun deleteCheckInById(id: String)
    
    @Query("DELETE FROM check_ins WHERE userId = :userId AND date = :date")
    suspend fun deleteCheckInForUserAndDate(userId: String, date: LocalDate)
}