package com.fitnessapp.android.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.fitnessapp.android.service.HealthSyncService
import com.fitnessapp.feature.health.sync.HealthDataSyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Background worker for periodic health data synchronization
 */
@HiltWorker
class HealthSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthDataSyncManager: HealthDataSyncManager
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Starting health sync worker")
            
            // Start foreground service for long-running sync
            HealthSyncService.start(applicationContext)
            
            // Perform incremental sync in worker
            healthDataSyncManager.performIncrementalSync()
            
            Timber.d("Health sync worker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Health sync worker failed")
            Result.retry()
        }
    }
    
    companion object {
        private const val WORK_NAME = "health_sync_work"
        
        fun schedulePeriodicSync() {
            val workRequest = PeriodicWorkRequestBuilder<HealthSyncWorker>(
                6, TimeUnit.HOURS, // Repeat every 6 hours
                30, TimeUnit.MINUTES // Flex interval of 30 minutes
            ).build()
            
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun cancelPeriodicSync() {
            WorkManager.getInstance().cancelUniqueWork(WORK_NAME)
        }
    }
}