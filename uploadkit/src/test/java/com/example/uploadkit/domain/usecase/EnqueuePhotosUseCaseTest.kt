package com.example.uploadkit.domain.usecase

import android.net.Uri
import com.example.uploadkit.domain.UploadRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.*

class EnqueuePhotosUseCaseTest {
    @Test
    fun `invoke calls enqueue on repository`() = runBlocking {
        val repo = mock(UploadRepository::class.java)
        val useCase = EnqueuePhotosUseCase(repo)
        val uris = listOf(mock(Uri::class.java), mock(Uri::class.java))
        useCase.invoke(uris)
        verify(repo, times(1)).enqueue(uris)
    }
}

