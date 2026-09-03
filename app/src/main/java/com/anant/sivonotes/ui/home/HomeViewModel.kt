package com.anant.sivonotes.ui.home

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
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.domain.streak.StreakStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val greeting: String = "Welcome back",
    val recentNotes: List<NoteEntity> = emptyList(),
    val todayTodos: List<TodoEntity> = emptyList(),
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0,
    val nextReminder: ReminderEntity? = null,
    val foldersMap: Map<Long, FolderEntity> = emptyMap(),
    val streakStats: StreakStats = StreakStats(),
    val totalNotesCount: Int = 0,
    val totalPointsCount: Int = 0,
    val totalFoldersCount: Int = 0,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val notesRepository: NotesRepository,
    private val todosRepository: TodosRepository,
    private val remindersRepository: RemindersRepository,
    private val foldersRepository: FoldersRepository,
    private val pointsRepository: ImportantPointsRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        notesRepository.getAllNotes(),
        todosRepository.getAllTodos(),
        remindersRepository.getAllReminders(),
        foldersRepository.getAllFolders(),
        pointsRepository.getAllPoints()
    ) { notes, todos, reminders, folders, points ->
        val greeting = calculateGreeting()
        val foldersMap = folders.associateBy { it.id }
        val streakStats = StreakEngine.calculateStats(todos)

        // Recent Notes (up to 4)
        val recentNotes = notes.take(4)

        // Today's Todos
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis
        val todayEnd = todayStart + 24 * 60 * 60 * 1000 - 1

        val todayTodos = todos.filter {
            it.dueDate == null || it.dueDate <= todayEnd
        }
        val todayCompleted = todayTodos.count { it.isCompleted }

        // Next upcoming reminder
        val now = System.currentTimeMillis()
        val nextReminder = reminders
            .filter { !it.isCompleted && it.targetTimeMillis > now }
            .minByOrNull { it.targetTimeMillis }

        HomeUiState(
            greeting = greeting,
            recentNotes = recentNotes,
            todayTodos = todayTodos.filter { !it.isCompleted }.take(3),
            todayCompletedCount = todayCompleted,
            todayTotalCount = todayTodos.size,
            nextReminder = nextReminder,
            foldersMap = foldersMap,
            streakStats = streakStats,
            totalNotesCount = notes.size,
            totalPointsCount = points.size,
            totalFoldersCount = folders.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun toggleTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todosRepository.toggleTodoCompleted(todo)
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            notesRepository.togglePin(note)
        }
    }

    private fun calculateGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..11 -> "Good morning ☀️"
            in 12..16 -> "Good afternoon 🌤️"
            in 17..21 -> "Good evening 🌆"
            else -> "Night owl mode 🌙"
        }
    }

    companion object {
        fun provideFactory(
            notesRepository: NotesRepository,
            todosRepository: TodosRepository,
            remindersRepository: RemindersRepository,
            foldersRepository: FoldersRepository,
            pointsRepository: ImportantPointsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    notesRepository,
                    todosRepository,
                    remindersRepository,
                    foldersRepository,
                    pointsRepository
                ) as T
            }
        }
    }
}
