package com.example.daybloom.ui.calendar

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.daybloom.AppDatabase
import com.example.daybloom.R
import com.example.daybloom.data.entity.EventEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private var selectedDate: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val selectedDateText = view.findViewById<TextView>(R.id.selectedDateText)
        val eventContainer = view.findViewById<LinearLayout>(R.id.eventContainer)
        val addEventBtn = view.findViewById<Button>(R.id.addEventBtn)

        val eventDao = AppDatabase.getDatabase(requireContext()).eventDao()

        // ✅ Default = today
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
        selectedDateText.text = "Events on $selectedDate"

        fun loadEvents() {
            lifecycleScope.launch {
                eventDao.getEventsByDate(selectedDate).collect { events ->
                    eventContainer.removeAllViews()
                    if (events.isEmpty()) {
                        eventContainer.addView(TextView(requireContext()).apply {
                            text = "No events"
                        })
                    } else {
                        events.forEach { event ->
                            eventContainer.addView(TextView(requireContext()).apply {
                                text = "• ${event.title}\n${event.description}"
                                textSize = 16f
                            })
                        }
                    }
                }
            }
        }

        loadEvents()

        calendarView.setOnDateChangeListener { _, y, m, d ->
            selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            selectedDateText.text = "Events on $selectedDate"
            loadEvents()
        }

        addEventBtn.setOnClickListener {
            showAddEventDialog(eventDao)
        }
    }

    private fun showAddEventDialog(
        eventDao: com.example.daybloom.data.dao.EventDao
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dailog_add_event, null)
        val titleEt = dialogView.findViewById<EditText>(R.id.eventTitle)
        val descEt = dialogView.findViewById<EditText>(R.id.eventDesc)

        AlertDialog.Builder(requireContext())


            .setTitle("Add Event")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                if (titleEt.text.isNullOrBlank()) return@setPositiveButton

                lifecycleScope.launch {
                    eventDao.insertEvent(
                        EventEntity(
                            date = selectedDate,
                            title = titleEt.text.toString(),
                            description = descEt.text.toString()
                        )
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }
}
