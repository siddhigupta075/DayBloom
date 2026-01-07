package com.example.daybloom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val reminderTime: String,   // "08:30 AM"
    val streak: Int = 0,
    val lastCompletedDate: String? = null
)
