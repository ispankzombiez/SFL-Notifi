package com.app.SFLNotifi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService

data class ScheduledNotification(
    val id: String,  // Unique ID for deduplication
    val title: String,
    val content: String,
    val triggerTime: Long
)

class NotificationScheduler(private val context: Context) {
    private val alarmManager: AlarmManager? = context.getSystemService()
    private val scheduledNotifications = mutableSetOf<String>() // Track scheduled notification IDs

    fun scheduleNotification(
        id: String,
        title: String,
        content: String,
        triggerTime: Long
    ) {
        // Don't schedule if already passed
        if (triggerTime <= System.currentTimeMillis()) {
            return
        }

        // Check if this notification is already scheduled
        if (scheduledNotifications.contains(id)) {
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", id)
            putExtra("title", title)
            putExtra("content", content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerTime, null),
                pendingIntent
            )
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

        scheduledNotifications.add(id)
    }

    fun cancelNotification(id: String) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager?.cancel(pendingIntent)
        scheduledNotifications.remove(id)
        Log.d("NotificationScheduler", "Cancelled notification: $id")
    }

    fun clearAllNotifications() {
        scheduledNotifications.toList().forEach { id ->
            cancelNotification(id)
        }
        scheduledNotifications.clear()
        Log.d("NotificationScheduler", "Cleared all notifications")
    }
} 