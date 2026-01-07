package com.example.daybloom.ui.progress

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.daybloom.AppDatabase
import com.example.daybloom.R
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

class ProgressFragment : Fragment(R.layout.fragment_progress) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()
        val habitDao = AppDatabase.getDatabase(requireContext()).habitDao()

        val tasksCompletedText =
            view.findViewById<TextView>(R.id.tasksCompletedText)
        val highestStreakText =
            view.findViewById<TextView>(R.id.highestStreakText)
        val productivityBar =
            view.findViewById<ProgressBar>(R.id.productivityBar)
        val productivityText =
            view.findViewById<TextView>(R.id.productivityText)

        val weekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        lifecycleScope.launch {

            combine(
                taskDao.getCompletedTasksThisWeek(weekStart),
                habitDao.getActiveHabits(),
                habitDao.getHighestHabitStreak()
            ) { tasksDone, activeHabits, bestStreak ->
                Triple(tasksDone, activeHabits, bestStreak ?: 0)
            }.collect { (tasksDone, activeHabits, bestStreak) ->

                tasksCompletedText.text = tasksDone.toString()
                highestStreakText.text = "$bestStreak days"

                val productivity =
                    if (activeHabits == 0) 0
                    else (tasksDone * 100 / (activeHabits * 7)).coerceAtMost(100)

                productivityBar.progress = productivity
                productivityText.text = "$productivity%"
            }
        }
    }
}
