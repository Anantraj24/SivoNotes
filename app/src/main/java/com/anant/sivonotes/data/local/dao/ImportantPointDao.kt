package com.anant.sivonotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.anant.sivonotes.data.local.entity.ImportantPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImportantPointDao {
    @Query("SELECT * FROM important_points ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllPoints(): Flow<List<ImportantPointEntity>>

    @Query("SELECT * FROM important_points WHERE folderId = :folderId ORDER BY isCompleted ASC, createdAt DESC")
    fun getPointsByFolder(folderId: Long): Flow<List<ImportantPointEntity>>

    @Query("SELECT * FROM important_points WHERE text LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchPoints(query: String): Flow<List<ImportantPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: ImportantPointEntity): Long

    @Update
    suspend fun updatePoint(point: ImportantPointEntity)

    @Delete
    suspend fun deletePoint(point: ImportantPointEntity)

    @Query("DELETE FROM important_points WHERE id = :id")
    suspend fun deletePointById(id: Long)

    @Query("SELECT COUNT(*) FROM important_points WHERE folderId = :folderId")
    fun getPointCountByFolder(folderId: Long): Flow<Int>
}
