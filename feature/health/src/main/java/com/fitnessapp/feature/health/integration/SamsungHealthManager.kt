package com.fitnessapp.feature.health.integration

import android.content.Context
import android.util.Log
import com.samsung.android.sdk.healthdata.*
import com.samsung.android.sdk.healthdata.HealthConstants.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Samsung Health SDK integration for direct Samsung device support
 * Provides native Samsung Health data access for enhanced accuracy
 */
@Singleton
class SamsungHealthManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "SamsungHealthManager"
        
        val REQUIRED_PERMISSIONS = setOf(
            HealthConstants.Weight.HEALTH_DATA_TYPE,
            HealthConstants.StepCount.HEALTH_DATA_TYPE,
            HealthConstants.Exercise.HEALTH_DATA_TYPE,
            HealthConstants.HeartRate.HEALTH_DATA_TYPE,
            HealthConstants.Sleep.HEALTH_DATA_TYPE,
            HealthConstants.Nutrition.HEALTH_DATA_TYPE,
            HealthConstants.WalkingSpeed.HEALTH_DATA_TYPE,
            HealthConstants.Distance.HEALTH_DATA_TYPE
        )
    }
    
    private var healthDataStore: HealthDataStore? = null
    private var isConnected = false
    
    private val connectionListener = object : HealthDataStore.ConnectionListener() {
        override fun onConnected() {
            Log.d(TAG, "Samsung Health connected")
            isConnected = true
        }
        
        override fun onConnectionFailed(error: HealthConnectionErrorResult) {
            Log.e(TAG, "Samsung Health connection failed: $error")
            isConnected = false
        }
        
        override fun onDisconnected() {
            Log.d(TAG, "Samsung Health disconnected")
            isConnected = false
        }
    }
    
    suspend fun initialize(): Result<Unit> {
        return try {
            val healthDataService = HealthDataService()
            healthDataService.initialize(context)
            
            if (!healthDataService.isValidPackage(context)) {
                return Result.failure(Exception("Samsung Health not available"))
            }
            
            healthDataStore = HealthDataStore(context, connectionListener)
            healthDataStore?.connectService()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Samsung Health", e)
            Result.failure(e)
        }
    }
    
    fun isAvailable(): Boolean {
        return try {
            val healthDataService = HealthDataService()
            healthDataService.isValidPackage(context)
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun hasPermissions(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            
            val permissionManager = HealthPermissionManager(store)
            
            try {
                permissionManager.isPermissionAcquired(REQUIRED_PERMISSIONS) { result ->
                    continuation.resume(result.isSuccess && result.resultMap.values.all { it })
                }
            } catch (e: Exception) {
                continuation.resume(false)
            }
        }
    }
    
    suspend fun requestPermissions(): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val permissionManager = HealthPermissionManager(store)
            
            try {
                permissionManager.requestPermissions(REQUIRED_PERMISSIONS, context as android.app.Activity) { result ->
                    val allGranted = result.isSuccess && result.resultMap.values.all { it }
                    continuation.resume(Result.success(allGranted))
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // Weight Management
    suspend fun insertWeight(weightKg: Float, timeMillis: Long): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val weightData = HealthData().apply {
                sourcePackageName = context.packageName
                putFloat(Weight.WEIGHT, weightKg)
                putLong(Weight.START_TIME, timeMillis)
                putLong(Weight.TIME_OFFSET, TimeZone.getDefault().rawOffset.toLong())
            }
            
            val request = HealthDataResolver.InsertRequest.Builder()
                .setDataType(Weight.HEALTH_DATA_TYPE)
                .addHealthData(weightData)
                .build()
                
            try {
                store.getHealthDataResolver().insert(request) { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(Exception("Insert failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    suspend fun getWeightHistory(
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<List<HealthData>> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val request = HealthDataResolver.ReadRequest.Builder()
                .setDataType(Weight.HEALTH_DATA_TYPE)
                .setFilter(
                    HealthDataResolver.Filter.and(
                        HealthDataResolver.Filter.greaterThanEquals(Weight.START_TIME, startTimeMillis),
                        HealthDataResolver.Filter.lessThanEquals(Weight.START_TIME, endTimeMillis)
                    )
                )
                .build()
                
            try {
                store.getHealthDataResolver().read(request) { result ->
                    if (result.isSuccessful) {
                        val data = mutableListOf<HealthData>()
                        result.resultDataIterator?.let { iterator ->
                            while (iterator.hasNext()) {
                                data.add(iterator.next())
                            }
                        }
                        continuation.resume(Result.success(data))
                    } else {
                        continuation.resume(Result.failure(Exception("Read failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // Steps Management
    suspend fun insertSteps(
        steps: Int,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val stepsData = HealthData().apply {
                sourcePackageName = context.packageName
                putInt(StepCount.COUNT, steps)
                putLong(StepCount.START_TIME, startTimeMillis)
                putLong(StepCount.END_TIME, endTimeMillis)
                putLong(StepCount.TIME_OFFSET, TimeZone.getDefault().rawOffset.toLong())
            }
            
            val request = HealthDataResolver.InsertRequest.Builder()
                .setDataType(StepCount.HEALTH_DATA_TYPE)
                .addHealthData(stepsData)
                .build()
                
            try {
                store.getHealthDataResolver().insert(request) { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(Exception("Insert failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // Heart Rate Management
    suspend fun insertHeartRate(bpm: Int, timeMillis: Long): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val heartRateData = HealthData().apply {
                sourcePackageName = context.packageName
                putInt(HeartRate.HEART_RATE, bpm)
                putLong(HeartRate.START_TIME, timeMillis)
                putLong(HeartRate.TIME_OFFSET, TimeZone.getDefault().rawOffset.toLong())
            }
            
            val request = HealthDataResolver.InsertRequest.Builder()
                .setDataType(HeartRate.HEALTH_DATA_TYPE)
                .addHealthData(heartRateData)
                .build()
                
            try {
                store.getHealthDataResolver().insert(request) { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(Exception("Insert failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // Exercise Management
    suspend fun insertExercise(
        exerciseType: Int,
        startTimeMillis: Long,
        endTimeMillis: Long,
        caloriesBurned: Float? = null,
        distance: Float? = null
    ): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val exerciseData = HealthData().apply {
                sourcePackageName = context.packageName
                putInt(Exercise.EXERCISE_TYPE, exerciseType)
                putLong(Exercise.START_TIME, startTimeMillis)
                putLong(Exercise.END_TIME, endTimeMillis)
                putLong(Exercise.TIME_OFFSET, TimeZone.getDefault().rawOffset.toLong())
                
                caloriesBurned?.let { putFloat(Exercise.CALORIE, it) }
                distance?.let { putFloat(Exercise.DISTANCE, it) }
            }
            
            val request = HealthDataResolver.InsertRequest.Builder()
                .setDataType(Exercise.HEALTH_DATA_TYPE)
                .addHealthData(exerciseData)
                .build()
                
            try {
                store.getHealthDataResolver().insert(request) { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(Exception("Insert failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    // Sleep Management
    suspend fun insertSleep(
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val store = healthDataStore ?: run {
                continuation.resume(Result.failure(Exception("Not connected")))
                return@suspendCancellableCoroutine
            }
            
            val sleepData = HealthData().apply {
                sourcePackageName = context.packageName
                putLong(Sleep.START_TIME, startTimeMillis)
                putLong(Sleep.END_TIME, endTimeMillis)
                putLong(Sleep.TIME_OFFSET, TimeZone.getDefault().rawOffset.toLong())
            }
            
            val request = HealthDataResolver.InsertRequest.Builder()
                .setDataType(Sleep.HEALTH_DATA_TYPE)
                .addHealthData(sleepData)
                .build()
                
            try {
                store.getHealthDataResolver().insert(request) { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(Exception("Insert failed")))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    fun disconnect() {
        healthDataStore?.disconnectService()
        isConnected = false
    }
}