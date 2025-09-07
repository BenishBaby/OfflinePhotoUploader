package com.example.uploadkit

import android.content.Context
import androidx.room.Room
import com.example.uploadkit.data.db.UploadDatabase
import com.example.uploadkit.domain.usecase.EnqueuePhotosUseCase
import com.example.uploadkit.domain.usecase.ObserveUploadsUseCase
import com.example.uploadkit.domain.usecase.RetryFailedUseCase
import com.example.uploadkit.infra.AndroidFileStore
import com.example.uploadkit.infra.FirebaseUploader
import com.example.uploadkit.infra.NetworkMonitor
import com.example.uploadkit.infra.QueueProcessor
import com.example.uploadkit.infra.UploadRepositoryImpl

object UploadKit {
    private lateinit var appContext: Context
    private lateinit var repository: UploadRepositoryImpl
    private lateinit var processor: QueueProcessor
    private lateinit var database: UploadDatabase

    /**
     * Initialize the UploadKit SDK. Call this method in your Application's onCreate().
     *
     * @param context The application context.
     */
    fun init(context: Context) {
        appContext = context.applicationContext
        val fileStore = AndroidFileStore(appContext)
        database = Room.databaseBuilder(appContext, UploadDatabase::class.java, "uploads.db").build()

        val dao = database.uploads()
        repository = UploadRepositoryImpl(dao, fileStore)

        val uploader = FirebaseUploader(appContext)
        val network = NetworkMonitor(appContext)
        processor = QueueProcessor(dao, uploader, network)
        processor.start()
    }

    /**
     * Call this when your app/service is shutting down to release resources.
     */
    fun shutdown() {
        if (::processor.isInitialized) {
            processor.stop()
        }
    }
}
