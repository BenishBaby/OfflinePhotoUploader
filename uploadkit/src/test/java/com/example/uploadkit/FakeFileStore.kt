package com.example.uploadkit

import android.net.Uri
import com.example.uploadkit.infra.FileStore
import java.io.File

class FakeFileStore : FileStore {
    override fun copyFrom(uri: Uri): File {
        val f = File.createTempFile("test-", ".jpg")
        f.writeText("fake")
        return f
    }
}

