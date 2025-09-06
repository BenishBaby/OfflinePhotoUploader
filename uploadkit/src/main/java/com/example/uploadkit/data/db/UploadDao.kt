package com.example.uploadkit.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadDao {
    @Query("SELECT * FROM uploads ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<UploadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<UploadEntity>)

    @Update
    suspend fun update(item: UploadEntity)

    @Query("SELECT * FROM uploads WHERE status IN ('QUEUED', 'FAILED') ORDER BY createdAt ASC LIMIT 1")
    suspend fun getNextPending(): UploadEntity?

    @Query("UPDATE uploads SET status=:status, progress=:progress, error=:error WHERE id=:id")
    suspend fun updateStatus(id: String, status: String, progress: Int, error: String?)

    @Query("DELETE FROM uploads")
    suspend fun clearAll()
}
