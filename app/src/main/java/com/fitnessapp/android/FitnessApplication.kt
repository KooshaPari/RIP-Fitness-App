package com.fitnessapp.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.fitnessapp.android.di.AppModule
import com.fitnessapp.android.service.NotificationChannels
import com.fitnessapp.android.worker.HealthSyncWorker
import com.fitnessapp.android.worker.NutritionReminderWorker
import com.fitnessapp.android.worker.WorkoutReminderWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
// Remove Timber import - not configured yet
// import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for FitnessApp
 * 
 * Handles:
 * - Dependency injection setup with Hilt
 * - Notification channels creation
 * - WorkManager configuration
 * - Background sync initialization
 * - Crash reporting setup
 * - Logging configuration
 */
@HiltAndroidApp
class FitnessApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize logging
        initializeLogging()
        
        // Create notification channels
        createNotificationChannels()
        
        // Initialize background work
        initializeBackgroundWork()
        
        // Timber.i("FitnessApplication initialized successfully")
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.INFO)
            .build()
    }
    
    private fun initializeLogging() {
        // Configure logging when Timber is properly set up
        /*
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            // In production, you might want to use a custom tree for crash reporting
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Only log warnings and errors in production
                    if (priority >= android.util.Log.WARN) {
                        // Send to crash reporting service (Firebase Crashlytics, etc.)
                        super.log(priority, tag, message, t)
                    }
                }
            })
        }
        */
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Create all notification channels
            val channels = listOf(
                NotificationChannel(
                    NotificationChannels.WORKOUT_REMINDERS,
                    "Workout Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders for scheduled workouts"
                    enableVibration(true)
                },
                
                NotificationChannel(
                    NotificationChannels.NUTRITION_REMINDERS,
                    "Nutrition Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Meal logging and nutrition reminders"
                    enableVibration(true)
                },
                
                NotificationChannel(
                    NotificationChannels.HEALTH_SYNC,
                    "Health Data Sync",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Health data synchronization status"
                    enableVibration(false)
                },
                
                NotificationChannel(
                    NotificationChannels.ACHIEVEMENTS,
                    "Achievements",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Goal achievements and milestones"
                    enableVibration(true)
                    enableLights(true)
                },
                
                NotificationChannel(
                    NotificationChannels.WORKOUT_TIMER,
                    "Workout Timer",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Active workout session notifications"
                    setShowBadge(false)
                }
            )
            
            notificationManager.createNotificationChannels(channels)
            // Timber.d("Created ${channels.size} notification channels")
        }
    }
    
    private fun initializeBackgroundWork() {
        applicationScope.launch {
            try {
                // Schedule periodic health data sync
                HealthSyncWorker.schedulePeriodicSync()
                
                // Schedule workout reminders
                WorkoutReminderWorker.scheduleReminders()
                
                // Schedule nutrition reminders
                NutritionReminderWorker.scheduleReminders()
                
                // Timber.d("Background work scheduled successfully")
            } catch (e: Exception) {
                // Timber.e(e, "Failed to initialize background work")
            }
        }
    }
}