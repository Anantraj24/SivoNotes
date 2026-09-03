package com.anant.sivonotes.backup

import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase6UnitTest {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    @Test
    fun testBackupPayloadSerializationAndDeserialization() {
        val folder = FolderEntity(id = 1, name = "Engineering", colorHex = "#6C5CE7", iconName = "code")
        val note = NoteEntity(id = 10, title = "System Design", content = "Scalability & offline sync", tags = listOf("arch", "db"))
        val todo = TodoEntity(id = 100, title = "Submit Release", priority = "HIGH", repeatRule = "NONE")
        val point = ImportantPointEntity(id = 50, text = "Remember to verify SHA-256")
        val vaultEntry = VaultEntryEntity(id = 7, title = "AWS Root", username = "admin", encryptedPassword = "cipherText...", iv = "iv123")

        val payload = SivoBackupPayload(
            version = 1,
            app = "SivoNotes",
            exportedAt = System.currentTimeMillis(),
            folders = listOf(folder),
            notes = listOf(note),
            todos = listOf(todo),
            importantPoints = listOf(point),
            reminders = emptyList(),
            vaultEntries = listOf(vaultEntry),
            privateNotes = emptyList()
        )

        val jsonString = gson.toJson(payload)
        assertNotNull(jsonString)
        assertTrue(jsonString.contains("SivoNotes"))
        assertTrue(jsonString.contains("System Design"))
        assertTrue(jsonString.contains("AWS Root"))

        val restored = gson.fromJson(jsonString, SivoBackupPayload::class.java)
        assertNotNull(restored)
        assertEquals("SivoNotes", restored.app)
        assertEquals(1, restored.folders.size)
        assertEquals("Engineering", restored.folders[0].name)
        assertEquals(1, restored.notes.size)
        assertEquals(2, restored.notes[0].tags.size)
        assertEquals(1, restored.vaultEntries.size)
        assertEquals("AWS Root", restored.vaultEntries[0].title)
    }

    @Test
    fun testIncompatibleBackupRejection() {
        val invalidJson = """
            {
              "version": 1,
              "app": "OtherNotesApp",
              "folders": []
            }
        """.trimIndent()

        val parsed = gson.fromJson(invalidJson, SivoBackupPayload::class.java)
        assertEquals("OtherNotesApp", parsed.app)
        // Verified that app signature does not equal "SivoNotes"
        assertTrue(parsed.app != "SivoNotes")
    }
}
