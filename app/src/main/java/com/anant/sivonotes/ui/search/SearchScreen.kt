package com.anant.sivonotes.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.ui.components.CategoryHelpers
import com.anant.sivonotes.ui.components.EmptyState
import com.anant.sivonotes.ui.notes.components.NoteCard
import com.anant.sivonotes.ui.points.PointItemRow
import com.anant.sivonotes.ui.reminders.ReminderItemCard
import com.anant.sivonotes.ui.todos.components.TodoCard

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onFolderClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Input Row with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Search anything...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SearchFilterType.values().forEach { filter ->
                val isSelected = uiState.filterType == filter
                val label = when (filter) {
                    SearchFilterType.ALL -> "All"
                    SearchFilterType.NOTES -> "Notes"
                    SearchFilterType.TASKS -> "Tasks"
                    SearchFilterType.POINTS -> "Points"
                    SearchFilterType.FOLDERS -> "Folders"
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFilterType(filter) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.query.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.Search,
                    title = "Search across Sivo",
                    subtitle = "Type to search your notes, tasks, important points, reminders, and folders."
                )
            }
        } else if (uiState.totalMatchesCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.SearchOff,
                    title = "No results found",
                    subtitle = "No items matched \"${uiState.query}\". Try a different keyword."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Matched Folders
                if (uiState.matchedFolders.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Folders (${uiState.matchedFolders.size})")
                    }
                    items(uiState.matchedFolders, key = { "f_${it.id}" }) { folder ->
                        val color = CategoryHelpers.parseColor(folder.colorHex)
                        val icon = CategoryHelpers.getIcon(folder.iconName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onFolderClick(folder.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(color.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = folder.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Matched Notes
                if (uiState.matchedNotes.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Notes (${uiState.matchedNotes.size})")
                    }
                    items(uiState.matchedNotes, key = { "n_${it.id}" }) { note ->
                        val folder = note.folderId?.let { uiState.foldersMap[it] }
                        NoteCard(
                            note = note,
                            folder = folder,
                            onClick = { onNoteClick(note.id) },
                            onPinClick = {}
                        )
                    }
                }

                // Matched Tasks
                if (uiState.matchedTodos.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Tasks (${uiState.matchedTodos.size})")
                    }
                    items(uiState.matchedTodos, key = { "t_${it.id}" }) { todo ->
                        val folder = todo.folderId?.let { uiState.foldersMap[it] }
                        TodoCard(
                            todo = todo,
                            folder = folder,
                            onToggleCompleted = { viewModel.toggleTodo(todo) },
                            onDelete = {},
                            onClick = {}
                        )
                    }
                }

                // Matched Important Points
                if (uiState.matchedPoints.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Important Points (${uiState.matchedPoints.size})")
                    }
                    items(uiState.matchedPoints, key = { "p_${it.id}" }) { point ->
                        val folder = point.folderId?.let { uiState.foldersMap[it] }
                        PointItemRow(
                            point = point,
                            folderName = folder?.name,
                            folderColorHex = folder?.colorHex,
                            onToggle = { viewModel.togglePoint(point) },
                            onDelete = {}
                        )
                    }
                }

                // Matched Reminders
                if (uiState.matchedReminders.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Reminders (${uiState.matchedReminders.size})")
                    }
                    items(uiState.matchedReminders, key = { "r_${it.id}" }) { reminder ->
                        ReminderItemCard(
                            reminder = reminder,
                            onDone = {},
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
