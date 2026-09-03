package com.anant.sivonotes.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.repository.TodosRepository
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.domain.streak.StreakStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StreakProgressUiState(
    val streakStats: StreakStats = StreakStats(),
    val isLoading: Boolean = false
)

class StreakProgressViewModel(
    private val todosRepository: TodosRepository
) : ViewModel() {

    val uiState: StateFlow<StreakProgressUiState> = todosRepository.getAllTodos()
        .map { allTodos ->
            val stats = StreakEngine.calculateStats(allTodos)
            StreakProgressUiState(streakStats = stats, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StreakProgressUiState(isLoading = true)
        )

    companion object {
        fun provideFactory(
            todosRepository: TodosRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StreakProgressViewModel(todosRepository) as T
            }
        }
    }
}
