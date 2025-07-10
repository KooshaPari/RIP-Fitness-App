package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for BodyMeasurement entities
 */
@Dao
interface BodyMeasurementDao {
    
    @Query("SELECT * FROM body_measurements WHERE id = :id")
    suspend fun getBodyMeasurementById(id: String): BodyMeasurementEntity?
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY date DESC")
    suspend fun getBodyMeasurementsForUser(userId: String): List<BodyMeasurementEntity>
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY date DESC")
    fun getBodyMeasurementsForUserFlow(userId: String): Flow<List<BodyMeasurementEntity>>
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId AND date = :date")
    suspend fun getBodyMeasurementForUserAndDate(userId: String, date: LocalDate): BodyMeasurementEntity?
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getBodyMeasurementsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<BodyMeasurementEntity>
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getBodyMeasurementsForUserAndDateRangeFlow(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<BodyMeasurementEntity>>
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestBodyMeasurementForUser(userId: String): BodyMeasurementEntity?
    
    @Query("SELECT * FROM body_measurements WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLatestBodyMeasurementForUserFlow(userId: String): Flow<BodyMeasurementEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMeasurement(bodyMeasurement: BodyMeasurementEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMeasurements(bodyMeasurements: List<BodyMeasurementEntity>)
    
    @Update
    suspend fun updateBodyMeasurement(bodyMeasurement: BodyMeasurementEntity)
    
    @Delete
    suspend fun deleteBodyMeasurement(bodyMeasurement: BodyMeasurementEntity)
    
    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteBodyMeasurementById(id: String)
    
    @Query("DELETE FROM body_measurements WHERE userId = :userId AND date = :date")
    suspend fun deleteBodyMeasurementForUserAndDate(userId: String, date: LocalDate)
}