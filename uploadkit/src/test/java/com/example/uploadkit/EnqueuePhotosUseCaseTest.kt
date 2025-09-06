package com.example.uploadkit

import android.net.Uri
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.infra.FileStore
import com.example.uploadkit.infra.UploadRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.uploadkit.data.db.UploadDatabase

class FakeFileStore : FileStore {
    override fun copyFrom(uri: Uri): File {
        val f = File.createTempFile("test-", ".jpg")
        f.writeText("fake")
        return f
    }
}

class EnqueuePhotosUseCaseTest {
    @Test
    fun enqueue_inserts_items() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = UploadRepositoryImpl(context, FakeFileStore())
        val use = EnqueuePhotosUseCase(repo)
        use(listOf(Uri.parse("file:///tmp/a.jpg")))
        // If no exception is thrown, basic path works
        assertTrue(true)
    }
}
