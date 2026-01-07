package com.example.daybloom.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daybloom.R
import com.example.daybloom.data.entity.HabitEntity

class HabitAdapter(
    private val onComplete: (HabitEntity) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private var habits = listOf<HabitEntity>()

    fun submitList(list: List<HabitEntity>) {
        habits = list
        notifyDataSetChanged()
    }

    inner class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.habitTitle)
        val time: TextView = view.findViewById(R.id.habitTime)
        val streak: TextView = view.findViewById(R.id.habitStreak)
        val check: ImageView = view.findViewById(R.id.habitCheck)
        val delete: ImageView = view.findViewById(R.id.habitDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.title.text = habit.title
        holder.time.text = habit.reminderTime
        holder.streak.text = "🔥 ${habit.streak}"

        holder.check.setOnClickListener { onComplete(habit) }
        holder.delete.setOnClickListener { onDelete(habit.id) }
    }

    override fun getItemCount(): Int = habits.size

}
