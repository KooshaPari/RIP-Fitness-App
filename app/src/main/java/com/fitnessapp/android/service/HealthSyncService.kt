package com.fitnessapp.android.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fitnessapp.android.R
import com.fitnessapp.feature.health.sync.HealthDataSyncManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service for syncing health data
 */
@AndroidEntryPoint
class HealthSyncService : Service() {
    
    @Inject
    lateinit var healthDataSyncManager: HealthDataSyncManager
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationId = 1001
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("HealthSyncService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(notificationId, createNotification())
        
        serviceScope.launch {
            try {
                Timber.d("Starting health data sync")
                healthDataSyncManager.performFullSync()
                Timber.d("Health data sync completed")
            } catch (e: Exception) {
                Timber.e(e, "Health data sync failed")
            } finally {
                stopSelf()
            }
        }
        
        return START_NOT_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Timber.d("HealthSyncService destroyed")
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationChannels.HEALTH_SYNC)
            .setContentTitle("Syncing Health Data")
            .setContentText("Synchronizing your health data with connected apps")
            .setSmallIcon(R.drawable.ic_sync) // You'll need to add this icon
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()
    }
    
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HealthSyncService::class.java)
            context.startForegroundService(intent)
        }
    }
}