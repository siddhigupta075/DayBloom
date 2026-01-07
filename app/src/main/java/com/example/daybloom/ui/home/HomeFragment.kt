package com.example.daybloom.ui.home

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
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Switch

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val themeSwitch = view.findViewById<Switch>(R.id.themeSwitch)

        themeSwitch.isChecked =
            com.example.daybloom.utils.ThemeManager.isDarkMode(requireContext())

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            com.example.daybloom.utils.ThemeManager.toggleTheme(
                requireContext(),
                isChecked
            )
        }


        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()
        val habitDao = AppDatabase.getDatabase(requireContext()).habitDao()

        val greeting = view.findViewById<TextView>(R.id.greetingText)
        val tasksLeft = view.findViewById<TextView>(R.id.tasksLeftText)
        val habitsLeft = view.findViewById<TextView>(R.id.habitsLeftText)
        val progressBar = view.findViewById<ProgressBar>(R.id.habitProgress)
        val progressText = view.findViewById<TextView>(R.id.progressText)

        greeting.text = getGreeting()

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())

        lifecycleScope.launch {

            // Tasks left
            taskDao.getPendingTaskCount().collect {
                tasksLeft.text =
                    if (it == 0) "No tasks today 🎉"
                    else "Tasks left: $it"
            }
        }

        lifecycleScope.launch {

            // Habit progress
            combine(
                habitDao.getTotalHabits(),
                habitDao.getCompletedHabitsToday(today)
            ) { total, completed -> total to completed }
                .collect { (total, completed) ->

                    if (total == 0) {
                        habitsLeft.text = "No habits yet 🌱"
                        progressBar.progress = 0
                        progressText.text = "0% completed"
                    } else {
                        habitsLeft.text = "Habits left: ${total - completed}"
                        val percent = (completed * 100) / total
                        progressBar.progress = percent
                        progressText.text = "$percent% completed"
                    }
                }
        }
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤"
            else -> "Good Evening 🌙"
        }
    }
}
