package com.fitnessapp.core.network.sync

import android.content.Context
import androidx.work.*
import com.fitnessapp.core.network.cache.NetworkCacheManager
import com.fitnessapp.core.network.monitoring.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages offline operations and sync when connection is restored
 */
@Singleton
class OfflineSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor,
    private val cacheManager: NetworkCacheManager
) {

    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Queue API request for later execution when online
     */
    suspend fun queueOfflineRequest(
        url: String,
        method: String,
        body: String? = null,
        headers: Map<String, String> = emptyMap(),
        priority: SyncPriority = SyncPriority.NORMAL
    ): String {
        val requestId = generateRequestId()
        
        try {
            val offlineRequest = OfflineRequest(
                id = requestId,
                url = url,
                method = method,
                body = body,
                headers = headers,
                timestamp = System.currentTimeMillis(),
                priority = priority,
                retryCount = 0
            )
            
            // Store in local database/shared preferences
            storeOfflineRequest(offlineRequest)
            
            // Schedule sync work
            scheduleSync(priority)
            
            Timber.d("Queued offline request: $requestId for $url")
            return requestId
        } catch (e: Exception) {
            Timber.e(e, "Failed to queue offline request")
            throw e
        }
    }

    /**
     * Process queued requests when network becomes available
     */
    suspend fun processPendingRequests(): List<SyncResult> {
        val results = mutableListOf<SyncResult>()
        
        try {
            if (!networkMonitor.isCurrentlyOnline()) {
                Timber.d("Network not available, skipping sync")
                return results
            }
            
            val pendingRequests = getPendingRequests()
            Timber.d("Processing ${pendingRequests.size} pending requests")
            
            for (request in pendingRequests.sortedBy { it.priority.ordinal }) {
                val result = processRequest(request)
                results.add(result)
                
                if (result.success) {
                    removeProcessedRequest(request.id)
                } else {
                    updateRequestRetryCount(request.id)
                }
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Error processing pending requests")
        }
        
        return results
    }

    /**
     * Schedule periodic sync work
     */
    private fun scheduleSync(priority: SyncPriority) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val delay = when (priority) {
            SyncPriority.HIGH -> 0L
            SyncPriority.NORMAL -> 5L
            SyncPriority.LOW -> 15L
        }

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_offline_requests",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Handle conflict resolution for duplicate requests
     */
    suspend fun resolveConflicts(conflicts: List<ConflictData>): List<ConflictResolution> {
        return conflicts.map { conflict ->
            // Implement conflict resolution strategy
            // For now, use "last write wins" strategy
            ConflictResolution(
                conflictId = conflict.id,
                resolution = ResolutionStrategy.LAST_WRITE_WINS,
                selectedData = conflict.serverData // Prefer server data
            )
        }
    }

    private fun generateRequestId(): String {
        return "offline_${System.currentTimeMillis()}_${(0..999).random()}"
    }

    private suspend fun storeOfflineRequest(request: OfflineRequest) {
        // Store in local database - would be implemented with Room
        // For now, just log
        Timber.d("Storing offline request: ${request.id}")
    }

    private suspend fun getPendingRequests(): List<OfflineRequest> {
        // Retrieve from local database - would be implemented with Room
        // For now, return empty list
        return emptyList()
    }

    private suspend fun processRequest(request: OfflineRequest): SyncResult {
        return try {
            // Process the request using appropriate API client
            // This would involve making the actual HTTP request
            Timber.d("Processing request: ${request.id}")
            
            SyncResult(
                requestId = request.id,
                success = true,
                timestamp = System.currentTimeMillis(),
                error = null
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to process request: ${request.id}")
            SyncResult(
                requestId = request.id,
                success = false,
                timestamp = System.currentTimeMillis(),
                error = e.message
            )
        }
    }

    private suspend fun removeProcessedRequest(requestId: String) {
        Timber.d("Removing processed request: $requestId")
    }

    private suspend fun updateRequestRetryCount(requestId: String) {
        Timber.d("Updating retry count for request: $requestId")
    }
}

@Serializable
data class OfflineRequest(
    val id: String,
    val url: String,
    val method: String,
    val body: String?,
    val headers: Map<String, String>,
    val timestamp: Long,
    val priority: SyncPriority,
    val retryCount: Int
)

@Serializable
data class SyncResult(
    val requestId: String,
    val success: Boolean,
    val timestamp: Long,
    val error: String?
)

@Serializable
data class ConflictData(
    val id: String,
    val localData: String,
    val serverData: String,
    val timestamp: Long
)

@Serializable
data class ConflictResolution(
    val conflictId: String,
    val resolution: ResolutionStrategy,
    val selectedData: String
)

enum class SyncPriority {
    HIGH, NORMAL, LOW
}

enum class ResolutionStrategy {
    LAST_WRITE_WINS,
    FIRST_WRITE_WINS,
    MANUAL_RESOLUTION,
    MERGE_DATA
}

/**
 * WorkManager worker for background sync
 */
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: OfflineSyncManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val results = syncManager.processPendingRequests()
            val successCount = results.count { it.success }
            val totalCount = results.size
            
            Timber.d("Sync completed: $successCount/$totalCount requests processed successfully")
            
            if (successCount == totalCount) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Timber.e(e, "Sync work failed")
            Result.failure()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(context: Context, params: WorkerParameters): SyncWorker
    }
}