package com.fitnessapp.core.network.model.fatsecret

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FatSecretFoodSearchResponse(
    @SerialName("foods") val foods: FatSecretFoodsResult? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretFoodsResult(
    @SerialName("food") val food: List<FatSecretFood>? = null,
    @SerialName("max_results") val maxResults: String,
    @SerialName("page_number") val pageNumber: String,
    @SerialName("total_results") val totalResults: String
)

@Serializable
data class FatSecretFood(
    @SerialName("food_id") val foodId: String,
    @SerialName("food_name") val foodName: String,
    @SerialName("food_type") val foodType: String,
    @SerialName("food_url") val foodUrl: String? = null,
    @SerialName("brand_name") val brandName: String? = null,
    @SerialName("food_description") val foodDescription: String? = null
)

@Serializable
data class FatSecretFoodDetails(
    @SerialName("food") val food: FatSecretDetailedFood? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretDetailedFood(
    @SerialName("food_id") val foodId: String,
    @SerialName("food_name") val foodName: String,
    @SerialName("food_type") val foodType: String,
    @SerialName("food_url") val foodUrl: String? = null,
    @SerialName("brand_name") val brandName: String? = null,
    @SerialName("servings") val servings: FatSecretServings
)

@Serializable
data class FatSecretServings(
    @SerialName("serving") val serving: List<FatSecretServing>
)

@Serializable
data class FatSecretServing(
    @SerialName("serving_id") val servingId: String,
    @SerialName("serving_description") val servingDescription: String,
    @SerialName("serving_url") val servingUrl: String? = null,
    @SerialName("metric_serving_amount") val metricServingAmount: String? = null,
    @SerialName("metric_serving_unit") val metricServingUnit: String? = null,
    @SerialName("number_of_units") val numberOfUnits: String? = null,
    @SerialName("measurement_description") val measurementDescription: String? = null,
    @SerialName("calories") val calories: String? = null,
    @SerialName("carbohydrate") val carbohydrate: String? = null,
    @SerialName("protein") val protein: String? = null,
    @SerialName("fat") val fat: String? = null,
    @SerialName("saturated_fat") val saturatedFat: String? = null,
    @SerialName("polyunsaturated_fat") val polyunsaturatedFat: String? = null,
    @SerialName("monounsaturated_fat") val monounsaturatedFat: String? = null,
    @SerialName("cholesterol") val cholesterol: String? = null,
    @SerialName("sodium") val sodium: String? = null,
    @SerialName("potassium") val potassium: String? = null,
    @SerialName("fiber") val fiber: String? = null,
    @SerialName("sugar") val sugar: String? = null,
    @SerialName("vitamin_a") val vitaminA: String? = null,
    @SerialName("vitamin_c") val vitaminC: String? = null,
    @SerialName("calcium") val calcium: String? = null,
    @SerialName("iron") val iron: String? = null
)

@Serializable
data class FatSecretBarcodeResponse(
    @SerialName("food_id") val foodId: FatSecretBarcodeResult? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretBarcodeResult(
    @SerialName("value") val value: String
)

@Serializable
data class FatSecretRecipeSearchResponse(
    @SerialName("recipes") val recipes: FatSecretRecipesResult? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretRecipesResult(
    @SerialName("recipe") val recipe: List<FatSecretRecipe>? = null,
    @SerialName("max_results") val maxResults: String,
    @SerialName("page_number") val pageNumber: String,
    @SerialName("total_results") val totalResults: String
)

@Serializable
data class FatSecretRecipe(
    @SerialName("recipe_id") val recipeId: String,
    @SerialName("recipe_name") val recipeName: String,
    @SerialName("recipe_description") val recipeDescription: String? = null,
    @SerialName("recipe_url") val recipeUrl: String? = null,
    @SerialName("recipe_image") val recipeImage: String? = null
)

@Serializable
data class FatSecretRecipeDetails(
    @SerialName("recipe") val recipe: FatSecretDetailedRecipe? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretDetailedRecipe(
    @SerialName("recipe_id") val recipeId: String,
    @SerialName("recipe_name") val recipeName: String,
    @SerialName("recipe_description") val recipeDescription: String? = null,
    @SerialName("recipe_url") val recipeUrl: String? = null,
    @SerialName("recipe_image") val recipeImage: String? = null,
    @SerialName("preparation_time_min") val preparationTimeMin: String? = null,
    @SerialName("cooking_time_min") val cookingTimeMin: String? = null,
    @SerialName("number_of_servings") val numberOfServings: String? = null,
    @SerialName("ingredients") val ingredients: FatSecretIngredients? = null,
    @SerialName("directions") val directions: FatSecretDirections? = null,
    @SerialName("servings") val servings: FatSecretServings? = null
)

@Serializable
data class FatSecretIngredients(
    @SerialName("ingredient") val ingredient: List<FatSecretIngredient>
)

@Serializable
data class FatSecretIngredient(
    @SerialName("ingredient_description") val ingredientDescription: String,
    @SerialName("ingredient_url") val ingredientUrl: String? = null,
    @SerialName("food_id") val foodId: String? = null
)

@Serializable
data class FatSecretDirections(
    @SerialName("direction") val direction: List<FatSecretDirection>
)

@Serializable
data class FatSecretDirection(
    @SerialName("direction_number") val directionNumber: String,
    @SerialName("direction_description") val directionDescription: String
)

@Serializable
data class FatSecretAutocompleteResponse(
    @SerialName("suggestions") val suggestions: FatSecretSuggestions? = null,
    @SerialName("error") val error: FatSecretError? = null
)

@Serializable
data class FatSecretSuggestions(
    @SerialName("suggestion") val suggestion: List<String>
)

@Serializable
data class FatSecretError(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String
)