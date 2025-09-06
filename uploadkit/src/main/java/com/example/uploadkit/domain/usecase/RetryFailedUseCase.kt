package com.example.uploadkit.domain.usecase

import com.example.uploadkit.domain.UploadRepository

class RetryFailedUseCase(private val repo: UploadRepository) {
    suspend operator fun invoke() = repo.retryFailed()
}
