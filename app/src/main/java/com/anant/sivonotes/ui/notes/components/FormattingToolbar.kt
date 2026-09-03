package com.anant.sivonotes.ui.notes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FormattingToolbar(
    onBoldClick: () -> Unit,
    onItalicClick: () -> Unit,
    onBulletListClick: () -> Unit,
    onChecklistClick: () -> Unit,
    onHeadingClick: () -> Unit,
    onAddTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolbarIconButton(
            icon = Icons.Outlined.FormatBold,
            contentDescription = "Bold",
            onClick = onBoldClick
        )
        ToolbarIconButton(
            icon = Icons.Outlined.FormatItalic,
            contentDescription = "Italic",
            onClick = onItalicClick
        )
        ToolbarIconButton(
            icon = Icons.Outlined.Title,
            contentDescription = "Heading",
            onClick = onHeadingClick
        )
        ToolbarIconButton(
            icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            contentDescription = "Bullet List",
            onClick = onBulletListClick
        )
        ToolbarIconButton(
            icon = Icons.Outlined.CheckBox,
            contentDescription = "Checklist",
            onClick = onChecklistClick
        )
        ToolbarIconButton(
            icon = Icons.AutoMirrored.Outlined.Label,
            contentDescription = "Add Tag",
            onClick = onAddTagClick
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
