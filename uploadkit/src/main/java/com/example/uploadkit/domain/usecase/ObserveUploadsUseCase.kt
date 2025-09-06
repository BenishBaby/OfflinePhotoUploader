package com.example.uploadkit.domain.usecase

import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadRepository
import kotlinx.coroutines.flow.Flow

class ObserveUploadsUseCase(private val repo: UploadRepository) {
    operator fun invoke(): Flow<List<UploadItem>> = repo.observe()
}
