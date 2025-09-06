package com.example.uploader.app

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uploadkit.UploadKit
import com.example.uploadkit.domain.UploadItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UploadViewModel(app: Application) : AndroidViewModel(app) {
    val state: StateFlow<List<UploadItem>> =
        UploadKit.uploads().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun enqueue(uris: List<Uri>) {
        viewModelScope.launch { UploadKit.enqueue(uris) }
    }

    fun retry() {
        viewModelScope.launch { UploadKit.retry() }
    }

    object Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        operator fun invoke(app: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UploadViewModel(app) as T
            }
        }
    }
}
