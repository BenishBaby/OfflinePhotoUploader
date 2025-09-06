package com.example.uploader.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.uploader.app.ui.MainScreen
import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadStatus
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun empty_state_is_visible() {
        val state = MutableStateFlow<List<UploadItem>>(emptyList())
        compose.setContent {
            MainScreen(stateFlow = state, onPick = {}, onRetry = {})
        }
        compose.onNodeWithText("No photos yet. Tap 'Select Photos' to begin.").assertIsDisplayed()
    }
}
