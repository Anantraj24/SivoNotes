package com.anant.sivonotes.ui.folders

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.ui.components.CategoryHelpers
import com.anant.sivonotes.ui.components.EmptyState
import com.anant.sivonotes.ui.folders.components.CreateFolderDialog
import com.anant.sivonotes.ui.notes.components.NoteCard
import com.anant.sivonotes.ui.points.PointItemRow

@Composable
fun FolderDetailScreen(
    viewModel: FolderDetailViewModel,
    onBack: () -> Unit,
    onNoteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.folder, uiState.isLoading) {
        if (!uiState.isLoading && uiState.folder == null) {
            onBack()
        }
    }

    val folder = uiState.folder ?: return
    val accentColor = CategoryHelpers.parseColor(folder.colorHex)
    val icon = CategoryHelpers.getIcon(folder.iconName)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Navigation & Action Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Folder",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete Folder",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Folder Title Header Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${uiState.notes.size} notes • ${uiState.todos.size} tasks • ${uiState.points.size} points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Tabs (Notes | Todos | Important Points)
        val tabs = listOf("Notes (${uiState.notes.size})", "Todos (${uiState.todos.size})", "Points (${uiState.points.size})")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = accentColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Notes tab
                    if (uiState.notes.isEmpty()) {
                        EmptyState(
                            icon = Icons.Outlined.FolderOpen,
                            title = "No notes in this folder",
                            subtitle = "Assign notes to this folder to see them here."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    folder = folder,
                                    onClick = { onNoteClick(note.id) },
                                    onPinClick = { viewModel.togglePin(note) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Todos tab
                    if (uiState.todos.isEmpty()) {
                        EmptyState(
                            icon = Icons.Outlined.FolderOpen,
                            title = "No tasks in this folder",
                            subtitle = "Assign daily tasks to this folder."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.todos, key = { it.id }) { todo ->
                                PointItemRow(
                                    point = com.anant.sivonotes.data.local.entity.ImportantPointEntity(
                                        id = todo.id,
                                        text = todo.title,
                                        isCompleted = todo.isCompleted
                                    ),
                                    folderName = null,
                                    folderColorHex = null,
                                    onToggle = { viewModel.toggleTodo(todo) },
                                    onDelete = {}
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Important Points tab
                    if (uiState.points.isEmpty()) {
                        EmptyState(
                            icon = Icons.Outlined.FolderOpen,
                            title = "No points in this folder",
                            subtitle = "Add key memory concepts in this folder."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.points, key = { it.id }) { point ->
                                PointItemRow(
                                    point = point,
                                    folderName = null,
                                    folderColorHex = null,
                                    onToggle = { viewModel.togglePoint(point) },
                                    onDelete = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Folder Dialog
    if (showEditDialog) {
        CreateFolderDialog(
            folderToEdit = folder,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, iconKey, colorHex ->
                viewModel.updateFolder(name, iconKey, colorHex)
                showEditDialog = false
            }
        )
    }

    // Delete Folder Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Folder?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${folder.name}\"? Contents inside will not be deleted.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFolder()
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
