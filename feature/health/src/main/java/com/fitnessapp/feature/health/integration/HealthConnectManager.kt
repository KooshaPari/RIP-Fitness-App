package com.fitnessapp.feature.health.integration

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.metadata.Metadata
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Velocity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Primary Health Connect integration manager (2025 standard)
 * Handles all Health Connect SDK interactions with comprehensive error handling
 */
@Singleton
class HealthConnectManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getWritePermission(WeightRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(NutritionRecord::class),
            HealthPermission.getReadPermission(NutritionRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getWritePermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
        )
    }
    
    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }
    
    suspend fun isAvailable(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun hasAllPermissions(): Boolean {
        return try {
            val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
            REQUIRED_PERMISSIONS.all { it in grantedPermissions }
        } catch (e: Exception) {
            false
        }
    }
    
    fun getPermissionsFlow(): Flow<Set<String>> = flow {
        try {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            emit(granted)
        } catch (e: Exception) {
            emit(emptySet())
        }
    }
    
    // Weight Management
    suspend fun insertWeight(
        weightKg: Double,
        time: Instant = Instant.now(),
        metadata: Metadata = Metadata()
    ): Result<Unit> {
        return try {
            val weightRecord = WeightRecord(
                weight = Mass.kilograms(weightKg),
                time = time,
                zoneOffset = ZoneOffset.systemDefault().rules.getOffset(time),
                metadata = metadata
            )
            healthConnectClient.insertRecords(listOf(weightRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLatestWeight(): Result<WeightRecord?> {
        return try {
            val request = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.none(),
                ascendingOrder = false
            )
            val response = healthConnectClient.readRecords(request)
            Result.success(response.records.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeightHistory(
        startTime: Instant,
        endTime: Instant
    ): Result<List<WeightRecord>> {
        return try {
            val request = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            Result.success(response.records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Exercise Session Management
    suspend fun insertExerciseSession(
        exerciseType: Int,
        startTime: Instant,
        endTime: Instant,
        title: String? = null,
        notes: String? = null
    ): Result<Unit> {
        return try {
            val session = ExerciseSessionRecord(
                exerciseType = exerciseType,
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                title = title,
                notes = notes
            )
            healthConnectClient.insertRecords(listOf(session))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getExerciseSessions(
        startTime: Instant,
        endTime: Instant
    ): Result<List<ExerciseSessionRecord>> {
        return try {
            val request = ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.readRecords(request)
            Result.success(response.records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Heart Rate Management
    suspend fun insertHeartRate(
        beatsPerMinute: Long,
        time: Instant = Instant.now()
    ): Result<Unit> {
        return try {
            val heartRateRecord = HeartRateRecord(
                time = time,
                zoneOffset = ZoneOffset.systemDefault().rules.getOffset(time),
                beatsPerMinute = beatsPerMinute
            )
            healthConnectClient.insertRecords(listOf(heartRateRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Steps Management
    suspend fun insertSteps(
        count: Long,
        startTime: Instant,
        endTime: Instant
    ): Result<Unit> {
        return try {
            val stepsRecord = StepsRecord(
                count = count,
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime)
            )
            healthConnectClient.insertRecords(listOf(stepsRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAggregatedSteps(
        startTime: Instant,
        endTime: Instant
    ): Result<Long> {
        return try {
            val request = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            val response = healthConnectClient.aggregate(request)
            val totalSteps = response[StepsRecord.COUNT_TOTAL] ?: 0L
            Result.success(totalSteps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Nutrition Management
    suspend fun insertNutrition(
        totalEnergy: Energy?,
        protein: Mass? = null,
        carbs: Mass? = null,
        fat: Mass? = null,
        time: Instant = Instant.now(),
        mealType: Int = NutritionRecord.MEAL_TYPE_UNKNOWN
    ): Result<Unit> {
        return try {
            val nutritionRecord = NutritionRecord(
                time = time,
                zoneOffset = ZoneOffset.systemDefault().rules.getOffset(time),
                totalEnergy = totalEnergy,
                protein = protein,
                totalCarbohydrate = carbs,
                totalFat = fat,
                mealType = mealType
            )
            healthConnectClient.insertRecords(listOf(nutritionRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sleep Management
    suspend fun insertSleepSession(
        startTime: Instant,
        endTime: Instant,
        title: String? = null,
        notes: String? = null
    ): Result<Unit> {
        return try {
            val sleepSession = SleepSessionRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                title = title,
                notes = notes
            )
            healthConnectClient.insertRecords(listOf(sleepSession))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Calories Management
    suspend fun insertActiveCalories(
        calories: Energy,
        startTime: Instant,
        endTime: Instant
    ): Result<Unit> {
        return try {
            val caloriesRecord = ActiveCaloriesBurnedRecord(
                energy = calories,
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime)
            )
            healthConnectClient.insertRecords(listOf(caloriesRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Distance Management
    suspend fun insertDistance(
        distance: Length,
        startTime: Instant,
        endTime: Instant
    ): Result<Unit> {
        return try {
            val distanceRecord = DistanceRecord(
                distance = distance,
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime)
            )
            healthConnectClient.insertRecords(listOf(distanceRecord))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Data Cleanup
    suspend fun deleteRecordsByTimeRange(
        recordType: kotlin.reflect.KClass<out Record>,
        startTime: Instant,
        endTime: Instant
    ): Result<Unit> {
        return try {
            healthConnectClient.deleteRecords(
                recordType = recordType,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}