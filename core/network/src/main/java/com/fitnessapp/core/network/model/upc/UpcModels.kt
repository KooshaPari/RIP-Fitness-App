package com.fitnessapp.core.network.model.upc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpcLookupResponse(
    @SerialName("code") val code: String,
    @SerialName("total") val total: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("items") val items: List<UpcItem>
)

@Serializable
data class UpcItem(
    @SerialName("ean") val ean: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("upc") val upc: String,
    @SerialName("gtin") val gtin: String? = null,
    @SerialName("elid") val elid: String? = null,
    @SerialName("brand") val brand: String? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("color") val color: String? = null,
    @SerialName("size") val size: String? = null,
    @SerialName("dimension") val dimension: String? = null,
    @SerialName("weight") val weight: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("lowest_recorded_price") val lowestRecordedPrice: Double? = null,
    @SerialName("highest_recorded_price") val highestRecordedPrice: Double? = null,
    @SerialName("images") val images: List<String>? = null,
    @SerialName("offers") val offers: List<UpcOffer>? = null,
    @SerialName("user_data") val userData: UpcUserData? = null
)

@Serializable
data class UpcOffer(
    @SerialName("merchant") val merchant: String,
    @SerialName("domain") val domain: String,
    @SerialName("title") val title: String,
    @SerialName("currency") val currency: String? = null,
    @SerialName("list_price") val listPrice: String? = null,
    @SerialName("price") val price: Double? = null,
    @SerialName("shipping") val shipping: String? = null,
    @SerialName("condition") val condition: String? = null,
    @SerialName("availability") val availability: String? = null,
    @SerialName("link") val link: String,
    @SerialName("updated_t") val updatedT: Long
)

@Serializable
data class UpcUserData(
    @SerialName("nutrition_facts") val nutritionFacts: UpcNutritionFacts? = null,
    @SerialName("ingredients") val ingredients: String? = null,
    @SerialName("allergens") val allergens: List<String>? = null,
    @SerialName("serving_size") val servingSize: String? = null,
    @SerialName("net_weight") val netWeight: String? = null
)

@Serializable
data class UpcNutritionFacts(
    @SerialName("calories") val calories: String? = null,
    @SerialName("total_fat") val totalFat: String? = null,
    @SerialName("saturated_fat") val saturatedFat: String? = null,
    @SerialName("trans_fat") val transFat: String? = null,
    @SerialName("cholesterol") val cholesterol: String? = null,
    @SerialName("sodium") val sodium: String? = null,
    @SerialName("total_carbohydrate") val totalCarbohydrate: String? = null,
    @SerialName("dietary_fiber") val dietaryFiber: String? = null,
    @SerialName("total_sugars") val totalSugars: String? = null,
    @SerialName("added_sugars") val addedSugars: String? = null,
    @SerialName("protein") val protein: String? = null,
    @SerialName("vitamin_d") val vitaminD: String? = null,
    @SerialName("calcium") val calcium: String? = null,
    @SerialName("iron") val iron: String? = null,
    @SerialName("potassium") val potassium: String? = null
)

@Serializable
data class UpcSearchResponse(
    @SerialName("code") val code: String,
    @SerialName("total") val total: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("items") val items: List<UpcItem>
)

@Serializable
data class UpcCategoryResponse(
    @SerialName("code") val code: String,
    @SerialName("total") val total: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("items") val items: List<UpcItem>
)

@Serializable
data class UpcCategoriesResponse(
    @SerialName("code") val code: String,
    @SerialName("categories") val categories: List<UpcCategory>
)

@Serializable
data class UpcCategory(
    @SerialName("id") val id: String,
    @SerialName("parent") val parent: String? = null,
    @SerialName("name") val name: String,
    @SerialName("level") val level: Int
)