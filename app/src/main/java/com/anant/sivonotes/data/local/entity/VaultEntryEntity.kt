package com.anant.sivonotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_entries")
data class VaultEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // Encrypted base64 ciphertext
    val username: String, // Encrypted base64 ciphertext
    val encryptedPassword: String, // Encrypted base64 ciphertext
    val websiteUrl: String = "", // Encrypted base64 ciphertext
    val notes: String = "", // Encrypted base64 ciphertext
    val iv: String, // Base64 initialization vector
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
