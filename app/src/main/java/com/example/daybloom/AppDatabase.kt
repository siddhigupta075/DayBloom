package com.example.daybloom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.daybloom.data.dao.HabitDao
import com.example.daybloom.data.dao.TaskDao
import com.example.daybloom.data.entity.HabitEntity
import com.example.daybloom.data.dao.EventDao
import com.example.daybloom.data.dao.UserDao
import com.example.daybloom.data.entity.TaskEntity
import com.example.daybloom.data.entity.EventEntity
import com.example.daybloom.data.entity.UserEntity

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        EventEntity::class,
        UserEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // ✅ DAO functions
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao


    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daybloom_db"
                )
                    // during development only
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
