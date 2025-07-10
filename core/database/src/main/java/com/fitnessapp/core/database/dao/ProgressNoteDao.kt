package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.ProgressNoteEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for ProgressNote entities
 */
@Dao
interface ProgressNoteDao {
    
    @Query("SELECT * FROM progress_notes WHERE id = :id")
    suspend fun getProgressNoteById(id: String): ProgressNoteEntity?
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId ORDER BY date DESC")
    suspend fun getProgressNotesForUser(userId: String): List<ProgressNoteEntity>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId ORDER BY date DESC")
    fun getProgressNotesForUserFlow(userId: String): Flow<List<ProgressNoteEntity>>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId AND category = :category ORDER BY date DESC")
    suspend fun getProgressNotesByCategory(userId: String, category: String): List<ProgressNoteEntity>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId AND category = :category ORDER BY date DESC")
    fun getProgressNotesByCategoryFlow(userId: String, category: String): Flow<List<ProgressNoteEntity>>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getProgressNotesForUserAndDate(userId: String, date: LocalDate): List<ProgressNoteEntity>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getProgressNotesForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<ProgressNoteEntity>
    
    @Query("SELECT * FROM progress_notes WHERE userId = :userId AND title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    suspend fun searchProgressNotes(userId: String, searchQuery: String): List<ProgressNoteEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressNote(progressNote: ProgressNoteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressNotes(progressNotes: List<ProgressNoteEntity>)
    
    @Update
    suspend fun updateProgressNote(progressNote: ProgressNoteEntity)
    
    @Delete
    suspend fun deleteProgressNote(progressNote: ProgressNoteEntity)
    
    @Query("DELETE FROM progress_notes WHERE id = :id")
    suspend fun deleteProgressNoteById(id: String)
    
    @Query("DELETE FROM progress_notes WHERE userId = :userId AND date = :date")
    suspend fun deleteProgressNotesForUserAndDate(userId: String, date: LocalDate)
}