package com.fitnessapp.android.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fitnessapp.android.MainActivity
import com.fitnessapp.android.R
import com.fitnessapp.android.data.repository.FitnessRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Service for managing app notifications
 */
@AndroidEntryPoint
class NotificationService : Service() {
    
    @Inject
    lateinit var fitnessRepository: FitnessRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("NotificationService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_WORKOUT_REMINDER -> sendWorkoutReminder()
            ACTION_NUTRITION_REMINDER -> sendNutritionReminder()
            ACTION_ACHIEVEMENT_NOTIFICATION -> {
                val achievement = intent.getStringExtra(EXTRA_ACHIEVEMENT_TEXT)
                sendAchievementNotification(achievement ?: "Achievement unlocked!")
            }
            ACTION_HEALTH_SYNC_COMPLETE -> sendHealthSyncCompleteNotification()
        }
        
        return START_NOT_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Timber.d("NotificationService destroyed")
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun sendWorkoutReminder() {
        val intent = Intent(this, MainActivity::class.java).apply {
            data = Uri.parse("https://fitnessapp.com/workout")
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NotificationChannels.WORKOUT_REMINDERS)
            .setContentTitle("Time for your workout!")
            .setContentText("Don't break your streak - let's get moving!")
            .setSmallIcon(R.drawable.ic_fitness)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_fitness,
                "Start Workout",
                pendingIntent
            )
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_WORKOUT_REMINDER, notification)
        
        Timber.d("Workout reminder notification sent")
    }
    
    private fun sendNutritionReminder() {
        val intent = Intent(this, MainActivity::class.java).apply {
            data = Uri.parse("https://fitnessapp.com/nutrition")
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NotificationChannels.NUTRITION_REMINDERS)
            .setContentTitle("Log your meal")
            .setContentText("Remember to track what you've eaten")
            .setSmallIcon(R.drawable.ic_nutrition)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_nutrition,
                "Log Meal",
                pendingIntent
            )
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_NUTRITION_REMINDER, notification)
        
        Timber.d("Nutrition reminder notification sent")
    }
    
    private fun sendAchievementNotification(achievementText: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            data = Uri.parse("https://fitnessapp.com/profile")
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NotificationChannels.ACHIEVEMENTS)
            .setContentTitle("Achievement Unlocked! 🎉")
            .setContentText(achievementText)
            .setSmallIcon(R.drawable.ic_achievement)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_ACHIEVEMENT, notification)
        
        Timber.d("Achievement notification sent: $achievementText")
    }
    
    private fun sendHealthSyncCompleteNotification() {
        val notification = NotificationCompat.Builder(this, NotificationChannels.HEALTH_SYNC)
            .setContentTitle("Health data synced")
            .setContentText("Your health data has been updated")
            .setSmallIcon(R.drawable.ic_sync)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_HEALTH_SYNC, notification)
        
        Timber.d("Health sync complete notification sent")
    }
    
    companion object {
        const val ACTION_WORKOUT_REMINDER = "com.fitnessapp.android.ACTION_WORKOUT_REMINDER"
        const val ACTION_NUTRITION_REMINDER = "com.fitnessapp.android.ACTION_NUTRITION_REMINDER"
        const val ACTION_ACHIEVEMENT_NOTIFICATION = "com.fitnessapp.android.ACTION_ACHIEVEMENT_NOTIFICATION"
        const val ACTION_HEALTH_SYNC_COMPLETE = "com.fitnessapp.android.ACTION_HEALTH_SYNC_COMPLETE"
        
        const val EXTRA_ACHIEVEMENT_TEXT = "achievement_text"
        
        private const val NOTIFICATION_ID_WORKOUT_REMINDER = 3001
        private const val NOTIFICATION_ID_NUTRITION_REMINDER = 3002
        private const val NOTIFICATION_ID_ACHIEVEMENT = 3003
        private const val NOTIFICATION_ID_HEALTH_SYNC = 3004
        
        fun sendWorkoutReminder(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_WORKOUT_REMINDER
            }
            context.startService(intent)
        }
        
        fun sendNutritionReminder(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_NUTRITION_REMINDER
            }
            context.startService(intent)
        }
        
        fun sendAchievementNotification(context: Context, achievementText: String) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_ACHIEVEMENT_NOTIFICATION
                putExtra(EXTRA_ACHIEVEMENT_TEXT, achievementText)
            }
            context.startService(intent)
        }
        
        fun sendHealthSyncComplete(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_HEALTH_SYNC_COMPLETE
            }
            context.startService(intent)
        }
    }
}