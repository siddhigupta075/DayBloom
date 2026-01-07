package com.example.daybloom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.daybloom.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserEntity?
}
