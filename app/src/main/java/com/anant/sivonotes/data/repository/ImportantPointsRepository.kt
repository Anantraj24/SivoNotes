package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.ImportantPointDao
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import kotlinx.coroutines.flow.Flow

class ImportantPointsRepository(private val pointDao: ImportantPointDao) {

    fun getAllPoints(): Flow<List<ImportantPointEntity>> = pointDao.getAllPoints()

    fun getPointsByFolder(folderId: Long): Flow<List<ImportantPointEntity>> = pointDao.getPointsByFolder(folderId)

    fun searchPoints(query: String): Flow<List<ImportantPointEntity>> = pointDao.searchPoints(query)

    suspend fun insertPoint(point: ImportantPointEntity): Long = pointDao.insertPoint(point)

    suspend fun updatePoint(point: ImportantPointEntity) = pointDao.updatePoint(point)

    suspend fun deletePoint(point: ImportantPointEntity) = pointDao.deletePoint(point)

    suspend fun deletePointById(id: Long) = pointDao.deletePointById(id)

    suspend fun togglePointCompleted(point: ImportantPointEntity) {
        pointDao.updatePoint(
            point.copy(
                isCompleted = !point.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
