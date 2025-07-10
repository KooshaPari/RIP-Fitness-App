package com.fitnessapp.core.database.dao

import androidx.room.*
import com.fitnessapp.core.database.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Recipe entities
 */
@Dao
interface RecipeDao {
    
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?
    
    @Query("SELECT * FROM recipes WHERE userId = :userId ORDER BY name ASC")
    suspend fun getRecipesForUser(userId: String): List<RecipeEntity>
    
    @Query("SELECT * FROM recipes WHERE userId = :userId ORDER BY name ASC")
    fun getRecipesForUserFlow(userId: String): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun searchRecipesByName(searchQuery: String, limit: Int = 50): List<RecipeEntity>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    suspend fun getFavoriteRecipes(): List<RecipeEntity>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipesFlow(): Flow<List<RecipeEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)
    
    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
    
    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
    
    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)
    
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateRecipeFavoriteStatus(id: String, isFavorite: Boolean)
}