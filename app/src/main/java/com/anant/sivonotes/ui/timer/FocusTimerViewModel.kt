package com.anant.sivonotes.ui.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FocusSessionEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.repository.FocusSessionRepository
import com.anant.sivonotes.domain.streak.StreakEngine
import com.anant.sivonotes.domain.timer.FocusTimerState
import com.anant.sivonotes.notification.FocusTimerNotification
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusTimerViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    private val context: Context
) : ViewModel() {

    private val _timerState = MutableStateFlow<FocusTimerState>(FocusTimerState.Idle)
    val timerState: StateFlow<FocusTimerState> = _timerState.asStateFlow()

    private val _todoTitle = MutableStateFlow("Focus Session")
    val todoTitle: StateFlow<String> = _todoTitle.asStateFlow()

    private val _selectedMinutes = MutableStateFlow(25)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()

    private var currentTodoId: Long? = null
    private var targetDurationMillis: Long = 25 * 60 * 1000L
    private var remainingMillis: Long = targetDurationMillis
    private var sessionStartTimeMillis: Long = 0L
    private var tickerJob: Job? = null

    fun selectTodo(todo: TodoEntity?) {
        currentTodoId = todo?.id
        _todoTitle.value = todo?.title?.ifBlank { "Focus Session" } ?: "Focus Session"
    }

    fun setDurationMinutes(minutes: Int) {
        if (_timerState.value is FocusTimerState.Running || _timerState.value is FocusTimerState.Paused) {
            return
        }
        _selectedMinutes.value = minutes
        targetDurationMillis = minutes * 60 * 1000L
        remainingMillis = targetDurationMillis
    }

    fun startTimer() {
        tickerJob?.cancel()
        targetDurationMillis = _selectedMinutes.value * 60 * 1000L
        remainingMillis = targetDurationMillis
        sessionStartTimeMillis = System.currentTimeMillis()

        _timerState.value = FocusTimerState.Running(remainingMillis, targetDurationMillis)
        startTicker()
    }

    fun pauseTimer() {
        tickerJob?.cancel()
        _timerState.value = FocusTimerState.Paused(remainingMillis, targetDurationMillis)
    }

    fun resumeTimer() {
        if (_timerState.value is FocusTimerState.Paused) {
            _timerState.value = FocusTimerState.Running(remainingMillis, targetDurationMillis)
            startTicker()
        }
    }

    private fun startTicker() {
        tickerJob = viewModelScope.launch {
            while (isActive && remainingMillis > 0) {
                FocusTimerNotification.showRunningNotification(
                    context = context,
                    taskTitle = _todoTitle.value,
                    remainingFormatted = formatRemainingTime(remainingMillis)
                )

                delay(1000L)
                remainingMillis -= 1000L

                if (remainingMillis <= 0) {
                    remainingMillis = 0
                    handleTimerCompleted()
                    break
                } else {
                    _timerState.value = FocusTimerState.Running(remainingMillis, targetDurationMillis)
                }
            }
        }
    }

    private suspend fun handleTimerCompleted() {
        val completedAt = System.currentTimeMillis()
        val session = FocusSessionEntity(
            todoId = currentTodoId,
            todoTitle = _todoTitle.value,
            targetDurationMillis = targetDurationMillis,
            actualDurationMillis = targetDurationMillis,
            startedAt = sessionStartTimeMillis,
            completedAt = completedAt,
            isCompleted = true,
            isAbandoned = false,
            dateKey = StreakEngine.toEpochDay(completedAt)
        )
        focusSessionRepository.insertSession(session)

        FocusTimerNotification.dismissNotification(context)
        FocusTimerNotification.showCompletedNotification(
            context = context,
            taskTitle = _todoTitle.value,
            durationMinutes = _selectedMinutes.value
        )

        _timerState.value = FocusTimerState.Completed(
            totalMillis = targetDurationMillis,
            actualMillis = targetDurationMillis
        )
    }

    fun stopAndSave() {
        tickerJob?.cancel()
        FocusTimerNotification.dismissNotification(context)

        val completedAt = System.currentTimeMillis()
        val actualDuration = (targetDurationMillis - remainingMillis).coerceAtLeast(1000L)

        viewModelScope.launch {
            val session = FocusSessionEntity(
                todoId = currentTodoId,
                todoTitle = _todoTitle.value,
                targetDurationMillis = targetDurationMillis,
                actualDurationMillis = actualDuration,
                startedAt = sessionStartTimeMillis,
                completedAt = completedAt,
                isCompleted = true,
                isAbandoned = false,
                dateKey = StreakEngine.toEpochDay(completedAt)
            )
            focusSessionRepository.insertSession(session)

            val actualMinutes = ((actualDuration / (1000 * 60)).toInt()).coerceAtLeast(1)
            FocusTimerNotification.showCompletedNotification(
                context = context,
                taskTitle = _todoTitle.value,
                durationMinutes = actualMinutes
            )

            _timerState.value = FocusTimerState.Completed(
                totalMillis = targetDurationMillis,
                actualMillis = actualDuration
            )
        }
    }

    fun abandonTimer() {
        tickerJob?.cancel()
        FocusTimerNotification.dismissNotification(context)

        if (sessionStartTimeMillis > 0 && remainingMillis < targetDurationMillis) {
            val abandonedDuration = targetDurationMillis - remainingMillis
            viewModelScope.launch {
                val session = FocusSessionEntity(
                    todoId = currentTodoId,
                    todoTitle = _todoTitle.value,
                    targetDurationMillis = targetDurationMillis,
                    actualDurationMillis = abandonedDuration,
                    startedAt = sessionStartTimeMillis,
                    completedAt = System.currentTimeMillis(),
                    isCompleted = false,
                    isAbandoned = true,
                    dateKey = StreakEngine.toEpochDay(System.currentTimeMillis())
                )
                focusSessionRepository.insertSession(session)
            }
        }

        _timerState.value = FocusTimerState.Idle
        remainingMillis = targetDurationMillis
    }

    fun resetToIdle() {
        tickerJob?.cancel()
        FocusTimerNotification.dismissNotification(context)
        _timerState.value = FocusTimerState.Idle
        remainingMillis = targetDurationMillis
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
        FocusTimerNotification.dismissNotification(context)
    }

    private fun formatRemainingTime(millis: Long): String {
        val totalSecs = (millis / 1000).coerceAtLeast(0)
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return "%02d:%02d".format(mins, secs)
    }

    companion object {
        fun provideFactory(
            focusSessionRepository: FocusSessionRepository,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FocusTimerViewModel(focusSessionRepository, context.applicationContext) as T
            }
        }
    }
}
