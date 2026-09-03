package com.anant.sivonotes.data.local

import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationUnitTest {

    @Test
    fun testTypeConverterStringList() {
        val converter = Converters()
        val originalList = listOf("work", "personal", "study")
        val json = converter.fromStringList(originalList)
        val deserializedList = converter.toStringList(json)

        assertEquals(3, deserializedList.size)
        assertEquals("work", deserializedList[0])
        assertEquals("personal", deserializedList[1])
        assertEquals("study", deserializedList[2])
    }

    @Test
    fun testTypeConverterNullOrEmpty() {
        val converter = Converters()
        assertEquals(emptyList<String>(), converter.toStringList(""))
        assertEquals(emptyList<String>(), converter.toStringList(null))
        assertEquals("[]", converter.fromStringList(null))
    }

    @Test
    fun testEntityDefaults() {
        val folder = FolderEntity(name = "College")
        assertEquals("folder", folder.iconName)
        assertEquals("#6C5CE7", folder.colorHex)

        val note = NoteEntity(title = "Title", content = "Content")
        assertEquals(false, note.isPinned)
        assertTrue(note.tags.isEmpty())

        val todo = TodoEntity(title = "Study")
        assertEquals("MEDIUM", todo.priority)
        assertEquals("NONE", todo.repeatRule)
        assertEquals(false, todo.isCompleted)
    }

    @Test
    fun testScreenRouteGeneration() {
        assertEquals("note_editor?noteId=5&folderId=2", Screen.NoteEditor.createRoute(5, 2))
        assertEquals("note_editor?noteId=-1&folderId=-1", Screen.NoteEditor.createRoute(null, null))
        assertEquals("folder_detail/42", Screen.FolderDetail.createRoute(42))
        assertEquals("important_points?folderId=3", Screen.ImportantPoints.createRoute(3))
    }
}
