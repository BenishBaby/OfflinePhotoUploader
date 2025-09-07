package com.example.uploader.app

    import android.app.Application
    import android.content.Context
    import androidx.test.runner.AndroidJUnitRunner
    import dagger.hilt.android.testing.HiltTestApplication // Ensure this import is correct if HiltTestApplication is in a different package.

    // Custom Test Runner for Hilt
    class CustomTestRunner : AndroidJUnitRunner() {
        override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
            return super.newApplication(cl, HiltTestApplication::class.java.name, context)
        }
    }
