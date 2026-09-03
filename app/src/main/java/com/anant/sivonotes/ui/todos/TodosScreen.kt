package com.anant.sivonotes.ui.todos

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.ui.components.EmptyState
import com.anant.sivonotes.ui.todos.components.CreateTodoDialog
import com.anant.sivonotes.ui.todos.components.TodoCard

@Composable
fun TodosScreen(
    viewModel: TodosViewModel,
    onNavigateToStreak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<TodoEntity?>(null) }

    val todayTotal = uiState.todayTodos.size + uiState.completedTodos.size
    val todayCompleted = uiState.completedTodos.size
    val progress = if (todayTotal > 0) (todayCompleted.toFloat() / todayTotal.toFloat()) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "todayProgress")

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
                    text = "Daily Tasks",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Stay organized and keep your streak alive",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Streak Badge Pill
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFFFECEB))
                    .clickable(onClick = onNavigateToStreak)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.streakStats.currentStreak}d streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFE17055),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$todayCompleted of $todayTotal completed",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search tasks...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
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

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Today | Upcoming | Completed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TodoTab.values().forEach { tab ->
                val isSelected = uiState.activeTab == tab
                val count = when (tab) {
                    TodoTab.TODAY -> uiState.todayTodos.size
                    TodoTab.UPCOMING -> uiState.upcomingTodos.size
                    TodoTab.COMPLETED -> uiState.completedTodos.size
                }
                val label = when (tab) {
                    TodoTab.TODAY -> "Today ($count)"
                    TodoTab.UPCOMING -> "Upcoming ($count)"
                    TodoTab.COMPLETED -> "Completed ($count)"
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setActiveTab(tab) },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Task Items List
        val currentList = when (uiState.activeTab) {
            TodoTab.TODAY -> uiState.todayTodos
            TodoTab.UPCOMING -> uiState.upcomingTodos
            TodoTab.COMPLETED -> uiState.completedTodos
        }

        if (currentList.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val emptyTitle = when (uiState.activeTab) {
                    TodoTab.TODAY -> "You're all caught up!"
                    TodoTab.UPCOMING -> "No upcoming tasks"
                    TodoTab.COMPLETED -> "No completed tasks yet"
                }
                val emptySubtitle = when (uiState.activeTab) {
                    TodoTab.TODAY -> "Enjoy your day or add a new task to stay ahead."
                    TodoTab.UPCOMING -> "Schedule tasks for tomorrow or next week."
                    TodoTab.COMPLETED -> "Tasks you complete will appear here."
                }

                EmptyState(
                    icon = if (uiState.activeTab == TodoTab.COMPLETED) Icons.Outlined.CheckCircle else Icons.Outlined.CheckBox,
                    title = emptyTitle,
                    subtitle = emptySubtitle,
                    actionLabel = if (uiState.activeTab == TodoTab.TODAY) "Add Task" else null,
                    onActionClick = { showCreateDialog = true }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(currentList, key = { it.id }) { todo ->
                    val folder = todo.folderId?.let { uiState.foldersMap[it] }
                    TodoCard(
                        todo = todo,
                        folder = folder,
                        onToggleCompleted = { viewModel.toggleTodoCompleted(todo) },
                        onDelete = { viewModel.deleteTodo(todo) },
                        onClick = { todoToEdit = todo }
                    )
                }
            }
        }
    }

    // Create / Edit Dialog
    if (showCreateDialog || todoToEdit != null) {
        CreateTodoDialog(
            todoToEdit = todoToEdit,
            allFolders = uiState.allFolders,
            preselectedFolderId = uiState.selectedFolderId,
            onDismiss = {
                showCreateDialog = false
                todoToEdit = null
            },
            onConfirm = { title, desc, dueDate, dueTime, priority, repeat, folderId ->
                if (todoToEdit != null) {
                    viewModel.updateTodo(todoToEdit!!, title, desc, dueDate, dueTime, priority, repeat, folderId)
                } else {
                    viewModel.createTodo(title, desc, dueDate, dueTime, priority, repeat, folderId)
                }
                showCreateDialog = false
                todoToEdit = null
            }
        )
    }
}
