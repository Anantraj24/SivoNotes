package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.TodoDao
import com.anant.sivonotes.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodosRepository(private val todoDao: TodoDao) {
    fun getAllTodos(): Flow<List<TodoEntity>> = todoDao.getAllTodos()
    fun getTodayTodos(startOfDay: Long, endOfDay: Long): Flow<List<TodoEntity>> = todoDao.getTodayTodos(startOfDay, endOfDay)
    fun getActiveUpcomingTodos(fromTime: Long): Flow<List<TodoEntity>> = todoDao.getActiveUpcomingTodos(fromTime)
    fun getCompletedTodos(): Flow<List<TodoEntity>> = todoDao.getCompletedTodos()
    fun getTodosByFolder(folderId: Long): Flow<List<TodoEntity>> = todoDao.getTodosByFolder(folderId)
    fun getTodoById(id: Long): Flow<TodoEntity?> = todoDao.getTodoById(id)
    suspend fun getTodoByIdDirect(id: Long): TodoEntity? = todoDao.getTodoByIdDirect(id)
    fun searchTodos(query: String): Flow<List<TodoEntity>> = todoDao.searchTodos(query)
    suspend fun insertTodo(todo: TodoEntity): Long = todoDao.insertTodo(todo)
    suspend fun updateTodo(todo: TodoEntity) = todoDao.updateTodo(todo)
    suspend fun deleteTodo(todo: TodoEntity) = todoDao.deleteTodo(todo)
    suspend fun deleteTodoById(id: Long) = todoDao.deleteTodoById(id)
    fun getCompletedTodosHistory(): Flow<List<TodoEntity>> = todoDao.getCompletedTodosHistory()

    suspend fun toggleTodoCompleted(todo: TodoEntity) {
        val isNowCompleted = !todo.isCompleted
        val completedTimestamp = if (isNowCompleted) System.currentTimeMillis() else null
        todoDao.updateTodo(
            todo.copy(
                isCompleted = isNowCompleted,
                completedAt = completedTimestamp,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
