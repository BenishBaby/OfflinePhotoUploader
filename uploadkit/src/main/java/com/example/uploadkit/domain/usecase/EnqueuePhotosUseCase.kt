package com.example.uploadkit.domain.usecase

import android.net.Uri
import com.example.uploadkit.domain.UploadRepository
import javax.inject.Inject

class EnqueuePhotosUseCase @Inject constructor(private val repo: UploadRepository) {
    suspend operator fun invoke(uris: List<Uri>) = repo.enqueue(uris)
}
