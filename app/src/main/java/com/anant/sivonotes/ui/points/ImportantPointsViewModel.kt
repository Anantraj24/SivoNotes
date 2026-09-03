package com.anant.sivonotes.ui.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.repository.FoldersRepository
import com.anant.sivonotes.data.repository.ImportantPointsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PointsUiState(
    val points: List<ImportantPointEntity> = emptyList(),
    val foldersMap: Map<Long, FolderEntity> = emptyMap(),
    val allFolders: List<FolderEntity> = emptyList(),
    val selectedFolderId: Long? = null,
    val newPointText: String = "",
    val isLoading: Boolean = false
)

class ImportantPointsViewModel(
    private val initialFolderId: Long?,
    private val pointsRepository: ImportantPointsRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val _selectedFolderId = MutableStateFlow(
        if (initialFolderId != null && initialFolderId > 0) initialFolderId else null
    )
    val selectedFolderId: StateFlow<Long?> = _selectedFolderId.asStateFlow()

    private val _newPointText = MutableStateFlow("")
    val newPointText: StateFlow<String> = _newPointText.asStateFlow()

    val uiState: StateFlow<PointsUiState> = combine(
        pointsRepository.getAllPoints(),
        foldersRepository.getAllFolders(),
        _selectedFolderId,
        _newPointText
    ) { allPoints, allFolders, folderId, newText ->
        val foldersMap = allFolders.associateBy { it.id }
        val filteredPoints = if (folderId != null) {
            allPoints.filter { it.folderId == folderId }
        } else {
            allPoints
        }

        PointsUiState(
            points = filteredPoints,
            foldersMap = foldersMap,
            allFolders = allFolders,
            selectedFolderId = folderId,
            newPointText = newText,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PointsUiState(isLoading = true)
    )

    fun onNewPointTextChange(text: String) {
        _newPointText.value = text
    }

    fun selectFolder(folderId: Long?) {
        _selectedFolderId.value = folderId
    }

    fun addPoint() {
        val text = _newPointText.value.trim()
        if (text.isNotBlank()) {
            viewModelScope.launch {
                pointsRepository.insertPoint(
                    ImportantPointEntity(
                        text = text,
                        folderId = _selectedFolderId.value,
                        isCompleted = false
                    )
                )
                _newPointText.value = ""
            }
        }
    }

    fun togglePointCompleted(point: ImportantPointEntity) {
        viewModelScope.launch {
            pointsRepository.togglePointCompleted(point)
        }
    }

    fun deletePoint(point: ImportantPointEntity) {
        viewModelScope.launch {
            pointsRepository.deletePoint(point)
        }
    }

    companion object {
        fun provideFactory(
            folderId: Long?,
            pointsRepository: ImportantPointsRepository,
            foldersRepository: FoldersRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ImportantPointsViewModel(folderId, pointsRepository, foldersRepository) as T
            }
        }
    }
}
