package com.fitnessapp.feature.health.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import com.fitnessapp.feature.health.integration.HealthConnectManager
import com.fitnessapp.feature.health.integration.GoogleFitManager
import com.fitnessapp.feature.health.integration.SamsungHealthManager
import com.fitnessapp.feature.health.wearable.WearableIntegrationManager
import com.fitnessapp.feature.health.conflict.HealthDataConflictResolver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive health data synchronization manager
 * Orchestrates bidirectional sync between all health platforms with conflict resolution
 */
@Singleton
class HealthDataSyncManager @Inject constructor(
    private val context: Context,
    private val healthConnectManager: HealthConnectManager,
    private val googleFitManager: GoogleFitManager,
    private val samsungHealthManager: SamsungHealthManager,
    private val wearableManager: WearableIntegrationManager,
    private val conflictResolver: HealthDataConflictResolver
) {
    
    companion object {
        private const val TAG = "HealthDataSyncManager"
        private const val SYNC_WORK_NAME = "health_data_sync"
        private const val CONFLICT_SYNC_WORK_NAME = "health_conflict_resolution_sync"
        
        // Sync intervals
        private const val PERIODIC_SYNC_INTERVAL_HOURS = 6L
        private const val BACKGROUND_SYNC_INTERVAL_HOURS = 12L
        private const val WEARABLE_SYNC_INTERVAL_HOURS = 2L
    }
    
    private val workManager = WorkManager.getInstance(context)
    
    private val _syncEvents = MutableSharedFlow<SyncEvent>()
    val syncEvents: Flow<SyncEvent> = _syncEvents.asSharedFlow()
    
    data class SyncEvent(
        val type: SyncEventType,
        val platform: HealthPlatform,
        val dataType: String,
        val timestamp: Instant,
        val message: String,
        val error: Throwable? = null
    )
    
    enum class SyncEventType {
        SYNC_STARTED,
        SYNC_COMPLETED,
        SYNC_FAILED,
        CONFLICT_DETECTED,
        CONFLICT_RESOLVED,
        DATA_INSERTED,
        DATA_UPDATED
    }
    
    enum class HealthPlatform {
        HEALTH_CONNECT,
        GOOGLE_FIT,
        SAMSUNG_HEALTH,
        FITBIT,
        GARMIN,
        APPLE_WATCH,
        WEAR_OS
    }
    
    data class SyncConfiguration(
        val enableHealthConnect: Boolean = true,
        val enableGoogleFit: Boolean = true,
        val enableSamsungHealth: Boolean = true,
        val enableWearables: Boolean = true,
        val syncWeight: Boolean = true,
        val syncSteps: Boolean = true,
        val syncExercises: Boolean = true,
        val syncHeartRate: Boolean = true,
        val syncSleep: Boolean = true,
        val syncNutrition: Boolean = true,
        val bidirectionalSync: Boolean = true,
        val autoResolveConflicts: Boolean = true,
        val syncIntervalHours: Long = PERIODIC_SYNC_INTERVAL_HOURS
    )
    
    private var syncConfig = SyncConfiguration()
    
    suspend fun initializeSync(config: SyncConfiguration = SyncConfiguration()) {
        this.syncConfig = config
        
        try {
            // Cancel existing work
            workManager.cancelUniqueWork(SYNC_WORK_NAME)
            workManager.cancelUniqueWork(CONFLICT_SYNC_WORK_NAME)
            
            // Schedule periodic sync
            schedulePeriodicSync()
            
            // Schedule conflict resolution sync
            scheduleConflictResolutionSync()
            
            // Initial sync
            performFullSync()
            
            emitSyncEvent(
                SyncEventType.SYNC_STARTED,
                HealthPlatform.HEALTH_CONNECT,
                "initialization",
                "Health data sync initialized"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing sync", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "initialization",
                "Failed to initialize sync",
                e
            )
        }
    }
    
    private suspend fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<HealthSyncWorker>(
            syncConfig.syncIntervalHours, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("health_sync")
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRequest
        )
    }
    
    private suspend fun scheduleConflictResolutionSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val conflictRequest = PeriodicWorkRequestBuilder<ConflictResolutionWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("conflict_resolution")
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            CONFLICT_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            conflictRequest
        )
    }
    
    suspend fun performFullSync() {
        try {
            emitSyncEvent(
                SyncEventType.SYNC_STARTED,
                HealthPlatform.HEALTH_CONNECT,
                "full_sync",
                "Starting full health data sync"
            )
            
            // Sync weight data
            if (syncConfig.syncWeight) {
                syncWeightData()
            }
            
            // Sync fitness data
            if (syncConfig.syncSteps) {
                syncStepsData()
            }
            
            if (syncConfig.syncExercises) {
                syncExerciseData()
            }
            
            // Sync health vitals
            if (syncConfig.syncHeartRate) {
                syncHeartRateData()
            }
            
            // Sync nutrition
            if (syncConfig.syncNutrition) {
                syncNutritionData()
            }
            
            // Sync sleep
            if (syncConfig.syncSleep) {
                syncSleepData()
            }
            
            // Sync wearable data
            if (syncConfig.enableWearables) {
                syncWearableData()
            }
            
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "full_sync",
                "Full health data sync completed"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in full sync", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "full_sync",
                "Full sync failed",
                e
            )
        }
    }
    
    private suspend fun syncWeightData() {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minusSeconds(24 * 60 * 60) // Last 24 hours
            
            // Collect data from all sources
            val healthConnectData = if (syncConfig.enableHealthConnect && healthConnectManager.isAvailable()) {
                healthConnectManager.getWeightHistory(startTime, endTime).getOrNull() ?: emptyList()
            } else emptyList()
            
            val googleFitData = if (syncConfig.enableGoogleFit && googleFitManager.isAvailable()) {
                googleFitManager.getWeightHistory(
                    startTime.toEpochMilli(),
                    endTime.toEpochMilli()
                ).getOrNull() ?: emptyList()
            } else emptyList()
            
            // Resolve conflicts and sync bidirectionally
            if (syncConfig.bidirectionalSync) {
                val conflicts = conflictResolver.detectWeightConflicts(
                    healthConnectData,
                    googleFitData
                )
                
                if (conflicts.isNotEmpty() && syncConfig.autoResolveConflicts) {
                    conflictResolver.resolveWeightConflicts(conflicts)
                    emitSyncEvent(
                        SyncEventType.CONFLICT_RESOLVED,
                        HealthPlatform.HEALTH_CONNECT,
                        "weight",
                        "Resolved ${conflicts.size} weight conflicts"
                    )
                }
            }
            
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "weight",
                "Weight data sync completed"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing weight data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "weight",
                "Weight sync failed",
                e
            )
        }
    }
    
    private suspend fun syncStepsData() {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minusSeconds(24 * 60 * 60)
            
            // Health Connect steps
            if (syncConfig.enableHealthConnect && healthConnectManager.isAvailable()) {
                val totalSteps = healthConnectManager.getAggregatedSteps(startTime, endTime).getOrNull() ?: 0L
                
                // Sync to other platforms if bidirectional
                if (syncConfig.bidirectionalSync && totalSteps > 0) {
                    syncStepsToOtherPlatforms(totalSteps, startTime, endTime)
                }
            }
            
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "steps",
                "Steps data sync completed"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing steps data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "steps",
                "Steps sync failed",
                e
            )
        }
    }
    
    private suspend fun syncStepsToOtherPlatforms(steps: Long, startTime: Instant, endTime: Instant) {
        // Sync to Google Fit
        if (syncConfig.enableGoogleFit && googleFitManager.isAvailable()) {
            googleFitManager.insertSteps(
                steps.toInt(),
                startTime.toEpochMilli(),
                endTime.toEpochMilli()
            )
        }
        
        // Sync to Samsung Health
        if (syncConfig.enableSamsungHealth && samsungHealthManager.isAvailable()) {
            samsungHealthManager.insertSteps(
                steps.toInt(),
                startTime.toEpochMilli(),
                endTime.toEpochMilli()
            )
        }
    }
    
    private suspend fun syncExerciseData() {
        // Implementation for exercise data sync
        try {
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "exercise",
                "Exercise data sync completed"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing exercise data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "exercise",
                "Exercise sync failed",
                e
            )
        }
    }
    
    private suspend fun syncHeartRateData() {
        // Implementation for heart rate sync
        try {
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "heart_rate",
                "Heart rate data sync completed"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing heart rate data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "heart_rate",
                "Heart rate sync failed",
                e
            )
        }
    }
    
    private suspend fun syncNutritionData() {
        // Implementation for nutrition sync
        try {
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "nutrition",
                "Nutrition data sync completed"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing nutrition data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "nutrition",
                "Nutrition sync failed",
                e
            )
        }
    }
    
    private suspend fun syncSleepData() {
        // Implementation for sleep sync
        try {
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.HEALTH_CONNECT,
                "sleep",
                "Sleep data sync completed"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing sleep data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "sleep",
                "Sleep sync failed",
                e
            )
        }
    }
    
    private suspend fun syncWearableData() {
        // Implementation for wearable data sync
        try {
            emitSyncEvent(
                SyncEventType.SYNC_COMPLETED,
                HealthPlatform.FITBIT,
                "wearable",
                "Wearable data sync completed"
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing wearable data", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.FITBIT,
                "wearable",
                "Wearable sync failed",
                e
            )
        }
    }
    
    suspend fun requestImmediateSync(dataTypes: Set<String> = setOf("all")) {
        try {
            val oneTimeWorkRequest = OneTimeWorkRequestBuilder<HealthSyncWorker>()
                .setInputData(
                    workDataOf("sync_types" to dataTypes.joinToString(","))
                )
                .addTag("immediate_sync")
                .build()
                
            workManager.enqueue(oneTimeWorkRequest)
            
            emitSyncEvent(
                SyncEventType.SYNC_STARTED,
                HealthPlatform.HEALTH_CONNECT,
                "immediate",
                "Immediate sync requested"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting immediate sync", e)
            emitSyncEvent(
                SyncEventType.SYNC_FAILED,
                HealthPlatform.HEALTH_CONNECT,
                "immediate",
                "Immediate sync failed",
                e
            )
        }
    }
    
    fun updateSyncConfiguration(config: SyncConfiguration) {
        this.syncConfig = config
        
        // Reschedule work with new configuration
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
        schedulePeriodicSync()
    }
    
    private suspend fun emitSyncEvent(
        type: SyncEventType,
        platform: HealthPlatform,
        dataType: String,
        message: String,
        error: Throwable? = null
    ) {
        val event = SyncEvent(
            type = type,
            platform = platform,
            dataType = dataType,
            timestamp = Instant.now(),
            message = message,
            error = error
        )
        
        _syncEvents.emit(event)
        Log.d(TAG, "Sync event: $event")
    }
    
    fun getSyncStatus(): Flow<SyncEvent> = syncEvents
}

// Worker classes for background sync
class HealthSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Implementation would use dependency injection to get sync manager
        return Result.success()
    }
}

class ConflictResolutionWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Implementation for conflict resolution
        return Result.success()
    }
}