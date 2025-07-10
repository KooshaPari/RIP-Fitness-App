package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.UserGoalsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserGoals entities
 */
@Dao
interface UserGoalsDao {
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId")
    suspend fun getGoalsForUser(userId: String): UserGoalsEntity?
    
    @Query("SELECT * FROM user_goals WHERE userId = :userId")
    fun getGoalsForUserFlow(userId: String): Flow<UserGoalsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: UserGoalsEntity)
    
    @Update
    suspend fun updateGoals(goals: UserGoalsEntity)
    
    @Delete
    suspend fun deleteGoals(goals: UserGoalsEntity)
    
    @Query("DELETE FROM user_goals WHERE userId = :userId")
    suspend fun deleteGoalsForUser(userId: String)
}