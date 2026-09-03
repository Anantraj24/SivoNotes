package com.anant.sivonotes.search

import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase4UnitTest {

    @Test
    fun testSearchQueryMatchingNotes() {
        val notes = listOf(
            NoteEntity(id = 1, title = "Android Architecture", content = "Room with Flow and StateFlow", tags = listOf("android")),
            NoteEntity(id = 2, title = "Grocery List", content = "Milk, Eggs, Bread", tags = listOf("shopping")),
            NoteEntity(id = 3, title = "Kotlin Coroutines", content = "Dispatchers.IO for background tasks", tags = listOf("android", "concurrency"))
        )

        val query = "Android"
        val matched = notes.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }

        assertEquals(2, matched.size)
        assertTrue(matched.any { it.id == 1L })
        assertTrue(matched.any { it.id == 3L })
    }

    @Test
    fun testSearchQueryMatchingTasksAndPoints() {
        val todos = listOf(
            TodoEntity(id = 1, title = "Submit assignment", description = "CS50 Project"),
            TodoEntity(id = 2, title = "Buy vegetables", description = "")
        )
        val points = listOf(
            ImportantPointEntity(id = 1, text = "CS50 final exam on Monday")
        )

        val query = "CS50"
        val matchTodos = todos.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        val matchPoints = points.filter { it.text.contains(query, ignoreCase = true) }

        assertEquals(1, matchTodos.size)
        assertEquals(1, matchPoints.size)
    }

    @Test
    fun testSearchFolderMatching() {
        val folders = listOf(
            FolderEntity(id = 1, name = "University"),
            FolderEntity(id = 2, name = "Work & Projects")
        )

        val query = "work"
        val matched = folders.filter { it.name.contains(query, ignoreCase = true) }
        assertEquals(1, matched.size)
        assertEquals("Work & Projects", matched[0].name)
    }
}
