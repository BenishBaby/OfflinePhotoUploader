package com.example.uploader.app

import android.app.Application
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadStatus
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.domain.usecase.ObserveUploadsUseCase
import com.example.uploadkit.domain.usecase.RetryFailedUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UploadViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var enqueuePhotosUseCase: EnqueuePhotosUseCase
    private lateinit var observeUploadsUseCase: ObserveUploadsUseCase
    private lateinit var retryFailedUseCase: RetryFailedUseCase
    private lateinit var app: Application
    private lateinit var viewModel: UploadViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        enqueuePhotosUseCase = mock()
        observeUploadsUseCase = mock()
        retryFailedUseCase = mock()
        app = mock()
        val items = listOf(
            UploadItem("1", "/tmp/file1.jpg", UploadStatus.QUEUED, 0, null, 123L),
            UploadItem("2", "/tmp/file2.jpg", UploadStatus.SUCCESS, 100, null, 124L)
        )
        whenever(observeUploadsUseCase.invoke()).thenReturn(flowOf(items))
        viewModel = UploadViewModel(app, enqueuePhotosUseCase, observeUploadsUseCase, retryFailedUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state emits items from observeUploadsUseCase`() = runTest(testDispatcher) {
        val emitted = viewModel.state.first { it.size == 2 }
        assertEquals(2, emitted.size)
        assertEquals("1", emitted[0].id)
        assertEquals(UploadStatus.QUEUED, emitted[0].status)
        assertEquals("2", emitted[1].id)
        assertEquals(UploadStatus.SUCCESS, emitted[1].status)
    }

    @Test
    fun `enqueue calls enqueuePhotosUseCase`() = runTest(testDispatcher) {
        val uris: List<Uri> = listOf(mock())
        viewModel.enqueue(uris)
        testDispatcher.scheduler.advanceUntilIdle()
        verify(enqueuePhotosUseCase, times(1)).invoke(uris)
    }

    @Test
    fun `retry calls retryFailedUseCase`() = runTest(testDispatcher) {
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()
        verify(retryFailedUseCase, times(1)).invoke()
    }

    @Test
    fun `state emits empty list when observeUploadsUseCase returns empty`() = runTest(testDispatcher) {
        whenever(observeUploadsUseCase.invoke()).thenReturn(flowOf(emptyList()))
        viewModel = UploadViewModel(app, enqueuePhotosUseCase, observeUploadsUseCase, retryFailedUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        val emitted = viewModel.state.first { it.isEmpty() }
        assertEquals(0, emitted.size)
    }
}
