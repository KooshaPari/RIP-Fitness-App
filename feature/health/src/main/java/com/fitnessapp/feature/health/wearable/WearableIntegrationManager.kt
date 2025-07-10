package com.fitnessapp.feature.health.wearable

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wearable device integration manager for Fitbit, Garmin, and other devices
 * Handles OAuth authentication and data synchronization via APIs
 */
@Singleton
class WearableIntegrationManager @Inject constructor() {
    
    companion object {
        private const val TAG = "WearableIntegrationManager"
        
        // API Base URLs
        private const val FITBIT_API_BASE = "https://api.fitbit.com/1/user/-/"
        private const val GARMIN_API_BASE = "https://connectapi.garmin.com/wellness-api/rest/"
    }
    
    // Fitbit API Service
    interface FitbitApiService {
        @GET("profile.json")
        suspend fun getProfile(@Header("Authorization") auth: String): FitbitProfileResponse
        
        @GET("body/log/weight/date/{date}.json")
        suspend fun getWeight(
            @Header("Authorization") auth: String,
            @Path("date") date: String
        ): FitbitWeightResponse
        
        @GET("activities/steps/date/{date}/1d.json")
        suspend fun getSteps(
            @Header("Authorization") auth: String,
            @Path("date") date: String
        ): FitbitStepsResponse
        
        @GET("activities/heart/date/{date}/1d.json")
        suspend fun getHeartRate(
            @Header("Authorization") auth: String,
            @Path("date") date: String
        ): FitbitHeartRateResponse
        
        @GET("sleep/date/{date}.json")
        suspend fun getSleep(
            @Header("Authorization") auth: String,
            @Path("date") date: String
        ): FitbitSleepResponse
        
        @POST("body/log/weight.json")
        suspend fun logWeight(
            @Header("Authorization") auth: String,
            @Field("weight") weight: Float,
            @Field("date") date: String
        ): FitbitLogResponse
    }
    
    // Garmin API Service
    interface GarminApiService {
        @GET("user")
        suspend fun getUser(@Header("Authorization") auth: String): GarminUserResponse
        
        @GET("activities/{startDate}/{endDate}")
        suspend fun getActivities(
            @Header("Authorization") auth: String,
            @Path("startDate") startDate: String,
            @Path("endDate") endDate: String
        ): GarminActivitiesResponse
        
        @GET("dailySummary/{startDate}/{endDate}")
        suspend fun getDailySummary(
            @Header("Authorization") auth: String,
            @Path("startDate") startDate: String,
            @Path("endDate") endDate: String
        ): GarminDailySummaryResponse
        
        @GET("sleep/{startDate}/{endDate}")
        suspend fun getSleep(
            @Header("Authorization") auth: String,
            @Path("startDate") startDate: String,
            @Path("endDate") endDate: String
        ): GarminSleepResponse
    }
    
    private val fitbitApi: FitbitApiService by lazy {
        Retrofit.Builder()
            .baseUrl(FITBIT_API_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FitbitApiService::class.java)
    }
    
