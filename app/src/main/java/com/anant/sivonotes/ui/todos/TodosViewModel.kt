package com.anant.sivonotes.ui.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.TodosRepository
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.domain.streak.StreakStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TodoTab {
    TODAY,
    UPCOMING,
    COMPLETED
}

data class TodosUiState(
    val todayTodos: List<TodoEntity> = emptyList(),
    val upcomingTodos: List<TodoEntity> = emptyList(),
    val completedTodos: List<TodoEntity> = emptyList(),
    val foldersMap: Map<Long, FolderEntity> = emptyMap(),
    val allFolders: List<FolderEntity> = emptyList(),
    val streakStats: StreakStats = StreakStats(),
    val activeTab: TodoTab = TodoTab.TODAY,
    val selectedFolderId: Long? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class TodosViewModel(
    private val todosRepository: TodosRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val _activeTab = MutableStateFlow(TodoTab.TODAY)
    val activeTab: StateFlow<TodoTab> = _activeTab.asStateFlow()

    private val _selectedFolderId = MutableStateFlow<Long?>(null)
    val selectedFolderId: StateFlow<Long?> = _selectedFolderId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<TodosUiState> = combine(
        todosRepository.getAllTodos(),
        foldersRepository.getAllFolders(),
        _activeTab,
        _selectedFolderId,
        _searchQuery
    ) { allTodos, allFolders, tab, folderId, query ->
        val foldersMap = allFolders.associateBy { it.id }
        val streakStats = StreakEngine.calculateStats(allTodos)

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStartMillis = cal.timeInMillis
        val todayEndMillis = todayStartMillis + 24 * 60 * 60 * 1000 - 1

        val filtered = allTodos.filter { todo ->
            val matchesFolder = folderId == null || todo.folderId == folderId
            val matchesQuery = query.isBlank() ||
                    todo.title.contains(query, ignoreCase = true) ||
                    todo.description.contains(query, ignoreCase = true)
            matchesFolder && matchesQuery
        }

        val todayList = filtered.filter {
            !it.isCompleted && (it.dueDate == null || it.dueDate <= todayEndMillis)
        }

        val upcomingList = filtered.filter {
            !it.isCompleted && it.dueDate != null && it.dueDate > todayEndMillis
        }

        val completedList = filtered.filter { it.isCompleted }

        TodosUiState(
            todayTodos = todayList,
            upcomingTodos = upcomingList,
            completedTodos = completedList,
            foldersMap = foldersMap,
            allFolders = allFolders,
            streakStats = streakStats,
            activeTab = tab,
            selectedFolderId = folderId,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodosUiState(isLoading = true)
    )

    fun setActiveTab(tab: TodoTab) {
        _activeTab.value = tab
    }

    fun setSelectedFolder(folderId: Long?) {
        _selectedFolderId.value = folderId
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleTodoCompleted(todo: TodoEntity) {
        viewModelScope.launch {
            todosRepository.toggleTodoCompleted(todo)
        }
    }

    fun createTodo(
        title: String,
        description: String,
        dueDate: Long?,
        dueTime: String?,
        priority: String,
        repeatRule: String,
        folderId: Long?
    ) {
        viewModelScope.launch {
            todosRepository.insertTodo(
                TodoEntity(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    priority = priority,
                    repeatRule = repeatRule,
                    folderId = folderId,
                    isCompleted = false
                )
            )
        }
    }

    fun updateTodo(
        todo: TodoEntity,
        title: String,
        description: String,
        dueDate: Long?,
        dueTime: String?,
        priority: String,
        repeatRule: String,
        folderId: Long?
    ) {
        viewModelScope.launch {
            todosRepository.updateTodo(
                todo.copy(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    priority = priority,
                    repeatRule = repeatRule,
                    folderId = folderId,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todosRepository.deleteTodo(todo)
        }
    }

    companion object {
        fun provideFactory(
            todosRepository: TodosRepository,
            foldersRepository: FoldersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TodosViewModel(todosRepository, foldersRepository) as T
            }
        }
    }
}
