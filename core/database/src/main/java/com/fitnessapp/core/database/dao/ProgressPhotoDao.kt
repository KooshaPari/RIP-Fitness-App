package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.ProgressPhotoEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for ProgressPhoto entities
 */
@Dao
interface ProgressPhotoDao {
    
    @Query("SELECT * FROM progress_photos WHERE id = :id")
    suspend fun getProgressPhotoById(id: String): ProgressPhotoEntity?
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY dateTaken DESC")
    suspend fun getProgressPhotosForUser(userId: String): List<ProgressPhotoEntity>
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY dateTaken DESC")
    fun getProgressPhotosForUserFlow(userId: String): Flow<List<ProgressPhotoEntity>>
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND photoType = :photoType ORDER BY dateTaken DESC")
    suspend fun getProgressPhotosByType(userId: String, photoType: String): List<ProgressPhotoEntity>
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND photoType = :photoType ORDER BY dateTaken DESC")
    fun getProgressPhotosByTypeFlow(userId: String, photoType: String): Flow<List<ProgressPhotoEntity>>
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND dateTaken = :date ORDER BY timestamp DESC")
    suspend fun getProgressPhotosForUserAndDate(userId: String, date: LocalDate): List<ProgressPhotoEntity>
    
    @Query("SELECT * FROM progress_photos WHERE userId = :userId AND dateTaken BETWEEN :startDate AND :endDate ORDER BY dateTaken DESC")
    suspend fun getProgressPhotosForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<ProgressPhotoEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(progressPhoto: ProgressPhotoEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhotos(progressPhotos: List<ProgressPhotoEntity>)
    
    @Update
    suspend fun updateProgressPhoto(progressPhoto: ProgressPhotoEntity)
    
    @Delete
    suspend fun deleteProgressPhoto(progressPhoto: ProgressPhotoEntity)
    
    @Query("DELETE FROM progress_photos WHERE id = :id")
    suspend fun deleteProgressPhotoById(id: String)
    
    @Query("DELETE FROM progress_photos WHERE userId = :userId AND dateTaken = :date")
    suspend fun deleteProgressPhotosForUserAndDate(userId: String, date: LocalDate)
}