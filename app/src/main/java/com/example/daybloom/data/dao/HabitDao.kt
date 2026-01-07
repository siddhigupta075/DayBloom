package com.example.daybloom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.daybloom.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insertHabit(habit: HabitEntity): Long

    @Query("""
        UPDATE habits 
        SET streak = :newStreak, lastCompletedDate = :date 
        WHERE id = :habitId
    """)
    suspend fun updateHabitCompletion(
        habitId: Int,
        newStreak: Int,
        date: String
    )

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Int)

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT COUNT(*) FROM habits")
    fun getTotalHabits(): Flow<Int>

    @Query("SELECT COUNT(*) FROM habits WHERE lastCompletedDate = :today")
    fun getCompletedHabitsToday(today: String): Flow<Int>

    @Query("SELECT MAX(streak) FROM habits")
    fun getHighestHabitStreak(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM habits WHERE lastCompletedDate IS NOT NULL")
    fun getActiveHabits(): Flow<Int>
}
