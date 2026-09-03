package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.NoteDao
import com.anant.sivonotes.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun getPinnedNotes(): Flow<List<NoteEntity>> = noteDao.getPinnedNotes()

    fun getRecentNotes(limit: Int = 10): Flow<List<NoteEntity>> = noteDao.getRecentNotes(limit)

    fun getNotesByFolder(folderId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByFolder(folderId)

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdDirect(id: Long): NoteEntity? = noteDao.getNoteByIdDirect(id)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun togglePin(note: NoteEntity) {
        noteDao.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }
}
