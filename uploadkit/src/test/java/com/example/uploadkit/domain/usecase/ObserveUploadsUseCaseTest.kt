package com.example.uploadkit.domain.usecase

import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadRepository
import com.example.uploadkit.domain.UploadStatus
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*

class ObserveUploadsUseCaseTest {
    @Test
    fun `invoke returns flow from repository`() = runBlocking {
        val repo = mock(UploadRepository::class.java)
        val expected = listOf(
            UploadItem(
                id = "1",
                localPath = "/tmp/file1.jpg",
                status = UploadStatus.QUEUED,
                progress = 0,
                error = null,
                createdAt = 123456789L
            ),
            UploadItem(
                id = "2",
                localPath = "/tmp/file2.jpg",
                status = UploadStatus.QUEUED,
                progress = 0,
                error = null,
                createdAt = 123456790L
            )
        )
        `when`(repo.observe()).thenReturn(flowOf(expected))
        val useCase = ObserveUploadsUseCase(repo)
        val result = useCase.invoke()
        val items = result.first()
        assertEquals(expected, items)
    }
}
