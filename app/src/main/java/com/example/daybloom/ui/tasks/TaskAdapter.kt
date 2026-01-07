package com.example.daybloom.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daybloom.R
import com.example.daybloom.data.entity.TaskEntity

class TaskAdapter(
    private val onComplete: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks = listOf<TaskEntity>()

    fun submitList(list: List<TaskEntity>) {
        tasks = list
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val desc: TextView = view.findViewById(R.id.taskDesc)
        val check: ImageView = view.findViewById(R.id.taskCheck)
        val delete: ImageView = view.findViewById(R.id.taskDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.desc.text = task.description

        holder.check.setOnClickListener { onComplete(task.id) }
        holder.delete.setOnClickListener { onDelete(task.id) }
    }

    override fun getItemCount() = tasks.size
}