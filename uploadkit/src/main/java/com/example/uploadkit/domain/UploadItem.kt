package com.example.uploadkit.domain

data class UploadItem(
    val id: String,
    val localPath: String,
    val status: UploadStatus,
    val progress: Int,
    val error: String?,
    val createdAt: Long,
)
