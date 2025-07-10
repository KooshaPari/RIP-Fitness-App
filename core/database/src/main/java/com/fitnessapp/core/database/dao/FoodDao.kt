package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.FoodEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Food entities
 */
@Dao
interface FoodDao {
    
    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: String): FoodEntity?
    
    @Query("SELECT * FROM foods WHERE barcode = :barcode")
    suspend fun getFoodByBarcode(barcode: String): FoodEntity?
    
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun searchFoodsByName(searchQuery: String, limit: Int = 50): List<FoodEntity>
    
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC LIMIT :limit")
    fun searchFoodsByNameFlow(searchQuery: String, limit: Int = 50): Flow<List<FoodEntity>>
    
    @Query("SELECT * FROM foods WHERE isUserCreated = 1 ORDER BY name ASC")
    suspend fun getUserCreatedFoods(): List<FoodEntity>
    
    @Query("SELECT * FROM foods WHERE isUserCreated = 1 ORDER BY name ASC")
    fun getUserCreatedFoodsFlow(): Flow<List<FoodEntity>>
    
    @Query("SELECT * FROM foods WHERE isFavorite = 1 ORDER BY name ASC")
    suspend fun getFavoriteFoods(): List<FoodEntity>
    
    @Query("SELECT * FROM foods WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFoodsFlow(): Flow<List<FoodEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodEntity>)
    
    @Update
    suspend fun updateFood(food: FoodEntity)
    
    @Delete
    suspend fun deleteFood(food: FoodEntity)
    
    @Query("DELETE FROM foods WHERE id = :id")
    suspend fun deleteFoodById(id: String)
    
    @Query("UPDATE foods SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFoodFavoriteStatus(id: String, isFavorite: Boolean)
}