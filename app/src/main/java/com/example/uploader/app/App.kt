package com.example.uploader.app

import android.app.Application
import com.example.uploadkit.UploadKit
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        UploadKit.init(this)
    }
}
