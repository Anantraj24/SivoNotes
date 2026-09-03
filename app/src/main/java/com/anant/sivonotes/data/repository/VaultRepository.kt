package com.anant.sivonotes.data.repository

import android.content.Context
import com.anant.sivonotes.data.local.dao.VaultDao
import com.anant.sivonotes.data.local.entity.PrivateNoteEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity
import kotlinx.coroutines.flow.Flow

class VaultRepository(
    private val vaultDao: VaultDao,
    private val context: Context
) {
    fun getAllVaultEntries(): Flow<List<VaultEntryEntity>> = vaultDao.getAllVaultEntries()
    suspend fun getVaultEntryById(id: Long): VaultEntryEntity? = vaultDao.getVaultEntryById(id)
    suspend fun insertVaultEntry(entry: VaultEntryEntity): Long = vaultDao.insertVaultEntry(entry)
    suspend fun updateVaultEntry(entry: VaultEntryEntity) = vaultDao.updateVaultEntry(entry)
    suspend fun deleteVaultEntry(entry: VaultEntryEntity) = vaultDao.deleteVaultEntry(entry)
    suspend fun deleteVaultEntryById(id: Long) = vaultDao.deleteVaultEntryById(id)

    fun getAllPrivateNotes(): Flow<List<PrivateNoteEntity>> = vaultDao.getAllPrivateNotes()
    suspend fun getPrivateNoteById(id: Long): PrivateNoteEntity? = vaultDao.getPrivateNoteById(id)
    suspend fun insertPrivateNote(note: PrivateNoteEntity): Long = vaultDao.insertPrivateNote(note)
    suspend fun updatePrivateNote(note: PrivateNoteEntity) = vaultDao.updatePrivateNote(note)
    suspend fun deletePrivateNote(note: PrivateNoteEntity) = vaultDao.deletePrivateNote(note)
    suspend fun deletePrivateNoteById(id: Long) = vaultDao.deletePrivateNoteById(id)
}
