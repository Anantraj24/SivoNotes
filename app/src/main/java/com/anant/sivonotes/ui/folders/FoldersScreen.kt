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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.ui.components.EmptyState
import com.anant.sivonotes.ui.folders.components.CreateFolderDialog
import com.anant.sivonotes.ui.folders.components.FolderCard

@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel,
    onFolderClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var folderToEdit by remember { mutableStateOf<FolderEntity?>(null) }
    var folderToDelete by remember { mutableStateOf<FolderEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Folders",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${uiState.foldersWithCounts.size} organized categories",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.CreateNewFolder,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "New Folder",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.foldersWithCounts.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.FolderOpen,
                    title = "No folders yet",
                    subtitle = "Create folders to organize notes, todos, and important points together.",
                    actionLabel = "Create First Folder",
                    onActionClick = { showCreateDialog = true }
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(uiState.foldersWithCounts, key = { it.folder.id }) { item ->
                    FolderCard(
                        folderWithCounts = item,
                        onClick = { onFolderClick(item.folder.id) },
                        onLongClick = { folderToEdit = item.folder }
                    )
                }
            }
        }
    }

    // Create / Edit Folder Dialog
    if (showCreateDialog || folderToEdit != null) {
        CreateFolderDialog(
            folderToEdit = folderToEdit,
            onDismiss = {
                showCreateDialog = false
                folderToEdit = null
            },
            onConfirm = { name, icon, colorHex ->
                if (folderToEdit != null) {
                    viewModel.updateFolder(folderToEdit!!, name, icon, colorHex)
                } else {
                    viewModel.createFolder(name, icon, colorHex)
                }
                showCreateDialog = false
                folderToEdit = null
            }
        )
    }

    // Delete confirmation dialog
    folderToDelete?.let { folder ->
        AlertDialog(
            onDismissRequest = { folderToDelete = null },
            title = {
                Text(
                    text = "Delete Folder?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Delete \"${folder.name}\"? Contents inside will not be deleted, they will simply be unassigned from this folder.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFolder(folder)
                        folderToDelete = null
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
                TextButton(onClick = { folderToDelete = null }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}
