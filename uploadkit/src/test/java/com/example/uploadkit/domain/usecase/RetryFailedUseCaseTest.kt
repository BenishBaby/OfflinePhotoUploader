package com.example.uploadkit.domain.usecase

import com.example.uploadkit.domain.UploadRepository
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.*

class RetryFailedUseCaseTest {
    @Test
    fun `invoke calls retryFailed on repository`() = runBlocking {
        val repo = mock(UploadRepository::class.java)
        val useCase = RetryFailedUseCase(repo)
        useCase.invoke()
        verify(repo, times(1)).retryFailed()
    }
}

