package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.upc.*
import retrofit2.Response
import retrofit2.http.*

/**
 * UPC Database API interface
 * Provides barcode lookup for food products
 */
interface UpcDatabaseApi {

    @GET("lookup")
    suspend fun lookupBarcode(
        @Query("upc") upc: String,
        @Header("Authorization") authorization: String
    ): Response<UpcLookupResponse>

    @GET("search")
    suspend fun searchProducts(
        @Query("s") searchTerm: String,
        @Query("match") match: Int = 0, // 0 = partial match, 1 = exact match
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): Response<UpcSearchResponse>

    @GET("category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String,
        @Query("offset") offset: Int = 0,
        @Header("Authorization") authorization: String
    ): Response<UpcCategoryResponse>

    @GET("categories")
    suspend fun getCategories(
        @Header("Authorization") authorization: String
    ): Response<UpcCategoriesResponse>
}