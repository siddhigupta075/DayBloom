package com.example.daybloom.ui.habits

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daybloom.AppDatabase
import com.example.daybloom.R
import com.example.daybloom.data.dao.HabitDao
import com.example.daybloom.data.entity.HabitEntity
import com.example.daybloom.utils.HabitNotificationHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment(R.layout.fragment_habits) {

    private lateinit var adapter: HabitAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val habitDao = AppDatabase.getDatabase(requireContext()).habitDao()

        adapter = HabitAdapter(
            onComplete = { habit -> handleHabitCompletion(habit, habitDao) },
            onDelete = { id ->
                lifecycleScope.launch {
                    habitDao.deleteHabit(id)
                }
            }
        )

        val habitRecycler = view.findViewById<RecyclerView>(R.id.habitRecycler)
        habitRecycler.layoutManager = LinearLayoutManager(requireContext())
        habitRecycler.adapter = adapter

        lifecycleScope.launch {
            habitDao.getAllHabits().collect { habits ->
                adapter.submitList(habits)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.addHabitFab)
            .setOnClickListener {
                showAddHabitDialog(habitDao)
            }
    }

    private fun handleHabitCompletion(
        habit: HabitEntity,
        habitDao: com.example.daybloom.data.dao.HabitDao
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())

        val newStreak =
            if (habit.lastCompletedDate == today) habit.streak
            else habit.streak + 1

        lifecycleScope.launch {
            habitDao.updateHabitCompletion(habit.id, newStreak, today)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddHabitDialog(habitDao: HabitDao) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)

        val titleEt = dialogView.findViewById<EditText>(R.id.habitTitle)
        val timeEt = dialogView.findViewById<EditText>(R.id.habitTime)

        var selectedHour = -1
        var selectedMinute = -1

        timeEt.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    selectedHour = h
                    selectedMinute = m
                    timeEt.setText("%02d:%02d".format(h, m))
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Habit")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(requireContext().getColor(R.color.buttonPrimary))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(requireContext().getColor(R.color.textSecondary))

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val title = titleEt.text.toString().trim()

            if (title.isEmpty() || selectedHour == -1 || selectedMinute == -1) {
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val habitId = habitDao.insertHabit(
                        HabitEntity(
                            title = title,
                            reminderTime = "%02d:%02d".format(
                                selectedHour,
                                selectedMinute
                            )
                        )
                    ).toInt()

                    try {
                        HabitNotificationHelper.scheduleDailyHabitReminder(
                            requireContext(),
                            habitId,
                            title,
                            selectedHour,
                            selectedMinute
                        )
                    } catch (_: Exception) { }

                    dialog.dismiss()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}

