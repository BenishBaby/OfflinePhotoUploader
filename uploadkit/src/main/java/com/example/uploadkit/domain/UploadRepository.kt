package com.example.uploadkit.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface UploadRepository {
    fun observe(): Flow<List<UploadItem>>
    suspend fun enqueue(uris: List<Uri>)
    suspend fun retryFailed()
}
