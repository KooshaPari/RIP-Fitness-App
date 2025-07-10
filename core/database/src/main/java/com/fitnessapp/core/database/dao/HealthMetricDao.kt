package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.HealthMetricEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for HealthMetric entities
 */
@Dao
interface HealthMetricDao {
    
    @Query("SELECT * FROM health_metrics WHERE id = :id")
    suspend fun getHealthMetricById(id: String): HealthMetricEntity?
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId ORDER BY date DESC")
    suspend fun getHealthMetricsForUser(userId: String): List<HealthMetricEntity>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId ORDER BY date DESC")
    fun getHealthMetricsForUserFlow(userId: String): Flow<List<HealthMetricEntity>>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId AND metricType = :metricType ORDER BY date DESC")
    suspend fun getHealthMetricsByType(userId: String, metricType: String): List<HealthMetricEntity>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId AND metricType = :metricType ORDER BY date DESC")
    fun getHealthMetricsByTypeFlow(userId: String, metricType: String): Flow<List<HealthMetricEntity>>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getHealthMetricsForUserAndDate(userId: String, date: LocalDate): List<HealthMetricEntity>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getHealthMetricsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<HealthMetricEntity>
    
    @Query("SELECT * FROM health_metrics WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getHealthMetricsForUserAndDateRangeFlow(userId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<HealthMetricEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthMetric(healthMetric: HealthMetricEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthMetrics(healthMetrics: List<HealthMetricEntity>)
    
    @Update
    suspend fun updateHealthMetric(healthMetric: HealthMetricEntity)
    
    @Delete
    suspend fun deleteHealthMetric(healthMetric: HealthMetricEntity)
    
    @Query("DELETE FROM health_metrics WHERE id = :id")
    suspend fun deleteHealthMetricById(id: String)
    
    @Query("DELETE FROM health_metrics WHERE userId = :userId AND date = :date")
    suspend fun deleteHealthMetricsForUserAndDate(userId: String, date: LocalDate)
}