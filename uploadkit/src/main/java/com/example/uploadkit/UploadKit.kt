package com.example.uploadkit

import android.content.Context
import androidx.room.Room
import com.example.uploadkit.data.db.UploadDatabase
import com.example.uploadkit.domain.UploadRepository
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.domain.usecase.ObserveUploadsUseCase
import com.example.uploadkit.domain.usecase.RetryFailedUseCase
import com.example.uploadkit.infra.*

object UploadKit {
    private lateinit var appContext: Context
    private lateinit var repository: UploadRepositoryImpl
    private lateinit var processor: QueueProcessor
    private lateinit var database: UploadDatabase

    val uploads by lazy { ObserveUploadsUseCase(repository) }
    val enqueue by lazy { EnqueuePhotosUseCase(repository) }
    val retry by lazy { RetryFailedUseCase(repository) }

    fun init(context: Context) {
        appContext = context.applicationContext
        val fileStore = AndroidFileStore(appContext)
        repository = UploadRepositoryImpl(appContext, fileStore)

        database = Room.databaseBuilder(appContext, UploadDatabase::class.java, "uploads.db").build()
        val dao = database.uploads()
        val uploader = FirebaseUploader(appContext)
        val network = NetworkMonitor(appContext)
        processor = QueueProcessor(dao, uploader, network)
        processor.start()
    }
}
