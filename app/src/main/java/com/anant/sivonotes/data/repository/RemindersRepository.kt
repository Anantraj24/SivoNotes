package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.ReminderDao
import com.anant.sivonotes.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class RemindersRepository(private val reminderDao: ReminderDao) {
    fun getAllReminders(): Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    fun getActiveUpcomingReminders(currentTimeMillis: Long): Flow<List<ReminderEntity>> = reminderDao.getActiveUpcomingReminders(currentTimeMillis)
    fun getNextUpcomingReminder(currentTimeMillis: Long): Flow<ReminderEntity?> = reminderDao.getNextUpcomingReminder(currentTimeMillis)
    fun getReminderById(id: Long): Flow<ReminderEntity?> = reminderDao.getReminderById(id)
    suspend fun getReminderByIdDirect(id: Long): ReminderEntity? = reminderDao.getReminderByIdDirect(id)
    fun searchReminders(query: String): Flow<List<ReminderEntity>> = reminderDao.searchReminders(query)
    suspend fun insertReminder(reminder: ReminderEntity): Long = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)
    suspend fun deleteReminderById(id: Long) = reminderDao.deleteReminderById(id)
    suspend fun getAllPendingRemindersDirect(): List<ReminderEntity> = reminderDao.getAllPendingRemindersDirect()

    suspend fun markCompleted(reminder: ReminderEntity) {
        reminderDao.updateReminder(reminder.copy(isCompleted = true))
    }
}
