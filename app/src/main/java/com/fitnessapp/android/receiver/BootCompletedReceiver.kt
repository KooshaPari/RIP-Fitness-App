package com.fitnessapp.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fitnessapp.android.worker.HealthSyncWorker
import com.fitnessapp.android.worker.NutritionReminderWorker
import com.fitnessapp.android.worker.WorkoutReminderWorker
import timber.log.Timber

/**
 * Receiver for handling device boot completion
 * Reschedules background work and notifications
 */
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Device boot completed, rescheduling background work")
            
            try {
                // Reschedule all background workers
                HealthSyncWorker.schedulePeriodicSync()
                WorkoutReminderWorker.scheduleReminders()
                NutritionReminderWorker.scheduleReminders()
                
                Timber.i("Background work rescheduled successfully after boot")
            } catch (e: Exception) {
                Timber.e(e, "Failed to reschedule background work after boot")
            }
        }
    }
}