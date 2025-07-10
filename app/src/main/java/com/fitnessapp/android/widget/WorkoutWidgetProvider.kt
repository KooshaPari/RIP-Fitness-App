package com.fitnessapp.android.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.fitnessapp.android.MainActivity
import com.fitnessapp.android.R

/**
 * App widget for quick workout actions
 */
class WorkoutWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_workout)
        
        // Set up click intent to open workout screen
        val workoutIntent = Intent(context, MainActivity::class.java).apply {
            data = Uri.parse("https://fitnessapp.com/workout")
            action = Intent.ACTION_VIEW
        }
        val workoutPendingIntent = PendingIntent.getActivity(
            context,
            1,
            workoutIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.start_workout_button, workoutPendingIntent)
        
        // Set up nutrition logging intent
        val nutritionIntent = Intent(context, MainActivity::class.java).apply {
            data = Uri.parse("https://fitnessapp.com/nutrition")
            action = Intent.ACTION_VIEW
        }
        val nutritionPendingIntent = PendingIntent.getActivity(
            context,
            2,
            nutritionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.log_meal_button, nutritionPendingIntent)
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}