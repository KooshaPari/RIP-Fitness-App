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
 * Worker for sending workout reminders
 */
@HiltWorker
class WorkoutReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fitnessRepository: FitnessRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            Timber.d("Checking for workout reminders")
            
            val user = fitnessRepository.getUser()
            val userGoals = fitnessRepository.getUserGoals()
            
            if (user != null && userGoals != null) {
                // Check if user hasn't worked out today
                // This is a simplified check - in real app, you'd check last workout date
                val shouldRemind = true // Implement your logic here
                
                if (shouldRemind) {
                    sendWorkoutReminder()
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Workout reminder worker failed")
            Result.failure()
        }
    }
    
    private fun sendWorkoutReminder() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.WORKOUT_REMINDERS)
            .setContentTitle("Time for your workout!")
            .setContentText("Don't forget to log your exercise session today")
            .setSmallIcon(R.drawable.ic_fitness) // You'll need to add this icon
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(2001, notification)
        Timber.d("Workout reminder notification sent")
    }
    
    companion object {
        private const val WORK_NAME = "workout_reminder_work"
        
        fun scheduleReminders() {
            val workRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(
                1, TimeUnit.DAYS // Check daily
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