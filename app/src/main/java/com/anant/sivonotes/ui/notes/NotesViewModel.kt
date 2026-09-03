package com.anant.sivonotes.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NoteFilter {
    ALL,
    PINNED,
    RECENT
}

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val foldersMap: Map<Long, FolderEntity> = emptyMap(),
    val activeFilter: NoteFilter = NoteFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val _activeFilter = MutableStateFlow(NoteFilter.ALL)
    val activeFilter: StateFlow<NoteFilter> = _activeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<NotesUiState> = combine(
        notesRepository.getAllNotes(),
        foldersRepository.getAllFolders(),
        _activeFilter,
        _searchQuery
    ) { allNotes, allFolders, filter, query ->
        val foldersMap = allFolders.associateBy { it.id }

        val filteredNotes = when {
            query.isNotBlank() -> {
                allNotes.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.content.contains(query, ignoreCase = true) ||
                            it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                }
            }
            filter == NoteFilter.PINNED -> allNotes.filter { it.isPinned }
            filter == NoteFilter.RECENT -> allNotes.sortedByDescending { it.updatedAt }
            else -> allNotes
        }

        NotesUiState(
            notes = filteredNotes,
            foldersMap = foldersMap,
            activeFilter = filter,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotesUiState(isLoading = true)
    )

    fun setFilter(filter: NoteFilter) {
        _activeFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch {
            notesRepository.togglePin(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    companion object {
        fun provideFactory(
            notesRepository: NotesRepository,
            foldersRepository: FoldersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(notesRepository, foldersRepository) as T
            }
        }
    }
}
