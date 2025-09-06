package com.example.uploader.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uploader.app.ui.MainScreen

class MainActivity : ComponentActivity() {

    private lateinit var vm: UploadViewModel // ViewModel for handling results

    // registerForActivityResult remains in MainActivity's onCreate (implicitly via property init)
    private val picker: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(20)
        ) { uris: List<Uri> ->
            // Ensure vm is initialized. It will be by the time this callback runs.
            if (::vm.isInitialized) {
                vm.enqueue(uris)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[UploadViewModel::class.java]
        setContent {
            MainScreen(
                stateFlow = vm.state,
                onPick = {
                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onRetry = { vm.retry() }
            )
        }
    }
}
