package com.example.wellnessmate.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.wellnessmate.MainActivity
import com.example.wellnessmate.R
import com.example.wellnessmate.utils.DataManager
import java.text.DateFormat
import java.util.Date

class HabitProgressWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, HabitProgressWidget::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    companion object {
        private const val ACTION_REFRESH = "com.example.wellnessmate.widget.ACTION_REFRESH"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, HabitProgressWidget::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }

        private fun createLaunchPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        private fun createRefreshPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, HabitProgressWidget::class.java).apply {
                action = ACTION_REFRESH
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    context,
                    1,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val dataManager = DataManager(context)
            val progress = dataManager.getHabitProgress().coerceIn(0, 100)
            val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())

            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            views.setTextViewText(R.id.widget_title, context.getString(R.string.app_name))
            views.setTextViewText(R.id.widget_subtitle, "Today's Habit Completion")
            views.setTextViewText(R.id.widget_percentage, "$progress%")
            views.setProgressBar(R.id.widget_progress_bar, 100, progress, false)
            views.setTextViewText(R.id.widget_last_updated, "Updated $time")

            // Click to open app
            views.setOnClickPendingIntent(R.id.widget_root, createLaunchPendingIntent(context))
            // Click refresh
            views.setOnClickPendingIntent(R.id.widget_refresh, createRefreshPendingIntent(context))

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
