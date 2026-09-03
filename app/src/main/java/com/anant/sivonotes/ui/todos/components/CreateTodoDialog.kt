package com.anant.sivonotes.ui.todos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.ui.components.CategoryHelpers
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateTodoDialog(
    todoToEdit: TodoEntity? = null,
    allFolders: List<FolderEntity>,
    preselectedFolderId: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        dueDate: Long?,
        dueTime: String?,
        priority: String,
        repeatRule: String,
        folderId: Long?
    ) -> Unit
) {
    var title by remember { mutableStateOf(todoToEdit?.title ?: "") }
    var description by remember { mutableStateOf(todoToEdit?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(todoToEdit?.priority ?: "MEDIUM") }
    var selectedRepeat by remember { mutableStateOf(todoToEdit?.repeatRule ?: "NONE") }
    var selectedFolderId by remember { mutableStateOf(todoToEdit?.folderId ?: preselectedFolderId) }
    var selectedDueTime by remember { mutableStateOf(todoToEdit?.dueTime ?: "") }

    // Date selection
    val todayMillis = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val tomorrowMillis = remember { todayMillis + 24 * 60 * 60 * 1000 }

    var selectedDueDate by remember {
        mutableStateOf<Long?>(todoToEdit?.dueDate ?: todayMillis)
    }

    var folderDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (todoToEdit != null) "Edit Task" else "New Task",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Task Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("What needs to be done?") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Notes / description (optional)") },
                    maxLines = 3,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Due Date selection chips (Today | Tomorrow | Any Day)
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedDueDate == todayMillis,
                        onClick = { selectedDueDate = todayMillis },
                        label = { Text("Today") },
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = selectedDueDate == tomorrowMillis,
                        onClick = { selectedDueDate = tomorrowMillis },
                        label = { Text("Tomorrow") },
                        shape = CircleShape
                    )
                    FilterChip(
                        selected = selectedDueDate == null,
                        onClick = { selectedDueDate = null },
                        label = { Text("Someday") },
                        shape = CircleShape
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time Presets
                Text(
                    text = "Time (Optional)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                val times = listOf("9:00 AM", "12:00 PM", "6:00 PM", "9:00 PM", "None")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    times.forEach { t ->
                        val isSelected = (t == "None" && selectedDueTime.isEmpty()) || selectedDueTime == t
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDueTime = if (t == "None") "" else t
                            },
                            label = { Text(t) },
                            shape = CircleShape
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Priority
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                        val isSelected = selectedPriority == p
                        val color = when (p) {
                            "HIGH" -> Color(0xFFFF7675)
                            "LOW" -> Color(0xFF00B894)
                            else -> Color(0xFFF39C12)
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = p },
                            label = {
                                Text(
                                    text = p.lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = CircleShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.18f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recurrence / Repeat
                Text(
                    text = "Repeat",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "NONE" to "Once",
                        "DAILY" to "Every day",
                        "WEEKDAYS" to "Weekdays",
                        "WEEKLY" to "Weekly"
                    ).forEach { (ruleKey, label) ->
                        FilterChip(
                            selected = selectedRepeat == ruleKey,
                            onClick = { selectedRepeat = ruleKey },
                            label = { Text(label) },
                            shape = CircleShape
                        )
                    }
                }

                // Folder Selection
                if (allFolders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Folder",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val curFolder = allFolders.find { it.id == selectedFolderId }
                    ExposedDropdownMenuBox(
                        expanded = folderDropdownExpanded,
                        onExpandedChange = { folderDropdownExpanded = !folderDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = curFolder?.name ?: "None (No Folder)",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = folderDropdownExpanded) },
                            leadingIcon = {
                                if (curFolder != null) {
                                    val color = CategoryHelpers.parseColor(curFolder.colorHex)
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(color, CircleShape)
                                    )
                                } else {
                                    Icon(Icons.Outlined.Folder, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.small
                        )

                        ExposedDropdownMenu(
                            expanded = folderDropdownExpanded,
                            onDismissRequest = { folderDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (No Folder)") },
                                onClick = {
                                    selectedFolderId = null
                                    folderDropdownExpanded = false
                                }
                            )
                            allFolders.forEach { f ->
                                val color = CategoryHelpers.parseColor(f.colorHex)
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(color, CircleShape)
                                        )
                                    },
                                    text = { Text(f.name) },
                                    onClick = {
                                        selectedFolderId = f.id
                                        folderDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            title.trim(),
                            description.trim(),
                            selectedDueDate,
                            selectedDueTime.ifBlank { null },
                            selectedPriority,
                            selectedRepeat,
                            selectedFolderId
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text(
                    text = if (todoToEdit != null) "Save" else "Create",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}