    private val garminApi: GarminApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GARMIN_API_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GarminApiService::class.java)
    }
    
    // Fitbit Integration
    suspend fun syncFitbitData(accessToken: String, date: String): Result<WearableData> {
        return try {
            val auth = "Bearer $accessToken"
            
            // Parallel API calls for efficiency
            val profile = fitbitApi.getProfile(auth)
            val weight = runCatching { fitbitApi.getWeight(auth, date) }.getOrNull()
            val steps = runCatching { fitbitApi.getSteps(auth, date) }.getOrNull()
            val heartRate = runCatching { fitbitApi.getHeartRate(auth, date) }.getOrNull()
            val sleep = runCatching { fitbitApi.getSleep(auth, date) }.getOrNull()
            
            val wearableData = WearableData(
                deviceType = WearableDeviceType.FITBIT,
                userId = profile.user.encodedId,
                timestamp = Instant.now(),
                weight = weight?.weight?.firstOrNull()?.weight,
                steps = steps?.activities_steps?.firstOrNull()?.value?.toIntOrNull(),
                heartRate = heartRate?.activities_heart?.firstOrNull()?.value?.restingHeartRate,
                sleepMinutes = sleep?.sleep?.sumOf { it.minutesAsleep },
                caloriesBurned = heartRate?.activities_heart?.firstOrNull()?.value?.caloriesOut?.toFloatOrNull(),
                distanceMeters = steps?.activities_steps?.firstOrNull()?.value?.toFloatOrNull()?.times(0.762f) // Approximate conversion
            )
            
            Result.success(wearableData)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing Fitbit data", e)
            Result.failure(e)
        }
    }
    
    suspend fun logWeightToFitbit(accessToken: String, weight: Float, date: String): Result<Unit> {
        return try {
            val auth = "Bearer $accessToken"
            fitbitApi.logWeight(auth, weight, date)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error logging weight to Fitbit", e)
            Result.failure(e)
        }
    }
    
    // Garmin Integration
    suspend fun syncGarminData(
        accessToken: String,
        startDate: String,
        endDate: String
    ): Result<WearableData> {
        return try {
            val auth = "Bearer $accessToken"
            
            // Parallel API calls
            val user = garminApi.getUser(auth)
            val activities = runCatching { garminApi.getActivities(auth, startDate, endDate) }.getOrNull()
            val dailySummary = runCatching { garminApi.getDailySummary(auth, startDate, endDate) }.getOrNull()
            val sleep = runCatching { garminApi.getSleep(auth, startDate, endDate) }.getOrNull()
            
            val latestSummary = dailySummary?.dailySummaries?.maxByOrNull { it.calendarDate }
            val latestActivity = activities?.activities?.maxByOrNull { it.startTimeGMT }
            val latestSleep = sleep?.sleepSummaries?.maxByOrNull { it.calendarDate }
            
            val wearableData = WearableData(
                deviceType = WearableDeviceType.GARMIN,
                userId = user.userId,
                timestamp = Instant.now(),
                weight = null, // Garmin doesn't typically provide weight via API
                steps = latestSummary?.totalSteps,
                heartRate = latestSummary?.averageHeartRateInBeatsPerMinute,
                sleepMinutes = latestSleep?.sleepTimeInSeconds?.div(60),
                caloriesBurned = latestSummary?.totalKilocalories?.toFloat(),
                distanceMeters = latestSummary?.totalDistanceInMeters?.toFloat()
            )
            
            Result.success(wearableData)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing Garmin data", e)
            Result.failure(e)
        }
    }
    
    // Generic wearable data sync flow
    fun syncWearableDataFlow(
        deviceType: WearableDeviceType,
        accessToken: String,
        dateRange: Pair<String, String> = Pair("", "")
    ): Flow<Result<WearableData>> = flow {
        when (deviceType) {
            WearableDeviceType.FITBIT -> {
                val result = syncFitbitData(accessToken, dateRange.first.ifEmpty { getCurrentDate() })
                emit(result)
            }
            WearableDeviceType.GARMIN -> {
                val result = syncGarminData(
                    accessToken,
                    dateRange.first.ifEmpty { getCurrentDate() },
                    dateRange.second.ifEmpty { getCurrentDate() }
                )
                emit(result)
            }
            WearableDeviceType.APPLE_WATCH -> {
                // Apple Watch data typically comes through HealthKit, not API
                emit(Result.failure(Exception("Apple Watch uses HealthKit integration")))
            }
            WearableDeviceType.WEAR_OS -> {
                // Wear OS data typically comes through Google Fit
                emit(Result.failure(Exception("Wear OS uses Google Fit integration")))
            }
        }
    }
    
    // OAuth helper methods
    fun getFitbitAuthUrl(clientId: String, redirectUri: String, scope: String): String {
        return "https://www.fitbit.com/oauth2/authorize?" +
                "response_type=code&" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "scope=$scope&" +
                "expires_in=604800"
    }
    
    fun getGarminAuthUrl(clientId: String, redirectUri: String): String {
        return "https://connect.garmin.com/oauthConfirm?" +
                "oauth_callback=$redirectUri&" +
                "oauth_consumer_key=$clientId"
    }
    
    private fun getCurrentDate(): String {
        return java.time.LocalDate.now().toString()
    }
}

// Data classes for API responses
data class WearableData(
    val deviceType: WearableDeviceType,
    val userId: String,
    val timestamp: Instant,
    val weight: Float? = null,
    val steps: Int? = null,
    val heartRate: Int? = null,
    val sleepMinutes: Int? = null,
    val caloriesBurned: Float? = null,
    val distanceMeters: Float? = null
)

enum class WearableDeviceType {
    FITBIT,
    GARMIN,
    APPLE_WATCH,
    WEAR_OS
}

// Fitbit response models
data class FitbitProfileResponse(val user: FitbitUser)
data class FitbitUser(val encodedId: String)

data class FitbitWeightResponse(val weight: List<FitbitWeightEntry>)
data class FitbitWeightEntry(val weight: Float, val date: String, val time: String)

data class FitbitStepsResponse(val activities_steps: List<FitbitStepsEntry>)
data class FitbitStepsEntry(val value: String, val dateTime: String)

data class FitbitHeartRateResponse(val activities_heart: List<FitbitHeartRateEntry>)
data class FitbitHeartRateEntry(val value: FitbitHeartRateValue)
data class FitbitHeartRateValue(val restingHeartRate: Int, val caloriesOut: String)

data class FitbitSleepResponse(val sleep: List<FitbitSleepEntry>)
data class FitbitSleepEntry(val minutesAsleep: Int, val startTime: String, val endTime: String)

data class FitbitLogResponse(val weightLog: FitbitWeightLogEntry)
data class FitbitWeightLogEntry(val logId: Long, val weight: Float)

// Garmin response models
data class GarminUserResponse(val userId: String)

data class GarminActivitiesResponse(val activities: List<GarminActivity>)
data class GarminActivity(
    val activityId: String,
    val startTimeGMT: String,
    val activityType: String,
    val duration: Int,
    val distance: Float,
    val calories: Float
)

data class GarminDailySummaryResponse(val dailySummaries: List<GarminDailySummary>)
data class GarminDailySummary(
    val calendarDate: String,
    val totalSteps: Int,
    val totalDistanceInMeters: Int,
    val totalKilocalories: Int,
    val averageHeartRateInBeatsPerMinute: Int
)

data class GarminSleepResponse(val sleepSummaries: List<GarminSleepSummary>)
data class GarminSleepSummary(
    val calendarDate: String,
    val sleepTimeInSeconds: Int,
    val deepSleepTimeInSeconds: Int,
    val lightSleepTimeInSeconds: Int,
    val remSleepTimeInSeconds: Int
)