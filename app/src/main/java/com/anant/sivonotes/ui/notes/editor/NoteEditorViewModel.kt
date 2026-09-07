package com.anant.sivonotes.ui.notes.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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

/** Formatting modes supported by the toolbar */
enum class FormatType {
    BOLD,
    ITALIC,
    HEADING,
    BULLET,
    CHECKLIST
}

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

    /**
     * Apply a formatting action to the current TextFieldValue:
     * - Inline (BOLD, ITALIC):
     *   * If text is selected: wrap with markers (or unwrap if already wrapped).
     *   * If no selection: detect word under/adjacent to cursor and wrap/unwrap it.
     *   * If empty space: insert marker pair and place cursor inside.
     * - Block (HEADING, BULLET, CHECKLIST):
     *   * Toggle or cycle the line prefix at the current line start.
     */
    fun applyFormatting(current: TextFieldValue, type: FormatType): TextFieldValue {
        val text = current.text
        val selMin = current.selection.min.coerceIn(0, text.length)
        val selMax = current.selection.max.coerceIn(0, text.length)
        val hasSelection = selMin != selMax

        return when (type) {
            FormatType.BOLD -> applyInlineFormat(text, selMin, selMax, hasSelection, "**")
            FormatType.ITALIC -> applyInlineFormat(text, selMin, selMax, hasSelection, "*")
            FormatType.HEADING -> toggleLineHeading(text, selMin)
            FormatType.BULLET -> toggleLinePrefix(text, selMin, "• ")
            FormatType.CHECKLIST -> toggleChecklist(text, selMin)
        }.also { newValue ->
            onContentChange(newValue.text)
        }
    }

    // ── Inline & Block Formatting Helpers ──────────────────────────────────────

    private fun applyInlineFormat(
        text: String,
        selMin: Int,
        selMax: Int,
        hasSelection: Boolean,
        marker: String
    ): TextFieldValue {
        val markerLen = marker.length

        if (hasSelection) {
            val selected = text.substring(selMin, selMax)
            // Check if selection already starts and ends with marker
            if (selected.startsWith(marker) && selected.endsWith(marker) && selected.length >= markerLen * 2) {
                // Unwrap inside selection
                val unwrapped = selected.substring(markerLen, selected.length - markerLen)
                val newText = text.substring(0, selMin) + unwrapped + text.substring(selMax)
                return TextFieldValue(newText, TextRange(selMin, selMin + unwrapped.length))
            }
            // Check if markers surround the selection externally
            if (selMin >= markerLen && selMax + markerLen <= text.length &&
                text.substring(selMin - markerLen, selMin) == marker &&
                text.substring(selMax, selMax + markerLen) == marker
            ) {
                // Unwrap external markers
                val newText = text.substring(0, selMin - markerLen) + selected + text.substring(selMax + markerLen)
                val newStart = selMin - markerLen
                return TextFieldValue(newText, TextRange(newStart, newStart + selected.length))
            }

            // Wrap selection
            val newText = text.substring(0, selMin) + marker + selected + marker + text.substring(selMax)
            val newCursor = selMax + markerLen * 2
            return TextFieldValue(newText, TextRange(newCursor))
        } else {
            // No selection: check word under / right before cursor
            val cursorPos = selMin
            val (wordStart, wordEnd) = findWordBounds(text, cursorPos)
            if (wordStart < wordEnd) {
                val word = text.substring(wordStart, wordEnd)
                // Check if word is already wrapped with marker
                if (wordStart >= markerLen && wordEnd + markerLen <= text.length &&
                    text.substring(wordStart - markerLen, wordStart) == marker &&
                    text.substring(wordEnd, wordEnd + markerLen) == marker
                ) {
                    // Unwrap word
                    val newText = text.substring(0, wordStart - markerLen) + word + text.substring(wordEnd + markerLen)
                    val newCursor = (cursorPos - markerLen).coerceIn(wordStart - markerLen, wordStart - markerLen + word.length)
                    return TextFieldValue(newText, TextRange(newCursor))
                } else {
                    // Wrap word
                    val newText = text.substring(0, wordStart) + marker + word + marker + text.substring(wordEnd)
                    val newCursor = wordEnd + markerLen * 2
                    return TextFieldValue(newText, TextRange(newCursor))
                }
            } else {
                // Empty space or at boundary — insert marker pair and place cursor between them
                val newText = text.substring(0, cursorPos) + marker + marker + text.substring(cursorPos)
                val newCursor = cursorPos + markerLen
                return TextFieldValue(newText, TextRange(newCursor))
            }
        }
    }

    private fun findWordBounds(text: String, cursorPos: Int): Pair<Int, Int> {
        if (text.isEmpty()) return Pair(0, 0)

        // Locate character of the word
        val checkPos = when {
            cursorPos > 0 && !text[cursorPos - 1].isWhitespace() && text[cursorPos - 1] != '*' && text[cursorPos - 1] != '#' -> cursorPos - 1
            cursorPos < text.length && !text[cursorPos].isWhitespace() && text[cursorPos] != '*' && text[cursorPos] != '#' -> cursorPos
            else -> return Pair(cursorPos, cursorPos)
        }

        var start = checkPos
        while (start > 0 && !text[start - 1].isWhitespace() && text[start - 1] != '*' && text[start - 1] != '#' && text[start - 1] != '~') {
            start--
        }

        var end = checkPos + 1
        while (end < text.length && !text[end].isWhitespace() && text[end] != '*' && text[end] != '#' && text[end] != '~') {
            end++
        }

        return Pair(start, end)
    }

    private fun toggleLineHeading(text: String, cursorPos: Int): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = when {
            line.startsWith("### ") -> Pair(line.removePrefix("### "), -4)
            line.startsWith("## ") -> Pair("### " + line.removePrefix("## "), 1)
            line.startsWith("# ") -> Pair("## " + line.removePrefix("# "), 1)
            else -> {
                val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("☐ ").removePrefix("☑ ")
                val diff = cleanLine.length - line.length
                Pair("## $cleanLine", 3 + diff)
            }
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }

    private fun toggleLinePrefix(text: String, cursorPos: Int, prefix: String): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = if (line.startsWith(prefix)) {
            Pair(line.removePrefix(prefix), -prefix.length)
        } else {
            val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("☐ ").removePrefix("☑ ").removePrefix("## ").removePrefix("### ").removePrefix("# ")
            val diff = cleanLine.length - line.length
            Pair(prefix + cleanLine, prefix.length + diff)
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }

    private fun toggleChecklist(text: String, cursorPos: Int): TextFieldValue {
        val lineStart = text.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it
        }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, delta) = when {
            line.startsWith("☐ ") -> Pair("☑ " + line.removePrefix("☐ "), 0)
            line.startsWith("☑ ") -> Pair(line.removePrefix("☑ "), -2)
            else -> {
                val cleanLine = line.removePrefix("• ").removePrefix("- ").removePrefix("## ").removePrefix("### ").removePrefix("# ")
                val diff = cleanLine.length - line.length
                Pair("☐ " + cleanLine, 2 + diff)
            }
        }

        val newText = text.substring(0, lineStart) + newLine + text.substring(lineEnd)
        val newCursor = (cursorPos + delta).coerceIn(lineStart, lineStart + newLine.length)
        return TextFieldValue(newText, TextRange(newCursor))
    }

    // ── Persistence ────────────────────────────────────────────────────────────

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
            delay(400)
            saveNoteDirect()
        }
    }

    fun saveNoteDirect() {
        val state = _uiState.value
        if (state.title.isBlank() && state.content.isBlank() && state.noteId == 0L) {
            _uiState.value = state.copy(saveStatus = "Saved")
            return
        }

        viewModelScope.launch {
            val noteEntity = NoteEntity(
                id = state.noteId,
                title = state.title.trim(),
                content = state.content,
                folderId = state.folderId,
                isPinned = state.isPinned,
                tags = state.tags,
                colorHex = state.colorHex,
                updatedAt = System.currentTimeMillis()
            )

            if (state.noteId == 0L) {
                val newId = notesRepository.insertNote(noteEntity)
                _uiState.value = _uiState.value.copy(
                    noteId = newId,
                    saveStatus = "Saved"
                )
            } else {
                notesRepository.updateNote(noteEntity)
                _uiState.value = _uiState.value.copy(saveStatus = "Saved")
            }
        }
    }

    fun deleteNote() {
        val noteId = _uiState.value.noteId
        if (noteId > 0) {
            viewModelScope.launch {
                notesRepository.deleteNoteById(noteId)
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
