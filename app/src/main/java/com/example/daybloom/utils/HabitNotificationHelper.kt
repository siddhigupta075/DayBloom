package com.example.daybloom.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

object HabitNotificationHelper {

    fun scheduleDailyHabitReminder(
        context: Context,
        habitId: Int,
        title: String,
        hour: Int,
        minute: Int
    ) {

        createChannel(context)

        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra("title", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel",
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
