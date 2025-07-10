package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.MealEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for Meal entities
 */
@Dao
interface MealDao {
    
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: String): MealEntity?
    
    @Query("SELECT * FROM meals WHERE userId = :userId AND date = :date ORDER BY mealType ASC, timestamp DESC")
    suspend fun getMealsForUserAndDate(userId: String, date: LocalDate): List<MealEntity>
    
    @Query("SELECT * FROM meals WHERE userId = :userId AND date = :date ORDER BY mealType ASC, timestamp DESC")
    fun getMealsForUserAndDateFlow(userId: String, date: LocalDate): Flow<List<MealEntity>>
    
    @Query("SELECT * FROM meals WHERE userId = :userId AND date = :date AND mealType = :mealType ORDER BY timestamp DESC")
    suspend fun getMealsForUserDateAndType(userId: String, date: LocalDate, mealType: String): List<MealEntity>
    
    @Query("SELECT * FROM meals WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC, mealType ASC, timestamp DESC")
    suspend fun getMealsForUserAndDateRange(userId: String, startDate: LocalDate, endDate: LocalDate): List<MealEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)
    
    @Update
    suspend fun updateMeal(meal: MealEntity)
    
    @Delete
    suspend fun deleteMeal(meal: MealEntity)
    
    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMealById(id: String)
    
    @Query("DELETE FROM meals WHERE userId = :userId AND date = :date")
    suspend fun deleteMealsForUserAndDate(userId: String, date: LocalDate)
}