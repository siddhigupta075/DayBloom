package com.example.daybloom.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.daybloom.MainActivity
import com.example.daybloom.R

object NotificationHelper {

    private const val CHANNEL_ID = "task_reminder_channel"

    fun scheduleTaskReminder(
        context: Context,
        taskTitle: String,
        taskDesc: String,
        triggerTime: Long
    ) {

        createChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("title", taskTitle)

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskTitle.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
