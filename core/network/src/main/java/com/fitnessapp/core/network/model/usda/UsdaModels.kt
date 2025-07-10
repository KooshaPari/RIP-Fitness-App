package com.fitnessapp.core.network.model.usda

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsdaFoodSearchResponse(
    @SerialName("totalHits") val totalHits: Int,
    @SerialName("currentPage") val currentPage: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("foods") val foods: List<UsdaFoodSearchResult>
)

@Serializable
data class UsdaFoodSearchResult(
    @SerialName("fdcId") val fdcId: Int,
    @SerialName("description") val description: String,
    @SerialName("dataType") val dataType: String,
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("gtinUpc") val gtinUpc: String? = null,
    @SerialName("publishedDate") val publishedDate: String? = null,
    @SerialName("ingredients") val ingredients: String? = null,
    @SerialName("foodNutrients") val foodNutrients: List<UsdaFoodNutrient>? = null
)

@Serializable
data class UsdaFoodDetails(
    @SerialName("fdcId") val fdcId: Int,
    @SerialName("description") val description: String,
    @SerialName("dataType") val dataType: String,
    @SerialName("foodClass") val foodClass: String? = null,
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("gtinUpc") val gtinUpc: String? = null,
    @SerialName("ingredients") val ingredients: String? = null,
    @SerialName("servingSize") val servingSize: Double? = null,
    @SerialName("servingSizeUnit") val servingSizeUnit: String? = null,
    @SerialName("householdServingFullText") val householdServingFullText: String? = null,
    @SerialName("foodNutrients") val foodNutrients: List<UsdaFoodNutrient>,
    @SerialName("foodPortions") val foodPortions: List<UsdaFoodPortion>? = null,
    @SerialName("foodAttributes") val foodAttributes: List<UsdaFoodAttribute>? = null
)

@Serializable
data class UsdaFoodNutrient(
    @SerialName("nutrientId") val nutrientId: Int,
    @SerialName("nutrientName") val nutrientName: String,
    @SerialName("nutrientNumber") val nutrientNumber: String? = null,
    @SerialName("unitName") val unitName: String,
    @SerialName("derivationCode") val derivationCode: String? = null,
    @SerialName("derivationDescription") val derivationDescription: String? = null,
    @SerialName("value") val value: Double? = null,
    @SerialName("foodNutrientSourceId") val foodNutrientSourceId: Int? = null,
    @SerialName("foodNutrientSourceCode") val foodNutrientSourceCode: String? = null,
    @SerialName("foodNutrientSourceDescription") val foodNutrientSourceDescription: String? = null,
    @SerialName("rank") val rank: Int? = null,
    @SerialName("indentLevel") val indentLevel: Int? = null,
    @SerialName("foodNutrientId") val foodNutrientId: Int? = null
)

@Serializable
data class UsdaFoodPortion(
    @SerialName("id") val id: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("measureUnit") val measureUnit: UsdaMeasureUnit,
    @SerialName("modifier") val modifier: String? = null,
    @SerialName("gramWeight") val gramWeight: Double,
    @SerialName("sequenceNumber") val sequenceNumber: Int? = null
)

@Serializable
data class UsdaMeasureUnit(
    @SerialName("id") val id: Int,
    @SerialName("abbreviation") val abbreviation: String,
    @SerialName("name") val name: String
)

@Serializable
data class UsdaFoodAttribute(
    @SerialName("id") val id: Int,
    @SerialName("value") val value: String,
    @SerialName("name") val name: String? = null
)

@Serializable
data class UsdaNutrient(
    @SerialName("id") val id: Int,
    @SerialName("number") val number: String,
    @SerialName("name") val name: String,
    @SerialName("rank") val rank: Int,
    @SerialName("unitName") val unitName: String
)

@Serializable
data class UsdaFoodsByIdsRequest(
    @SerialName("fdcIds") val fdcIds: List<String>,
    @SerialName("format") val format: String = "abridged",
    @SerialName("nutrients") val nutrients: List<Int>? = null
)

@Serializable
data class UsdaFoodsListResponse(
    @SerialName("currentPage") val currentPage: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("foods") val foods: List<UsdaFoodSearchResult>
)