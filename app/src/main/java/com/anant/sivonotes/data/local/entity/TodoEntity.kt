package com.anant.sivonotes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todos",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["folderId"]),
        Index(value = ["isCompleted"]),
        Index(value = ["dueDate"])
    ]
)
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null, // epoch day timestamp in millis (start of day)
    val dueTime: String? = null, // formatted e.g. "10:00 AM" or "14:30"
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val repeatRule: String = "NONE", // "NONE", "DAILY", "WEEKDAYS", "WEEKLY", "CUSTOM"
    val folderId: Long? = null,
    val reminderTimeMillis: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
