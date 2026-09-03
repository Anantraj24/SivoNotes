package com.anant.sivonotes.ui.reminders

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.ReminderEntity
import com.anant.sivonotes.data.repository.RemindersRepository
import com.anant.sivonotes.notification.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RemindersUiState(
    val reminders: List<ReminderEntity> = emptyList(),
    val isLoading: Boolean = false
)

class RemindersViewModel(
    private val remindersRepository: RemindersRepository,
    private val context: Context
) : ViewModel() {

    val uiState: StateFlow<RemindersUiState> = remindersRepository.getAllReminders()
        .map { list ->
            RemindersUiState(reminders = list, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RemindersUiState(isLoading = true)
        )

    fun createReminder(
        title: String,
        note: String,
        targetTimeMillis: Long,
        repeatRule: String
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title.trim(),
                note = note.trim(),
                targetTimeMillis = targetTimeMillis,
                repeatRule = repeatRule,
                isCompleted = false
            )
            val newId = remindersRepository.insertReminder(reminder)
            AlarmScheduler.schedule(context, reminder.copy(id = newId))
        }
    }

    fun markCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            remindersRepository.markCompleted(reminder)
            AlarmScheduler.cancel(context, reminder.id)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            remindersRepository.deleteReminder(reminder)
            AlarmScheduler.cancel(context, reminder.id)
        }
    }

    companion object {
        fun provideFactory(
            remindersRepository: RemindersRepository,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RemindersViewModel(remindersRepository, context) as T
            }
        }
    }
}
