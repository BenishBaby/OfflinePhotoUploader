package com.example.uploadkit.domain.usecase

import com.example.uploadkit.domain.UploadRepository
import javax.inject.Inject

class RetryFailedUseCase @Inject constructor(private val repo: UploadRepository) {
    suspend operator fun invoke() = repo.retryFailed()
}
