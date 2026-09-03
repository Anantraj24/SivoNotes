package com.anant.sivonotes.ui.points

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.ui.components.CategoryHelpers
import com.anant.sivonotes.ui.components.EmptyState

@Composable
fun ImportantPointsScreen(
    viewModel: ImportantPointsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Header
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
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFF39C12),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Important Points",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Quick facts & key concepts to memorize",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fast Add Point Field
        OutlinedTextField(
            value = uiState.newPointText,
            onValueChange = { viewModel.onNewPointTextChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Add a point to remember...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.addPoint() },
                    enabled = uiState.newPointText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add Point",
                        tint = if (uiState.newPointText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Folder Filter Chips
        if (uiState.allFolders.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedFolderId == null,
                        onClick = { viewModel.selectFolder(null) },
                        label = { Text("All Folders") },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                items(uiState.allFolders, key = { it.id }) { folder ->
                    val isSelected = uiState.selectedFolderId == folder.id
                    val color = CategoryHelpers.parseColor(folder.colorHex)

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectFolder(folder.id) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(folder.name)
                            }
                        },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.15f),
                            selectedLabelColor = color,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Points List
        if (uiState.points.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.StarOutline,
                    title = "No points added yet",
                    subtitle = "Store small facts or exam points you need to remember fast."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.points, key = { it.id }) { point ->
                    val folder = point.folderId?.let { uiState.foldersMap[it] }
                    PointItemRow(
                        point = point,
                        folderName = folder?.name,
                        folderColorHex = folder?.colorHex,
                        onToggle = { viewModel.togglePointCompleted(point) },
                        onDelete = { viewModel.deletePoint(point) }
                    )
                }
            }
        }
    }
}

@Composable
fun PointItemRow(
    point: ImportantPointEntity,
    folderName: String?,
    folderColorHex: String?,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val folderColor = CategoryHelpers.parseColor(folderColorHex)

    val textColor by animateColorAsState(
        targetValue = if (point.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.onSurface,
        label = "textColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onToggle),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (point.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                contentDescription = if (point.isCompleted) "Completed" else "Mark Done",
                tint = if (point.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = point.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (point.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = textColor,
                    fontWeight = if (point.isCompleted) FontWeight.Normal else FontWeight.Medium
                )

                if (folderName != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = folderName,
                        style = MaterialTheme.typography.labelSmall,
                        color = folderColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete point",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
