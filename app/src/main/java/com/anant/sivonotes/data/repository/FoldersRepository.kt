package com.anant.sivonotes.data.repository

import com.anant.sivonotes.data.local.dao.FolderDao
import com.anant.sivonotes.data.local.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

class FoldersRepository(private val folderDao: FolderDao) {

    fun getAllFolders(): Flow<List<FolderEntity>> = folderDao.getAllFolders()

    fun getFolderById(id: Long): Flow<FolderEntity?> = folderDao.getFolderById(id)

    suspend fun getFolderByIdDirect(id: Long): FolderEntity? = folderDao.getFolderByIdDirect(id)

    suspend fun insertFolder(folder: FolderEntity): Long = folderDao.insertFolder(folder)

    suspend fun updateFolder(folder: FolderEntity) = folderDao.updateFolder(folder)

    suspend fun deleteFolder(folder: FolderEntity) = folderDao.deleteFolder(folder)

    suspend fun deleteFolderById(id: Long) = folderDao.deleteFolderById(id)

    fun getFolderCount(): Flow<Int> = folderDao.getFolderCount()
}
