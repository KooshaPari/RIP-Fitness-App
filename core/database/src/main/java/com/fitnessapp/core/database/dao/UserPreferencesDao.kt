package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserPreferences entities
 */
@Dao
interface UserPreferencesDao {
    
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getPreferencesForUser(userId: String): UserPreferencesEntity?
    
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getPreferencesForUserFlow(userId: String): Flow<UserPreferencesEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferencesEntity)
    
    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)
    
    @Delete
    suspend fun deletePreferences(preferences: UserPreferencesEntity)
    
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deletePreferencesForUser(userId: String)
}