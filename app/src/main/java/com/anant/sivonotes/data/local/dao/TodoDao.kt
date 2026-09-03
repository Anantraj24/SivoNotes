package com.anant.sivonotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anant.sivonotes.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, dueDate ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE dueDate >= :startOfDay AND dueDate <= :endOfDay ORDER BY isCompleted ASC, createdAt DESC")
    fun getTodayTodos(startOfDay: Long, endOfDay: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE (dueDate IS NULL OR dueDate >= :fromTime) AND isCompleted = 0 ORDER BY dueDate ASC, createdAt DESC")
    fun getActiveUpcomingTodos(fromTime: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE folderId = :folderId ORDER BY isCompleted ASC, createdAt DESC")
    fun getTodosByFolder(folderId: Long): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getTodoById(id: Long): Flow<TodoEntity?>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoByIdDirect(id: Long): TodoEntity?

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY isCompleted ASC, createdAt DESC")
    fun searchTodos(query: String): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)

    @Query("SELECT * FROM todos WHERE isCompleted = 1 AND completedAt IS NOT NULL ORDER BY completedAt ASC")
    fun getCompletedTodosHistory(): Flow<List<TodoEntity>>

    @Query("SELECT COUNT(*) FROM todos WHERE folderId = :folderId")
    fun getTodoCountByFolder(folderId: Long): Flow<Int>
}
