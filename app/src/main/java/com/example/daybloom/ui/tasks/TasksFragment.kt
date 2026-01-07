package com.example.daybloom.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daybloom.AppDatabase
import com.example.daybloom.R
import com.example.daybloom.data.entity.TaskEntity
import com.example.daybloom.utils.NotificationHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Calendar
import android.widget.Toast
import com.example.daybloom.data.dao.TaskDao

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var adapter: TaskAdapter
    private var selectedTime: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()

        adapter = TaskAdapter(
            onComplete = { id ->
                lifecycleScope.launch { taskDao.markTaskCompleted(id) }
            },
            onDelete = { id ->
                lifecycleScope.launch { taskDao.deleteTask(id) }
            }
        )

        val recycler = view.findViewById<RecyclerView>(R.id.taskRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        lifecycleScope.launch {
            taskDao.getPendingTasks().collect {
                adapter.submitList(it)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.addTaskFab)
            .setOnClickListener {
                showAddTaskDialog(taskDao)
            }
    }

    private fun showAddTaskDialog(taskDao: TaskDao) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val titleEt = dialogView.findViewById<EditText>(R.id.taskTitle)
        val descEt = dialogView.findViewById<EditText>(R.id.taskDescription)
        val dateBtn = dialogView.findViewById<Button>(R.id.dateBtn)
        val timeBtn = dialogView.findViewById<Button>(R.id.timeBtn)

        val calendar = Calendar.getInstance()
        var selectedTime = 0L

        dateBtn.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    calendar.set(Calendar.YEAR, y)
                    calendar.set(Calendar.MONTH, m)
                    calendar.set(Calendar.DAY_OF_MONTH, d)
                    dateBtn.text = "$d/${m + 1}/$y"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        timeBtn.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, h, min ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, min)
                    calendar.set(Calendar.SECOND, 0)
                    selectedTime = calendar.timeInMillis
                    timeBtn.text = "%02d:%02d".format(h, min)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Save", null) // ⬅️ IMPORTANT
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val title = titleEt.text.toString().trim()
            val desc = descEt.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || selectedTime == 0L) {
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    taskDao.insertTask(
                        TaskEntity(
                            title = title,
                            description = desc,
                            dueDate = selectedTime,
                            isCompleted = false
                        )
                    )

                    try {
                        NotificationHelper.scheduleTaskReminder(
                            requireContext(),
                            title,
                            desc,
                            selectedTime
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
