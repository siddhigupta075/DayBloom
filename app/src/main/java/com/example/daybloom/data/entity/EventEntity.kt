package com.example.daybloom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,          // yyyy-MM-dd
    val title: String,
    val description: String,
    val isBirthday: Boolean = false
)
