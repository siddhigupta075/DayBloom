package com.example.daybloom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.daybloom.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: TaskEntity)


    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isCompleted = 1 WHERE id = :taskId")
    suspend fun markTaskCompleted(taskId: Int)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): kotlinx.coroutines.flow.Flow<Int>

    @Query("""
    SELECT COUNT(*) FROM tasks 
    WHERE isCompleted = 1 
    AND dueDate >= :weekStart
""")
    fun getCompletedTasksThisWeek(weekStart: Long): kotlinx.coroutines.flow.Flow<Int>


}
