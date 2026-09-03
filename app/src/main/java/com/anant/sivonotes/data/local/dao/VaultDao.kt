package com.anant.sivonotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anant.sivonotes.data.local.entity.PrivateNoteEntity
import com.anant.sivonotes.data.local.entity.VaultEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    // Password Entries
    @Query("SELECT * FROM vault_entries ORDER BY updatedAt DESC")
    fun getAllVaultEntries(): Flow<List<VaultEntryEntity>>

    @Query("SELECT * FROM vault_entries ORDER BY updatedAt DESC")
    suspend fun getAllVaultEntriesDirect(): List<VaultEntryEntity>

    @Query("SELECT * FROM vault_entries WHERE id = :id")
    suspend fun getVaultEntryById(id: Long): VaultEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultEntry(entry: VaultEntryEntity): Long

    @Update
    suspend fun updateVaultEntry(entry: VaultEntryEntity)

    @Delete
    suspend fun deleteVaultEntry(entry: VaultEntryEntity)

    @Query("DELETE FROM vault_entries WHERE id = :id")
    suspend fun deleteVaultEntryById(id: Long)

    // Private Notes
    @Query("SELECT * FROM private_notes ORDER BY updatedAt DESC")
    fun getAllPrivateNotes(): Flow<List<PrivateNoteEntity>>

    @Query("SELECT * FROM private_notes ORDER BY updatedAt DESC")
    suspend fun getAllPrivateNotesDirect(): List<PrivateNoteEntity>

    @Query("SELECT * FROM private_notes WHERE id = :id")
    suspend fun getPrivateNoteById(id: Long): PrivateNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateNote(note: PrivateNoteEntity): Long

    @Update
    suspend fun updatePrivateNote(note: PrivateNoteEntity)

    @Delete
    suspend fun deletePrivateNote(note: PrivateNoteEntity)

    @Query("DELETE FROM private_notes WHERE id = :id")
    suspend fun deletePrivateNoteById(id: Long)

    @Query("DELETE FROM vault_entries")
    suspend fun clearAllVaultEntries()

    @Query("DELETE FROM private_notes")
    suspend fun clearAllPrivateNotes()
}
