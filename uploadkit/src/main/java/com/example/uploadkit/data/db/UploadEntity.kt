package com.example.uploadkit.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uploadkit.domain.UploadStatus

@Entity(tableName = "uploads")
data class UploadEntity(
    @PrimaryKey val id: String,
    val localPath: String,
    val status: UploadStatus,
    val progress: Int,
    val error: String?,
    val createdAt: Long,
)
