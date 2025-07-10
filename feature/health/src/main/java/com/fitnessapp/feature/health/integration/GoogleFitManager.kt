package com.fitnessapp.feature.health.integration

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataUpdateRequest
import com.google.android.gms.fitness.result.DataReadResponse
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Fit fallback manager for legacy support during Health Connect transition
 * Provides compatibility layer for devices not yet supporting Health Connect
 */
@Singleton
class GoogleFitManager @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "GoogleFitManager"
        
        val FITNESS_OPTIONS = FitnessOptions.builder()
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_WRITE)
            .build()
    }
    
    private val account: GoogleSignInAccount?
        get() = GoogleSignIn.getAccountForExtension(context, FITNESS_OPTIONS)
    
    fun isAvailable(): Boolean {
        return try {
            GoogleSignIn.hasPermissions(account, FITNESS_OPTIONS)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Google Fit availability", e)
            false
        }
    }
    
    fun hasPermissions(): Boolean {
        return account?.let { 
            GoogleSignIn.hasPermissions(it, FITNESS_OPTIONS) 
        } ?: false
    }
    
    // Weight Management
    suspend fun insertWeight(weightKg: Float, timestampMillis: Long): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_WEIGHT)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_WEIGHT, weightKg)
                .setTimestamp(timestampMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting weight", e)
            Result.failure(e)
        }
    }
    
    suspend fun getWeightHistory(
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<List<DataPoint>> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val response = Fitness.getHistoryApi(context, account)
                .readData(readRequest)
                .await()
                
            val dataPoints = response.dataSets.flatMap { it.dataPoints }
            Result.success(dataPoints)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading weight history", e)
            Result.failure(e)
        }
    }
    
    // Steps Management
    suspend fun insertSteps(
        steps: Int,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_STEPS, steps)
                .setTimeInterval(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting steps", e)
            Result.failure(e)
        }
    }
    
    suspend fun getStepsHistory(
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<List<DataPoint>> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val readRequest = DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val response = Fitness.getHistoryApi(context, account)
                .readData(readRequest)
                .await()
                
            val dataPoints = response.dataSets.flatMap { it.dataPoints }
            Result.success(dataPoints)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading steps history", e)
            Result.failure(e)
        }
    }
    
    // Heart Rate Management
    suspend fun insertHeartRate(bpm: Float, timestampMillis: Long): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_BPM, bpm)
                .setTimestamp(timestampMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting heart rate", e)
            Result.failure(e)
        }
    }
    
    // Calories Management
    suspend fun insertCalories(
        calories: Float,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_CALORIES, calories)
                .setTimeInterval(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting calories", e)
            Result.failure(e)
        }
    }
    
    // Distance Management
    suspend fun insertDistance(
        distanceMeters: Float,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_DISTANCE, distanceMeters)
                .setTimeInterval(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting distance", e)
            Result.failure(e)
        }
    }
    
    // Activity/Exercise Management
    suspend fun insertActivity(
        activityType: String,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            val dataSource = DataSource.Builder()
                .setAppPackageName(context.packageName)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_RAW)
                .build()
                
            val dataPoint = DataPoint.builder(dataSource)
                .setField(Field.FIELD_ACTIVITY, activityType)
                .setTimeInterval(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                .build()
                
            val dataSet = DataSet.builder(dataSource)
                .add(dataPoint)
                .build()
                
            Fitness.getHistoryApi(context, account)
                .insertData(dataSet)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting activity", e)
            Result.failure(e)
        }
    }
    
    // Data cleanup
    suspend fun deleteData(
        dataType: DataType,
        startTimeMillis: Long,
        endTimeMillis: Long
    ): Result<Unit> {
        return try {
            val account = this.account ?: return Result.failure(Exception("Not signed in"))
            
            Fitness.getHistoryApi(context, account)
                .deleteData(
                    DataDeleteRequest.Builder()
                        .setTimeInterval(startTimeMillis, endTimeMillis, TimeUnit.MILLISECONDS)
                        .addDataType(dataType)
                        .build()
                )
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting data", e)
            Result.failure(e)
        }
    }
}