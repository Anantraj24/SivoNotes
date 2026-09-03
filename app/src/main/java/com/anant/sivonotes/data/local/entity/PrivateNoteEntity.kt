package com.anant.sivonotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "private_notes")
data class PrivateNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // Encrypted base64 ciphertext
    val encryptedContent: String, // Encrypted base64 ciphertext
    val iv: String, // Base64 initialization vector
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
