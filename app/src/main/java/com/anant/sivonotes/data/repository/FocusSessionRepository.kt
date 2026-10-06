package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.FocusSessionDao
import com.anant.sivonotes.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(private val focusSessionDao: FocusSessionDao) {

    fun getAllSessions(): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getAllSessions()

    fun getCompletedSessions(): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getCompletedSessions()

    fun getSessionsByDate(startOfDay: Long, endOfDay: Long): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getSessionsByDate(startOfDay, endOfDay)

    fun getSessionsForTodo(todoId: Long): Flow<List<FocusSessionEntity>> =
        focusSessionDao.getSessionsForTodo(todoId)

    suspend fun insertSession(session: FocusSessionEntity): Long =
        focusSessionDao.insertSession(session)

    suspend fun updateSession(session: FocusSessionEntity) =
        focusSessionDao.updateSession(session)

    suspend fun deleteSession(session: FocusSessionEntity) =
        focusSessionDao.deleteSession(session)

    suspend fun deleteSessionById(id: Long) =
        focusSessionDao.deleteSessionById(id)
}
