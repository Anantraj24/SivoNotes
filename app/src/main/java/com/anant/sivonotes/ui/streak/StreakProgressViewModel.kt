package com.anant.sivonotes.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FocusSessionEntity
import com.anant.sivonotes.data.repository.FocusSessionRepository
import com.anant.sivonotes.data.repository.TodosRepository
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.domain.streak.StreakStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class StreakProgressUiState(
    val streakStats: StreakStats = StreakStats(),
    val recentFocusSessions: List<FocusSessionEntity> = emptyList(),
    val isLoading: Boolean = false
)

class StreakProgressViewModel(
    private val todosRepository: TodosRepository,
    private val focusSessionRepository: FocusSessionRepository? = null
) : ViewModel() {

    private val sessionsFlow = focusSessionRepository?.getAllSessions() ?: flowOf(emptyList())

    val uiState: StateFlow<StreakProgressUiState> = combine(
        todosRepository.getAllTodos(),
        sessionsFlow
    ) { allTodos, allSessions ->
        val stats = StreakEngine.calculateStats(allTodos, allSessions)
        val completedSessions = allSessions.filter { it.isCompleted && !it.isAbandoned }
        StreakProgressUiState(
            streakStats = stats,
            recentFocusSessions = completedSessions.take(10),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StreakProgressUiState(isLoading = true)
    )

    companion object {
        fun provideFactory(
            todosRepository: TodosRepository,
            focusSessionRepository: FocusSessionRepository? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StreakProgressViewModel(todosRepository, focusSessionRepository) as T
            }
        }
    }
}
