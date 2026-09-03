package com.anant.sivonotes.ui.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import com.anant.sivonotes.data.repository.NotesRepository
import com.anant.sivonotes.data.repository.TodosRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FolderDetailUiState(
    val folder: FolderEntity? = null,
    val notes: List<NoteEntity> = emptyList(),
    val todos: List<TodoEntity> = emptyList(),
    val points: List<ImportantPointEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleted: Boolean = false
)

class FolderDetailViewModel(
    private val folderId: Long,
    private val foldersRepository: FoldersRepository,
    private val notesRepository: NotesRepository,
    private val todosRepository: TodosRepository,
    private val pointsRepository: ImportantPointsRepository
) : ViewModel() {

    val uiState: StateFlow<FolderDetailUiState> = combine(
        foldersRepository.getFolderById(folderId),
        notesRepository.getNotesByFolder(folderId),
        todosRepository.getTodosByFolder(folderId),
        pointsRepository.getPointsByFolder(folderId)
    ) { folder, notes, todos, points ->
        FolderDetailUiState(
            folder = folder,
            notes = notes,
            todos = todos,
            points = points,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FolderDetailUiState(isLoading = true)
    )

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

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            notesRepository.togglePin(note)
        }
    }

    fun updateFolder(newName: String, newIcon: String, newColor: String) {
        val currentFolder = uiState.value.folder ?: return
        viewModelScope.launch {
            foldersRepository.updateFolder(
                currentFolder.copy(
                    name = newName.trim(),
                    iconName = newIcon,
                    colorHex = newColor,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteFolder() {
        val currentFolder = uiState.value.folder ?: return
        viewModelScope.launch {
            foldersRepository.deleteFolder(currentFolder)
        }
    }

    companion object {
        fun provideFactory(
            folderId: Long,
            foldersRepository: FoldersRepository,
            notesRepository: NotesRepository,
            todosRepository: TodosRepository,
            pointsRepository: ImportantPointsRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FolderDetailViewModel(
                    folderId,
                    foldersRepository,
                    notesRepository,
                    todosRepository,
                    pointsRepository
                ) as T
            }
        }
    }
}
