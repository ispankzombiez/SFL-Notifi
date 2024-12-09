package com.app.SFLNotifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getStringExtra("notification_id") ?: return
        val title = intent.getStringExtra("title") ?: return
        val content = intent.getStringExtra("content") ?: return

        Log.d("NotificationReceiver", "Received notification: $notificationId")
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, content)
    }
} 