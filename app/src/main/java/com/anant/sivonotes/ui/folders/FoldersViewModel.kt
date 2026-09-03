package com.anant.sivonotes.ui.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import com.anant.sivonotes.data.repository.NotesRepository
import com.anant.sivonotes.data.repository.TodosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FolderWithCounts(
    val folder: FolderEntity,
    val noteCount: Int = 0,
    val todoCount: Int = 0,
    val pointCount: Int = 0
)

data class FoldersUiState(
    val foldersWithCounts: List<FolderWithCounts> = emptyList(),
    val isLoading: Boolean = false
)

class FoldersViewModel(
    private val foldersRepository: FoldersRepository,
    private val notesRepository: NotesRepository,
    private val todosRepository: TodosRepository,
    private val pointsRepository: ImportantPointsRepository
) : ViewModel() {

    val uiState: StateFlow<FoldersUiState> = combine(
        foldersRepository.getAllFolders(),
        notesRepository.getAllNotes(),
        todosRepository.getAllTodos(),
        pointsRepository.getAllPoints()
    ) { folders, notes, todos, points ->
        val list = folders.map { folder ->
            FolderWithCounts(
                folder = folder,
                noteCount = notes.count { it.folderId == folder.id },
                todoCount = todos.count { it.folderId == folder.id && !it.isCompleted },
                pointCount = points.count { it.folderId == folder.id }
            )
        }
        FoldersUiState(foldersWithCounts = list, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FoldersUiState(isLoading = true)
    )

    fun createFolder(name: String, iconName: String, colorHex: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                foldersRepository.insertFolder(
                    FolderEntity(
                        name = name.trim(),
                        iconName = iconName,
                        colorHex = colorHex
                    )
                )
            }
        }
    }

    fun updateFolder(folder: FolderEntity, newName: String, newIcon: String, newColor: String) {
        if (newName.isNotBlank()) {
            viewModelScope.launch {
                foldersRepository.updateFolder(
                    folder.copy(
                        name = newName.trim(),
                        iconName = newIcon,
                        colorHex = newColor,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun deleteFolder(folder: FolderEntity) {
        viewModelScope.launch {
            foldersRepository.deleteFolder(folder)
        }
    }

    companion object {
        fun provideFactory(
            foldersRepository: FoldersRepository,
            notesRepository: NotesRepository,
            todosRepository: TodosRepository,
            pointsRepository: ImportantPointsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FoldersViewModel(
                    foldersRepository,
                    notesRepository,
                    todosRepository,
                    pointsRepository
                ) as T
            }
        }
    }
}
