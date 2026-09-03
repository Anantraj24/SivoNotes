package com.anant.sivonotes.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.backup.BackupRestoreManager
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import com.anant.sivonotes.data.repository.NotesRepository
import com.anant.sivonotes.data.repository.TodosRepository
import com.anant.sivonotes.security.VaultManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val isBiometricEnabled: Boolean = true,
    val notesCount: Int = 0,
    val todosCount: Int = 0,
    val pointsCount: Int = 0,
    val isFirstLaunchCompleted: Boolean = true,
    val backupStatusMessage: String? = null,
    val isProcessing: Boolean = false
)

private data class StorageCounts(
    val notesCount: Int = 0,
    val todosCount: Int = 0,
    val pointsCount: Int = 0
)

class SettingsViewModel(
    private val context: Context,
    private val vaultManager: VaultManager,
    private val backupRestoreManager: BackupRestoreManager,
    private val notesRepository: NotesRepository,
    private val todosRepository: TodosRepository,
    private val pointsRepository: ImportantPointsRepository
) : ViewModel() {

    private val prefs = context.getSharedPreferences("sivo_settings_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
    private val _backupStatus = MutableStateFlow<String?>(null)
    private val _isProcessing = MutableStateFlow(false)

    private val countsFlow = combine(
        notesRepository.getAllNotes(),
        todosRepository.getAllTodos(),
        pointsRepository.getAllPoints()
    ) { notes, todos, points ->
        StorageCounts(notes.size, todos.size, points.size)
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        _themeMode,
        _backupStatus,
        _isProcessing,
        countsFlow
    ) { theme, status, processing, counts ->
        SettingsUiState(
            themeMode = theme,
            isBiometricEnabled = vaultManager.isBiometricEnabled(),
            notesCount = counts.notesCount,
            todosCount = counts.todosCount,
            pointsCount = counts.pointsCount,
            isFirstLaunchCompleted = prefs.getBoolean(KEY_FIRST_LAUNCH_COMPLETED, false),
            backupStatusMessage = status,
            isProcessing = processing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        vaultManager.setBiometricEnabled(enabled)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH_COMPLETED, true).apply()
    }

    fun exportBackup(uri: Uri) {
        _isProcessing.value = true
        _backupStatus.value = null
        viewModelScope.launch {
            val result = backupRestoreManager.exportBackupToUri(uri)
            _isProcessing.value = false
            if (result.isSuccess) {
                _backupStatus.value = "Backup exported successfully!"
            } else {
                _backupStatus.value = "Export failed: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun importBackup(uri: Uri) {
        _isProcessing.value = true
        _backupStatus.value = null
        viewModelScope.launch {
            val result = backupRestoreManager.importBackupFromUri(uri)
            _isProcessing.value = false
            if (result.isSuccess) {
                _backupStatus.value = "Restored ${result.getOrNull()} items successfully!"
            } else {
                _backupStatus.value = "Restore failed: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun clearStatusMessage() {
        _backupStatus.value = null
    }

    companion object {
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_FIRST_LAUNCH_COMPLETED = "first_launch_completed"

        fun provideFactory(
            context: Context,
            vaultManager: VaultManager,
            backupRestoreManager: BackupRestoreManager,
            notesRepository: NotesRepository,
            todosRepository: TodosRepository,
            pointsRepository: ImportantPointsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    context,
                    vaultManager,
                    backupRestoreManager,
                    notesRepository,
                    todosRepository,
                    pointsRepository
                ) as T
            }
        }
    }
}
