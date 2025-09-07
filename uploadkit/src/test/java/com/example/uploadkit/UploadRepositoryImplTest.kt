package com.example.uploadkit

import android.net.Uri
import androidx.room.Room
import com.example.uploadkit.data.db.UploadDao
import com.example.uploadkit.data.db.UploadDatabase
import com.example.uploadkit.domain.UploadStatus
import com.example.uploadkit.infra.UploadRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class UploadRepositoryImplTest {
    private lateinit var db: UploadDatabase
    private lateinit var dao: UploadDao
    private lateinit var repo: UploadRepositoryImpl

    @Before
    fun setup() {
        val context = RuntimeEnvironment.getApplication()
        db = Room.inMemoryDatabaseBuilder(context, UploadDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.uploads()
        repo = UploadRepositoryImpl(dao, FakeFileStore())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun enqueue_inserts_items() = runBlocking {
        val uris = listOf(Uri.parse("file:///tmp/a.jpg"), Uri.parse("file:///tmp/b.jpg"))
        repo.enqueue(uris)
        val items = repo.observe().first()
        assertEquals(2, items.size)
        assertTrue(items.all { it.status == UploadStatus.QUEUED })
    }

    @Test
    fun observe_returns_items() = runBlocking {
        val uris = listOf(Uri.parse("file:///tmp/c.jpg"))
        repo.enqueue(uris)
        val items = repo.observe().first()
        assertEquals(1, items.size)
        val file = File(items[0].localPath)
        assertTrue(file.exists())
        assertTrue(file.name.endsWith(".jpg"))
    }

    @Test
    fun retryFailed_does_not_throw_and_handles_failed_items() = runBlocking {
        // Insert a failed item manually
        val failedEntity = com.example.uploadkit.data.db.UploadEntity(
            id = "failed-id",
            localPath = "/tmp/failed.jpg",
            status = UploadStatus.FAILED,
            progress = 0,
            error = "Some error",
            createdAt = System.currentTimeMillis()
        )
        dao.insertAll(listOf(failedEntity))
        // Should not throw and should allow QueueProcessor to pick up FAILED
        repo.retryFailed()
        // Check that the failed item is still present and status is unchanged (since retryFailed is a no-op)
        val items = repo.observe().first()
        assertTrue(items.any { it.id == "failed-id" && it.status == UploadStatus.FAILED })
    }
}
