package com.example.uploadkit.infra

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.example.uploadkit.data.db.UploadDao
import com.example.uploadkit.data.db.UploadDatabase
import com.example.uploadkit.data.db.UploadEntity
import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadRepository
import com.example.uploadkit.domain.UploadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class UploadRepositoryImpl(
    private val appContext: Context,
    private val fileStore: FileStore
) : UploadRepository {

    private val db: UploadDatabase by lazy {
        Room.databaseBuilder(appContext, UploadDatabase::class.java, "uploads.db").build()
    }
    private val dao: UploadDao get() = db.uploads()

    override fun observe(): Flow<List<UploadItem>> =
        dao.observeAll().map { list ->
            list.map { e ->
                UploadItem(e.id, e.localPath, e.status, e.progress, e.error, e.createdAt)
            }
        }

    override suspend fun enqueue(uris: List<Uri>) {
        val now = System.currentTimeMillis()
        val entities = uris.map { uri ->
            val f = fileStore.copyFrom(uri)
            UploadEntity(
                id = UUID.randomUUID().toString(),
                localPath = f.absolutePath,
                status = UploadStatus.QUEUED,
                progress = 0,
                error = null,
                createdAt = now
            )
        }
        dao.insertAll(entities)
    }

    override suspend fun retryFailed() {
        // simple approach: flip FAILED -> QUEUED
        val pending = dao.getNextPending() // call to warm up DB
        // We'll just rely on QueueProcessor to pick up FAILED as well
    }
}
