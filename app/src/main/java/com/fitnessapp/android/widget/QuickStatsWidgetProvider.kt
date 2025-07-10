package com.fitnessapp.android.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.fitnessapp.android.MainActivity
import com.fitnessapp.android.R

/**
 * App widget for displaying quick fitness stats on the home screen
 */
class QuickStatsWidgetProvider : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Create RemoteViews for the widget layout
        val views = RemoteViews(context.packageName, R.layout.widget_quick_stats)
        
        // Set up click intent to open the app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        
        // Update widget data (this would fetch real data in a full implementation)
        views.setTextViewText(R.id.steps_count, "8,234")
        views.setTextViewText(R.id.calories_count, "1,856")
        views.setTextViewText(R.id.workouts_count, "2")
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}