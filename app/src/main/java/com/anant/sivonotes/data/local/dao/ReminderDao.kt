package com.anant.sivonotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anant.sivonotes.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY targetTimeMillis ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders ORDER BY targetTimeMillis ASC")
    suspend fun getAllRemindersDirect(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND targetTimeMillis >= :currentTimeMillis ORDER BY targetTimeMillis ASC")
    fun getActiveUpcomingReminders(currentTimeMillis: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND targetTimeMillis >= :currentTimeMillis ORDER BY targetTimeMillis ASC LIMIT 1")
    fun getNextUpcomingReminder(currentTimeMillis: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderByIdDirect(id: Long): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY targetTimeMillis ASC")
    fun searchReminders(query: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)

    @Query("SELECT * FROM reminders WHERE isCompleted = 0")
    suspend fun getAllPendingRemindersDirect(): List<ReminderEntity>
}
