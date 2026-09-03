package com.anant.sivonotes.backup

import android.content.Context
import android.net.Uri
import com.anant.sivonotes.data.local.AppDatabase
import com.anant.sivonotes.data.local.entity.FolderEntity
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import com.anant.sivonotes.data.local.entity.NoteEntity
import com.anant.sivonotes.data.local.entity.PrivateNoteEntity
import com.anant.sivonotes.data.local.entity.ReminderEntity
import com.anant.sivonotes.data.local.entity.TodoEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

data class SivoBackupPayload(
    val version: Int = 1,
    val app: String = "SivoNotes",
    val exportedAt: Long = System.currentTimeMillis(),
    val folders: List<FolderEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList(),
    val todos: List<TodoEntity> = emptyList(),
    val importantPoints: List<ImportantPointEntity> = emptyList(),
    val reminders: List<ReminderEntity> = emptyList(),
    val vaultEntries: List<VaultEntryEntity> = emptyList(),
    val privateNotes: List<PrivateNoteEntity> = emptyList()
)

class BackupRestoreManager(
    private val context: Context,
    private val database: AppDatabase
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun createBackupPayload(): SivoBackupPayload = withContext(Dispatchers.IO) {
        val folderDao = database.folderDao()
        val noteDao = database.noteDao()
        val todoDao = database.todoDao()
        val pointDao = database.importantPointDao()
        val reminderDao = database.reminderDao()
        val vaultDao = database.vaultDao()

        // Fetch direct lists
        val folders = folderDao.getAllFoldersDirect()
        val notes = noteDao.getAllNotesDirect()
        val todos = todoDao.getAllTodosDirect()
        val points = pointDao.getAllPointsDirect()
        val reminders = reminderDao.getAllRemindersDirect()
        val vaultEntries = vaultDao.getAllVaultEntriesDirect()
        val privateNotes = vaultDao.getAllPrivateNotesDirect()

        SivoBackupPayload(
            version = 1,
            app = "SivoNotes",
            exportedAt = System.currentTimeMillis(),
            folders = folders,
            notes = notes,
            todos = todos,
            importantPoints = points,
            reminders = reminders,
            vaultEntries = vaultEntries,
            privateNotes = privateNotes
        )
    }

    suspend fun exportBackupToUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val payload = createBackupPayload()
            val jsonString = gson.toJson(payload)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                }
            } ?: return@withContext Result.failure(Exception("Failed to open output stream"))

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun importBackupFromUri(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: return@withContext Result.failure(Exception("Failed to open input stream"))

            val payload = gson.fromJson(jsonString, SivoBackupPayload::class.java)
                ?: return@withContext Result.failure(Exception("Invalid backup format"))

            if (payload.app != "SivoNotes") {
                return@withContext Result.failure(Exception("Incompatible backup file"))
            }

            var importedCount = 0

            // Restore records
            val folderDao = database.folderDao()
            val noteDao = database.noteDao()
            val todoDao = database.todoDao()
            val pointDao = database.importantPointDao()
            val reminderDao = database.reminderDao()
            val vaultDao = database.vaultDao()

            payload.folders.forEach {
                folderDao.insertFolder(it.copy(id = 0))
                importedCount++
            }
            payload.notes.forEach {
                noteDao.insertNote(it.copy(id = 0))
                importedCount++
            }
            payload.todos.forEach {
                todoDao.insertTodo(it.copy(id = 0))
                importedCount++
            }
            payload.importantPoints.forEach {
                pointDao.insertPoint(it.copy(id = 0))
                importedCount++
            }
            payload.reminders.forEach {
                reminderDao.insertReminder(it.copy(id = 0))
                importedCount++
            }
            payload.vaultEntries.forEach {
                vaultDao.insertVaultEntry(it.copy(id = 0))
                importedCount++
            }
            payload.privateNotes.forEach {
                vaultDao.insertPrivateNote(it.copy(id = 0))
                importedCount++
            }

            Result.success(importedCount)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
