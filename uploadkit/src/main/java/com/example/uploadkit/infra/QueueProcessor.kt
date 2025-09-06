package com.example.uploadkit.infra

import com.example.uploadkit.data.db.UploadDao
import com.example.uploadkit.data.db.UploadEntity
import com.example.uploadkit.domain.UploadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class QueueProcessor(
    private val dao: UploadDao,
    private val uploader: FirebaseUploader,
    private val network: NetworkMonitor,
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var runningJob: Job? = null

    fun start() {
        network.start()
        runningJob = scope.launch {
            network.online.collectLatest { online ->
                if (online) {
                    processLoop()
                }
            }
        }
    }

    private suspend fun processLoop() {
        while (true) {
            val next = dao.getNextPending() ?: break
            processOne(next)
            // small yield to avoid tight loop
            delay(100)
        }
    }

    private suspend fun processOne(item: UploadEntity) {
        try {
            dao.updateStatus(item.id, UploadStatus.UPLOADING.name, item.progress, null)
            val file = File(item.localPath)
            if (!file.exists()) throw IllegalStateException("File missing: ${item.localPath}")
            uploader.upload(file).collect { p ->
                dao.updateStatus(item.id, UploadStatus.UPLOADING.name, p, null)
            }
            dao.updateStatus(item.id, UploadStatus.SUCCESS.name, 100, null)
        } catch (t: Throwable) {
            dao.updateStatus(item.id, UploadStatus.FAILED.name, 0, t.message)
        }
    }

    fun stop() {
        runningJob?.cancel()
        network.stop()
        scope.cancel()
    }
}
