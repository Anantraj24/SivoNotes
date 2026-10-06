package com.anant.sivonotes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = TodoEntity::class,
            parentColumns = ["id"],
            childColumns = ["todoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["todoId"]),
        Index(value = ["isCompleted"]),
        Index(value = ["dateKey"])
    ]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val todoId: Long? = null,
    val todoTitle: String,
    val targetDurationMillis: Long,
    val actualDurationMillis: Long,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isCompleted: Boolean = false,
    val isAbandoned: Boolean = false,
    val dateKey: Long = System.currentTimeMillis() // Start-of-day millis or epoch timestamp for streak matching
)
