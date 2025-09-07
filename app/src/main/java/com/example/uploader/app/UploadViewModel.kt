package com.example.uploader.app

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.domain.usecase.ObserveUploadsUseCase
import com.example.uploadkit.domain.usecase.RetryFailedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class UploadViewModel @Inject constructor(
    app: Application,
    private val enqueuePhotosUseCase: EnqueuePhotosUseCase,
    observeUploadsUseCase: ObserveUploadsUseCase,
    private val retryFailedUseCase: RetryFailedUseCase
) : AndroidViewModel(app) {
    val state: StateFlow<List<UploadItem>> =
        observeUploadsUseCase().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun enqueue(uris: List<Uri>) {
        viewModelScope.launch { enqueuePhotosUseCase(uris) }
    }

    fun retry() {
        viewModelScope.launch { retryFailedUseCase() }
    }
}
