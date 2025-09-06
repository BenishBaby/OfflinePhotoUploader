package com.example.uploadkit.domain.usecase

import android.net.Uri
import com.example.uploadkit.domain.UploadRepository

class EnqueuePhotosUseCase(private val repo: UploadRepository) {
    suspend operator fun invoke(uris: List<Uri>) = repo.enqueue(uris)
}
