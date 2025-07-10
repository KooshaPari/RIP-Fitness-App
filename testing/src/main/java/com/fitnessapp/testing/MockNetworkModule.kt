package com.fitnessapp.testing

import com.fitnessapp.core.network.api.*
import com.fitnessapp.core.network.model.exercise.ExerciseResponse
import com.fitnessapp.core.network.model.fatsecret.FatSecretResponse
import com.fitnessapp.core.network.model.nutritionix.NutritionixResponse
import com.fitnessapp.core.network.model.usda.UsdaResponse
import com.fitnessapp.core.network.model.upc.UpcResponse
import com.fitnessapp.core.network.model.ai.FoodRecognitionResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.coEvery
import io.mockk.mockk
import javax.inject.Singleton

/**
 * Mock network module for testing
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [com.fitnessapp.core.network.di.NetworkModule::class]
)
object MockNetworkModule {
    
    @Provides
    @Singleton
    fun provideMockExerciseApi(): ExerciseApi {
        return mockk<ExerciseApi>().apply {
            coEvery { searchExercises(any()) } returns ExerciseResponse(
                exercises = listOf(
                    ExerciseResponse.Exercise(
                        id = "1",
                        name = "Push Up",
                        category = "chest",
                        instructions = "Perform push ups",
                        equipment = "bodyweight",
                        muscles = listOf("chest", "triceps", "shoulders")
                    )
                )
            )
            
            coEvery { getExerciseById(any()) } returns ExerciseResponse.Exercise(
                id = "1",
                name = "Push Up", 
                category = "chest",
                instructions = "Perform push ups",
                equipment = "bodyweight",
                muscles = listOf("chest", "triceps", "shoulders")
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideMockNutritionixApi(): NutritionixApi {
        return mockk<NutritionixApi>().apply {
            coEvery { searchFoods(any()) } returns NutritionixResponse(
                foods = listOf(
                    NutritionixResponse.Food(
                        foodName = "chicken breast",
                        calories = 165.0,
                        protein = 31.0,
                        carbs = 0.0,
                        fat = 3.6,
                        servingWeightGrams = 100.0
                    )
                )
            )
            
            coEvery { getFoodDetails(any()) } returns NutritionixResponse.Food(
                foodName = "chicken breast",
                calories = 165.0,
                protein = 31.0,
                carbs = 0.0,
                fat = 3.6,
                servingWeightGrams = 100.0
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideMockFatSecretApi(): FatSecretApi {
        return mockk<FatSecretApi>().apply {
            coEvery { searchFoods(any()) } returns FatSecretResponse(
                foods = FatSecretResponse.Foods(
                    food = listOf(
                        FatSecretResponse.Food(
                            foodId = "12345",
                            foodName = "Banana",
                            brandName = "Generic",
                            foodDescription = "Banana, raw"
                        )
                    )
                )
            )
            
            coEvery { getFoodById(any()) } returns FatSecretResponse.Food(
                foodId = "12345",
                foodName = "Banana",
                brandName = "Generic",
                foodDescription = "Banana, raw"
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideMockUsdaApi(): UsdaFoodDataApi {
        return mockk<UsdaFoodDataApi>().apply {
            coEvery { searchFoods(any()) } returns UsdaResponse(
                foods = listOf(
                    UsdaResponse.Food(
                        fdcId = 123456,
                        description = "Broccoli, raw",
                        brandOwner = null,
                        ingredients = null,
                        foodNutrients = listOf(
                            UsdaResponse.FoodNutrient(
                                nutrientId = 1008,
                                nutrientName = "Energy",
                                value = 25.0,
                                unitName = "kcal"
                            )
                        )
                    )
                )
            )
            
            coEvery { getFoodById(any()) } returns UsdaResponse.Food(
                fdcId = 123456,
                description = "Broccoli, raw",
                brandOwner = null,
                ingredients = null,
                foodNutrients = listOf(
                    UsdaResponse.FoodNutrient(
                        nutrientId = 1008,
                        nutrientName = "Energy",
                        value = 25.0,
                        unitName = "kcal"
                    )
                )
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideMockUpcApi(): UpcDatabaseApi {
        return mockk<UpcDatabaseApi>().apply {
            coEvery { getProductByBarcode(any()) } returns UpcResponse(
                title = "Test Product",
                brand = "Test Brand",
                ingredients = "Test ingredients",
                size = "100g"
            )
        }
    }
    
    @Provides
    @Singleton
    fun provideMockFoodRecognitionApi(): FoodRecognitionApi {
        return mockk<FoodRecognitionApi>().apply {
            coEvery { recognizeFood(any()) } returns FoodRecognitionResponse(
                predictions = listOf(
                    FoodRecognitionResponse.Prediction(
                        className = "apple",
                        confidence = 0.95,
                        boundingBox = FoodRecognitionResponse.BoundingBox(
                            x = 100, y = 100, width = 200, height = 200
                        )
                    )
                )
            )
        }
    }
}