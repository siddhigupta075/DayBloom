package com.example.daybloom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val gender: String,
    val birthday: String
)
