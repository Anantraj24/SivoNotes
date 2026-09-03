package com.anant.sivonotes.ui.notes.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.NotesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteEditorUiState(
    val noteId: Long = 0,
    val title: String = "",
    val content: String = "",
    val folderId: Long? = null,
    val isPinned: Boolean = false,
    val tags: List<String> = emptyList(),
    val colorHex: String? = null,
    val saveStatus: String = "Saved",
    val isLoaded: Boolean = false,
    val isDeleted: Boolean = false
)

class NoteEditorViewModel(
    private val initialNoteId: Long,
    private val initialFolderId: Long?,
    private val notesRepository: NotesRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteEditorUiState(
        noteId = if (initialNoteId > 0) initialNoteId else 0,
        folderId = if (initialFolderId != null && initialFolderId > 0) initialFolderId else null
    ))
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    val allFolders: StateFlow<List<FolderEntity>> = foldersRepository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var autoSaveJob: Job? = null

    init {
        if (initialNoteId > 0) {
            loadNote(initialNoteId)
        } else {
            _uiState.value = _uiState.value.copy(isLoaded = true)
        }
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            val note = notesRepository.getNoteByIdDirect(id)
            if (note != null) {
                _uiState.value = _uiState.value.copy(
                    noteId = note.id,
                    title = note.title,
                    content = note.content,
                    folderId = note.folderId,
                    isPinned = note.isPinned,
                    tags = note.tags,
                    colorHex = note.colorHex,
                    isLoaded = true,
                    saveStatus = "Saved"
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoaded = true)
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(
            title = newTitle,
            saveStatus = "Saving..."
        )
        triggerAutoSave()
    }

    fun onContentChange(newContent: String) {
        _uiState.value = _uiState.value.copy(
            content = newContent,
            saveStatus = "Saving..."
        )
        triggerAutoSave()
    }

    fun setFolder(folderId: Long?) {
        _uiState.value = _uiState.value.copy(
            folderId = folderId,
            saveStatus = "Saving..."
        )
        triggerAutoSave()
    }

    fun togglePin() {
        _uiState.value = _uiState.value.copy(
            isPinned = !_uiState.value.isPinned,
            saveStatus = "Saving..."
        )
        triggerAutoSave()
    }

    fun addTag(tag: String) {
        val cleanTag = tag.trim().replace("#", "")
        if (cleanTag.isNotBlank() && !_uiState.value.tags.contains(cleanTag)) {
            val updatedTags = _uiState.value.tags + cleanTag
            _uiState.value = _uiState.value.copy(
                tags = updatedTags,
                saveStatus = "Saving..."
            )
            triggerAutoSave()
        }
    }

    fun removeTag(tag: String) {
        val updatedTags = _uiState.value.tags.filter { it != tag }
        _uiState.value = _uiState.value.copy(
            tags = updatedTags,
            saveStatus = "Saving..."
        )
        triggerAutoSave()
    }

    private fun triggerAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(400) // Debounce auto-save
            saveNoteDirect()
        }
    }

    fun saveNoteDirect() {
        val state = _uiState.value
        // Don't save empty blank note if it was never created
        if (state.title.isBlank() && state.content.isBlank() && state.noteId == 0L) {
            _uiState.value = state.copy(saveStatus = "Saved")
            return
        }

        viewModelScope.launch {
            val noteEntity = NoteEntity(
                id = state.noteId,
                title = state.title,
                content = state.content,
                folderId = state.folderId,
                isPinned = state.isPinned,
                tags = state.tags,
                colorHex = state.colorHex,
                updatedAt = System.currentTimeMillis()
            )

            if (state.noteId == 0L) {
                val newId = notesRepository.insertNote(noteEntity)
                _uiState.value = _uiState.value.copy(noteId = newId, saveStatus = "Saved")
            } else {
                notesRepository.updateNote(noteEntity)
                _uiState.value = _uiState.value.copy(saveStatus = "Saved")
            }
        }
    }

    fun deleteNote() {
        val state = _uiState.value
        if (state.noteId > 0) {
            viewModelScope.launch {
                notesRepository.deleteNoteById(state.noteId)
                _uiState.value = _uiState.value.copy(isDeleted = true)
            }
        } else {
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }

    companion object {
        fun provideFactory(
            noteId: Long,
            folderId: Long?,
            notesRepository: NotesRepository,
            foldersRepository: FoldersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteEditorViewModel(noteId, folderId, notesRepository, foldersRepository) as T
            }
        }
    }
}
