package com.example.uploadkit.infra

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File

/** Abstraction so we can fake in tests. */
interface FileStore {
    fun copyFrom(uri: Uri): File
}

class AndroidFileStore(
    private val context: Context,
    private val subdir: String = "upload-cache"
) : FileStore {
    override fun copyFrom(uri: Uri): File {
        val dir = File(context.filesDir, subdir).apply { mkdirs() }
        val ext = ".jpg"
        val file = File(dir, java.util.UUID.randomUUID().toString() + ext)
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IllegalStateException("Cannot open stream for $uri")
        return file
    }
}
