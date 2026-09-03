package com.anant.sivonotes.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.ReminderEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import com.anant.sivonotes.data.repository.NotesRepository
import com.anant.sivonotes.data.repository.RemindersRepository
import com.anant.sivonotes.data.repository.TodosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SearchFilterType {
    ALL,
    NOTES,
    TASKS,
    POINTS,
    FOLDERS
}

data class SearchUiState(
    val query: String = "",
    val filterType: SearchFilterType = SearchFilterType.ALL,
    val matchedNotes: List<NoteEntity> = emptyList(),
    val matchedTodos: List<TodoEntity> = emptyList(),
    val matchedPoints: List<ImportantPointEntity> = emptyList(),
    val matchedFolders: List<FolderEntity> = emptyList(),
    val matchedReminders: List<ReminderEntity> = emptyList(),
    val foldersMap: Map<Long, FolderEntity> = emptyMap(),
    val totalMatchesCount: Int = 0
)

class SearchViewModel(
    private val notesRepository: NotesRepository,
    private val todosRepository: TodosRepository,
    private val pointsRepository: ImportantPointsRepository,
    private val foldersRepository: FoldersRepository,
    private val remindersRepository: RemindersRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filterType = MutableStateFlow(SearchFilterType.ALL)
    val filterType: StateFlow<SearchFilterType> = _filterType.asStateFlow()

    // Combine data sources first
    private val allDataFlow = combine(
        notesRepository.getAllNotes(),
        todosRepository.getAllTodos(),
        pointsRepository.getAllPoints(),
        foldersRepository.getAllFolders(),
        remindersRepository.getAllReminders()
    ) { notes, todos, points, folders, reminders ->
        DataSnapshot(notes, todos, points, folders, reminders)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        _filterType,
        allDataFlow
    ) { q, filter, data ->
        val trimmed = q.trim()
        val foldersMap = data.folders.associateBy { it.id }

        if (trimmed.isEmpty()) {
            return@combine SearchUiState(
                query = q,
                filterType = filter,
                foldersMap = foldersMap
            )
        }

        val matchNotes = if (filter == SearchFilterType.ALL || filter == SearchFilterType.NOTES) {
            data.notes.filter {
                it.title.contains(trimmed, ignoreCase = true) ||
                        it.content.contains(trimmed, ignoreCase = true) ||
                        it.tags.any { tag -> tag.contains(trimmed, ignoreCase = true) }
            }
        } else emptyList()

        val matchTodos = if (filter == SearchFilterType.ALL || filter == SearchFilterType.TASKS) {
            data.todos.filter {
                it.title.contains(trimmed, ignoreCase = true) ||
                        it.description.contains(trimmed, ignoreCase = true)
            }
        } else emptyList()

        val matchPoints = if (filter == SearchFilterType.ALL || filter == SearchFilterType.POINTS) {
            data.points.filter {
                it.text.contains(trimmed, ignoreCase = true)
            }
        } else emptyList()

        val matchFolders = if (filter == SearchFilterType.ALL || filter == SearchFilterType.FOLDERS) {
            data.folders.filter {
                it.name.contains(trimmed, ignoreCase = true)
            }
        } else emptyList()

        val matchReminders = if (filter == SearchFilterType.ALL) {
            data.reminders.filter {
                it.title.contains(trimmed, ignoreCase = true) ||
                        it.note.contains(trimmed, ignoreCase = true)
            }
        } else emptyList()

        val total = matchNotes.size + matchTodos.size + matchPoints.size + matchFolders.size + matchReminders.size

        SearchUiState(
            query = q,
            filterType = filter,
            matchedNotes = matchNotes,
            matchedTodos = matchTodos,
            matchedPoints = matchPoints,
            matchedFolders = matchFolders,
            matchedReminders = matchReminders,
            foldersMap = foldersMap,
            totalMatchesCount = total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun setFilterType(type: SearchFilterType) {
        _filterType.value = type
    }

    fun toggleTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todosRepository.toggleTodoCompleted(todo)
        }
    }

    fun togglePoint(point: ImportantPointEntity) {
        viewModelScope.launch {
            pointsRepository.togglePointCompleted(point)
        }
    }

    private data class DataSnapshot(
        val notes: List<NoteEntity>,
        val todos: List<TodoEntity>,
        val points: List<ImportantPointEntity>,
        val folders: List<FolderEntity>,
        val reminders: List<ReminderEntity>
    )

    companion object {
        fun provideFactory(
            notesRepository: NotesRepository,
            todosRepository: TodosRepository,
            pointsRepository: ImportantPointsRepository,
            foldersRepository: FoldersRepository,
            remindersRepository: RemindersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    notesRepository,
                    todosRepository,
                    pointsRepository,
                    foldersRepository,
                    remindersRepository
                ) as T
            }
        }
    }
}
