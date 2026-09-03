package com.anant.sivonotes.repository

import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.ui.components.CategoryHelpers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase2UnitTest {

    @Test
    fun testNoteTagsAndPinLogic() {
        val note = NoteEntity(
            id = 1,
            title = "Kotlin Jetpack Compose",
            content = "Compose is modern declarative UI toolkit",
            isPinned = false,
            tags = listOf("android", "kotlin")
        )

        val pinnedNote = note.copy(isPinned = true)
        assertTrue(pinnedNote.isPinned)
        assertEquals(2, pinnedNote.tags.size)
    }

    @Test
    fun testImportantPointCompletionToggle() {
        val point = ImportantPointEntity(
            id = 1,
            text = "Java String is immutable",
            isCompleted = false
        )
        val toggled = point.copy(isCompleted = !point.isCompleted)
        assertTrue(toggled.isCompleted)
    }

    @Test
    fun testCategoryHelpersRelativeTime() {
        val now = System.currentTimeMillis()
        assertEquals("Just now", CategoryHelpers.formatRelativeTime(now - 1000))
        assertEquals("5m ago", CategoryHelpers.formatRelativeTime(now - 5 * 60 * 1000))
        assertEquals("2h ago", CategoryHelpers.formatRelativeTime(now - 2 * 60 * 60 * 1000))
    }

    @Test
    fun testFolderEntityDefaults() {
        val folder = FolderEntity(
            id = 10,
            name = "College",
            colorHex = "#6C5CE7",
            iconName = "school"
        )
        assertEquals("College", folder.name)
        assertEquals("school", folder.iconName)
        assertEquals("#6C5CE7", folder.colorHex)
    }
}
