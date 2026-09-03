package com.anant.sivonotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalAddSheet(
    onDismiss: () -> Unit,
    onAddNote: () -> Unit,
    onAddTodo: () -> Unit,
    onAddReminder: () -> Unit,
    onAddPoint: () -> Unit,
    onAddFolder: () -> Unit,
    onAddVaultEntry: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Quick Capture",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            QuickActionRow(
                icon = Icons.Outlined.EditNote,
                title = "New Note",
                subtitle = "Write down a thought, concept, or document",
                color = Color(0xFF6C5CE7),
                onClick = {
                    onDismiss()
                    onAddNote()
                }
            )

            QuickActionRow(
                icon = Icons.Outlined.CheckBox,
                title = "New Task",
                subtitle = "Schedule a daily or recurring todo item",
                color = Color(0xFF00B894),
                onClick = {
                    onDismiss()
                    onAddTodo()
                }
            )

            QuickActionRow(
                icon = Icons.Outlined.Star,
                title = "Important Point",
                subtitle = "Fast single-line concept to memorize",
                color = Color(0xFFF39C12),
                onClick = {
                    onDismiss()
                    onAddPoint()
                }
            )

            QuickActionRow(
                icon = Icons.Outlined.Alarm,
                title = "Set Reminder",
                subtitle = "Time-based local offline notification",
                color = Color(0xFF0984E3),
                onClick = {
                    onDismiss()
                    onAddReminder()
                }
            )

            QuickActionRow(
                icon = Icons.Outlined.CreateNewFolder,
                title = "New Folder",
                subtitle = "Organize mixed content under a custom category",
                color = Color(0xFFE84393),
                onClick = {
                    onDismiss()
                    onAddFolder()
                }
            )

            QuickActionRow(
                icon = Icons.Outlined.Lock,
                title = "Secure Vault Entry",
                subtitle = "Save a password or encrypted private note",
                color = Color(0xFF2D3436),
                onClick = {
                    onDismiss()
                    onAddVaultEntry()
                }
            )
        }
    }
}

@Composable
private fun QuickActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
