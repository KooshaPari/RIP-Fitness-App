package com.fitnessapp.core.network.model.nutritionix

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NutritionixNaturalRequest(
    @SerialName("query") val query: String,
    @SerialName("num_servings") val numServings: Int? = null,
    @SerialName("aggregate") val aggregate: String? = null,
    @SerialName("line_delimited") val lineDelimited: Boolean = false,
    @SerialName("use_raw_foods") val useRawFoods: Boolean = false,
    @SerialName("include_subrecipe") val includeSubrecipe: Boolean = false,
    @SerialName("timezone") val timezone: String = "US/Eastern",
    @SerialName("consumed_at") val consumedAt: String? = null,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lng") val lng: Double? = null,
    @SerialName("meal_type") val mealType: Int? = null,
    @SerialName("use_branded_foods") val useBrandedFoods: Boolean = false,
    @SerialName("locale") val locale: String = "en_US"
)

@Serializable
data class NutritionixNaturalResponse(
    @SerialName("foods") val foods: List<NutritionixFood>
)

@Serializable
data class NutritionixFood(
    @SerialName("food_name") val foodName: String,
    @SerialName("brand_name") val brandName: String? = null,
    @SerialName("serving_qty") val servingQty: Double,
    @SerialName("serving_unit") val servingUnit: String,
    @SerialName("serving_weight_grams") val servingWeightGrams: Double? = null,
    @SerialName("nf_calories") val nfCalories: Double? = null,
    @SerialName("nf_total_fat") val nfTotalFat: Double? = null,
    @SerialName("nf_saturated_fat") val nfSaturatedFat: Double? = null,
    @SerialName("nf_cholesterol") val nfCholesterol: Double? = null,
    @SerialName("nf_sodium") val nfSodium: Double? = null,
    @SerialName("nf_total_carbohydrate") val nfTotalCarbohydrate: Double? = null,
    @SerialName("nf_dietary_fiber") val nfDietaryFiber: Double? = null,
    @SerialName("nf_sugars") val nfSugars: Double? = null,
    @SerialName("nf_protein") val nfProtein: Double? = null,
    @SerialName("nf_potassium") val nfPotassium: Double? = null,
    @SerialName("nf_p") val nfPhosphorus: Double? = null,
    @SerialName("full_nutrients") val fullNutrients: List<NutritionixNutrient>? = null,
    @SerialName("nix_brand_name") val nixBrandName: String? = null,
    @SerialName("nix_brand_id") val nixBrandId: String? = null,
    @SerialName("nix_item_name") val nixItemName: String? = null,
    @SerialName("nix_item_id") val nixItemId: String? = null,
    @SerialName("upc") val upc: String? = null,
    @SerialName("consumed_at") val consumedAt: String? = null,
    @SerialName("metadata") val metadata: NutritionixMetadata? = null,
    @SerialName("source") val source: Int? = null,
    @SerialName("ndb_no") val ndbNo: Int? = null,
    @SerialName("tags") val tags: NutritionixTags? = null,
    @SerialName("alt_measures") val altMeasures: List<NutritionixAltMeasure>? = null,
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lng") val lng: Double? = null,
    @SerialName("meal_type") val mealType: Int? = null,
    @SerialName("photo") val photo: NutritionixPhoto? = null
)

@Serializable
data class NutritionixNutrient(
    @SerialName("attr_id") val attrId: Int,
    @SerialName("value") val value: Double
)

@Serializable
data class NutritionixMetadata(
    @SerialName("is_raw_food") val isRawFood: Boolean? = null
)

@Serializable
data class NutritionixTags(
    @SerialName("item") val item: String? = null,
    @SerialName("measure") val measure: String? = null,
    @SerialName("quantity") val quantity: String? = null,
    @SerialName("food_group") val foodGroup: Int? = null,
    @SerialName("tag_id") val tagId: Int? = null
)

@Serializable
data class NutritionixAltMeasure(
    @SerialName("serving_weight") val servingWeight: Double,
    @SerialName("measure") val measure: String,
    @SerialName("seq") val seq: Int? = null,
    @SerialName("qty") val qty: Double
)

@Serializable
data class NutritionixPhoto(
    @SerialName("thumb") val thumb: String? = null,
    @SerialName("highres") val highres: String? = null,
    @SerialName("is_user_uploaded") val isUserUploaded: Boolean? = null
)

@Serializable
data class NutritionixInstantSearchResponse(
    @SerialName("common") val common: List<NutritionixCommonFood>,
    @SerialName("branded") val branded: List<NutritionixBrandedFood>
)

@Serializable
data class NutritionixCommonFood(
    @SerialName("food_name") val foodName: String,
    @SerialName("serving_unit") val servingUnit: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("serving_qty") val servingQty: Double,
    @SerialName("common_type") val commonType: Int? = null,
    @SerialName("tag_id") val tagId: String,
    @SerialName("photo") val photo: NutritionixPhoto,
    @SerialName("locale") val locale: String
)

@Serializable
data class NutritionixBrandedFood(
    @SerialName("food_name") val foodName: String,
    @SerialName("serving_unit") val servingUnit: String,
    @SerialName("nix_brand_id") val nixBrandId: String,
    @SerialName("brand_name_item_name") val brandNameItemName: String,
    @SerialName("serving_qty") val servingQty: Double,
    @SerialName("nf_calories") val nfCalories: Double,
    @SerialName("photo") val photo: NutritionixPhoto,
    @SerialName("brand_name") val brandName: String,
    @SerialName("region") val region: Int,
    @SerialName("brand_type") val brandType: Int,
    @SerialName("nix_item_id") val nixItemId: String,
    @SerialName("locale") val locale: String
)

@Serializable
data class NutritionixExerciseRequest(
    @SerialName("query") val query: String,
    @SerialName("gender") val gender: String? = null,
    @SerialName("weight_kg") val weightKg: Double? = null,
    @SerialName("height_cm") val heightCm: Double? = null,
    @SerialName("age") val age: Int? = null
)

@Serializable
data class NutritionixExerciseResponse(
    @SerialName("exercises") val exercises: List<NutritionixExercise>
)

@Serializable
data class NutritionixExercise(
    @SerialName("tag_id") val tagId: Int,
    @SerialName("user_input") val userInput: String,
    @SerialName("duration_min") val durationMin: Double,
    @SerialName("met") val met: Double,
    @SerialName("nf_calories") val nfCalories: Double,
    @SerialName("photo") val photo: NutritionixPhoto,
    @SerialName("compendium_code") val compendiumCode: Int? = null,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("benefits") val benefits: String? = null
)

@Serializable
data class NutritionixLocationsResponse(
    @SerialName("locations") val locations: List<NutritionixLocation>
)

@Serializable
data class NutritionixLocation(
    @SerialName("name") val name: String,
    @SerialName("brand_id") val brandId: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("address") val address: String,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("zip") val zip: String,
    @SerialName("country") val country: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("distance_km") val distanceKm: Double? = null
)

@Serializable
data class NutritionixPhotoRequest(
    @SerialName("media_uris") val mediaUris: List<String>
)

@Serializable
data class NutritionixPhotoResponse(
    @SerialName("foods") val foods: List<NutritionixFood>
)