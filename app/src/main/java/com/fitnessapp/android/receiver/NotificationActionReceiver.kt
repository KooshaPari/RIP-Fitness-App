package com.fitnessapp.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fitnessapp.android.MainActivity
import timber.log.Timber

/**
 * Receiver for handling notification action buttons
 */
class NotificationActionReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_START_WORKOUT -> {
                Timber.d("Starting workout from notification action")
                val workoutIntent = Intent(context, MainActivity::class.java).apply {
                    data = Uri.parse("https://fitnessapp.com/workout")
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(workoutIntent)
            }
            
            ACTION_LOG_MEAL -> {
                Timber.d("Logging meal from notification action")
                val nutritionIntent = Intent(context, MainActivity::class.java).apply {
                    data = Uri.parse("https://fitnessapp.com/nutrition")
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(nutritionIntent)
            }
            
            ACTION_VIEW_ACHIEVEMENTS -> {
                Timber.d("Viewing achievements from notification action")
                val profileIntent = Intent(context, MainActivity::class.java).apply {
                    data = Uri.parse("https://fitnessapp.com/profile")
                    action = Intent.ACTION_VIEW
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(profileIntent)
            }
            
            ACTION_DISMISS -> {
                Timber.d("Notification dismissed by user action")
                // Handle notification dismissal if needed
            }
        }
    }
    
    companion object {
        const val ACTION_START_WORKOUT = "com.fitnessapp.android.ACTION_START_WORKOUT"
        const val ACTION_LOG_MEAL = "com.fitnessapp.android.ACTION_LOG_MEAL"
        const val ACTION_VIEW_ACHIEVEMENTS = "com.fitnessapp.android.ACTION_VIEW_ACHIEVEMENTS"
        const val ACTION_DISMISS = "com.fitnessapp.android.ACTION_DISMISS"
    }
}