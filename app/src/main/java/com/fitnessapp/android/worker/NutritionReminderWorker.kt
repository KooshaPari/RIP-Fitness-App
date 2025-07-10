package com.fitnessapp.android.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.fitnessapp.android.R
import com.fitnessapp.android.data.repository.FitnessRepository
import com.fitnessapp.android.service.NotificationChannels
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Worker for sending nutrition logging reminders
 */
@HiltWorker
class NutritionReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fitnessRepository: FitnessRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Checking for nutrition reminders")
            
            // Check if user has logged meals today
            // This is simplified - implement proper logic based on meal times
            val shouldRemind = true // Implement your logic here
            
            if (shouldRemind) {
                sendNutritionReminder()
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Nutrition reminder worker failed")
            Result.failure()
        }
    }
    
    private fun sendNutritionReminder() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.NUTRITION_REMINDERS)
            .setContentTitle("Log your meal")
            .setContentText("Remember to track what you've eaten today")
            .setSmallIcon(R.drawable.ic_nutrition) // You'll need to add this icon
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(2002, notification)
        Timber.d("Nutrition reminder notification sent")
    }
    
    companion object {
        private const val WORK_NAME = "nutrition_reminder_work"
        
        fun scheduleReminders() {
            val workRequest = PeriodicWorkRequestBuilder<NutritionReminderWorker>(
                4, TimeUnit.HOURS // Check every 4 hours for meal reminders
            ).build()
            
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun cancelReminders() {
            WorkManager.getInstance().cancelUniqueWork(WORK_NAME)
        }
    }
}