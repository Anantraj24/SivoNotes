package com.anant.sivonotes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryHelpers {
    val PRESET_COLORS = listOf(
        "#6C5CE7", // Lavender / Royal Violet
        "#00B894", // Mint Green
        "#FF7675", // Coral Peach
        "#F39C12", // Honey Yellow
        "#0984E3", // Sky Blue
        "#E84393", // Rose Pink
        "#E17055", // Amber Orange
        "#00CEC9"  // Robin Teal
    )

    val PRESET_ICONS = listOf(
        "folder" to Icons.Rounded.Folder,
        "school" to Icons.Rounded.School,
        "book" to Icons.AutoMirrored.Rounded.MenuBook,
        "work" to Icons.Rounded.Work,
        "code" to Icons.Rounded.Code,
        "home" to Icons.Rounded.Home,
        "star" to Icons.Rounded.Star,
        "cart" to Icons.Rounded.ShoppingCart,
        "heart" to Icons.Rounded.Favorite,
        "tag" to Icons.Rounded.Bookmark
    )

    fun getIcon(name: String): ImageVector {
        return PRESET_ICONS.find { it.first.equals(name, ignoreCase = true) }?.second
            ?: Icons.Rounded.Folder
    }

    fun parseColor(hex: String?, defaultColor: Color = Color(0xFF6C5CE7)): Color {
        if (hex.isNullOrBlank()) return defaultColor
        return try {
            val cleanHex = if (hex.startsWith("#")) hex else "#$hex"
            Color(android.graphics.Color.parseColor(cleanHex))
        } catch (e: Exception) {
            defaultColor
        }
    }

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        if (diff < 0) return "Just now"
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            else -> {
                val sdf = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
                sdf.format(java.util.Date(timestamp))
            }
        }
    }
}
