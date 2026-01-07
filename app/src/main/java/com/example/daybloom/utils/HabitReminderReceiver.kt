package com.example.daybloom.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.daybloom.R

class HabitReminderReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {

        val habitTitle = intent.getStringExtra("habit_title") ?: "Habit Reminder"

        val notification = NotificationCompat.Builder(context, "habit_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time for your habit 🌱")
            .setContentText(habitTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(habitTitle.hashCode(), notification)
    }
}
