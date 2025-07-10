package com.fitnessapp.core.network.api

import com.fitnessapp.core.network.model.exercise.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Exercise Database API interface
 * Provides comprehensive exercise database with instructions and media
 */
interface ExerciseApi {

    @GET("exercises")
    suspend fun getAllExercises(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<Exercise>>

    @GET("exercises/{id}")
    suspend fun getExerciseById(
        @Path("id") exerciseId: String,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<Exercise>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<Exercise>>

    @GET("exercises/target/{target}")
    suspend fun getExercisesByTarget(
        @Path("target") target: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<Exercise>>

    @GET("exercises/equipment/{equipment}")
    suspend fun getExercisesByEquipment(
        @Path("equipment") equipment: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<Exercise>>

    @GET("exercises/name/{name}")
    suspend fun searchExercisesByName(
        @Path("name") name: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<Exercise>>

    @GET("exercises/bodyPartList")
    suspend fun getBodyPartsList(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<String>>

    @GET("exercises/targetList")
    suspend fun getTargetsList(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<String>>

    @GET("exercises/equipmentList")
    suspend fun getEquipmentList(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "exercisedb.p.rapidapi.com"
    ): Response<List<String>>
}