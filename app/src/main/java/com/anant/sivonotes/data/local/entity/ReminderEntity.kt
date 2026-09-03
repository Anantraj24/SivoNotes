package com.anant.sivonotes.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["targetTimeMillis"]),
        Index(value = ["isCompleted"])
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String = "",
    val targetTimeMillis: Long,
    val repeatRule: String = "NEVER", // "NEVER", "DAILY", "WEEKLY", "MONTHLY"
    val isCompleted: Boolean = false,
    val linkedEntityId: Long? = null,
    val linkedEntityType: String? = null, // "TODO", "NOTE", "GENERAL"
    val createdAt: Long = System.currentTimeMillis()
)
