package com.example.uploader.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.example.uploader.app.ui.MainScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vm: UploadViewModel by viewModels() // ViewModel for handling results

    // registerForActivityResult remains in MainActivity's onCreate (implicitly via property init)
    private val picker: ActivityResultLauncher<PickVisualMediaRequest> =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(20)
        ) { uris: List<Uri> ->
            // Ensure vm is initialized. It will be by the time this callback runs.
            vm.enqueue(uris)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
