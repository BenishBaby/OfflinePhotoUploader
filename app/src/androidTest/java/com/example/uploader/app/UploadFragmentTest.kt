package com.example.uploader.app

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipData
import android.content.ContentResolver // Added for resource URIs
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.uploader.R // Added to access R.raw resources
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class UploadFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        Intents.init()
        hiltRule.inject() // Initialize Hilt components
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun initialState_showsEmptyGalleryMessage() {
        composeTestRule.onNodeWithText("No photos yet. Tap \'Select Photos\' to begin.").assertIsDisplayed()
    }

    @Test
    fun clickSelectPhotos_launchesImagePicker() {
        val expectedIntent = allOf(
            hasAction(MediaStore.ACTION_PICK_IMAGES),
            hasType("image/*")
        )
        Intents.intending(expectedIntent).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null)
        )
        composeTestRule.onNodeWithText("Select Photos", useUnmergedTree = true).performClick()
        Intents.intended(expectedIntent)
    }

    @Test
    fun selectPhotos_resultsInItemsDisplayed() {
        // 1. Prepare mock URIs from raw resources and ActivityResult for the photo picker
        val packageName = composeTestRule.activity.packageName

        // IMPORTANT: Ensure you have 'sample_image_1.jpg' (or .png etc.) and 'sample_image_2.png'
        // (or similar) in your app/src/main/res/raw/ folder.
        // The resource names in R.raw will be 'sample_image_1' and 'sample_image_2' (without extension).
        val imageUri1 = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.raw.image2}")
        val imageUri2 = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.raw.image3}")
        val resourceUris = listOf(imageUri1, imageUri2)

        val resultIntent = Intent().apply {
            val clipData = ClipData.newUri(composeTestRule.activity.contentResolver, "uris", resourceUris.first())
            for (i in 1 until resourceUris.size) {
                clipData.addItem(ClipData.Item(resourceUris[i]))
            }
            this.clipData = clipData
            // Add FLAG_GRANT_READ_URI_PERMISSION for good measure,
            // though ContentResolver usually handles android.resource UIs directly.
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val activityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)

        // 2. Stub the intent for picking multiple images
        Intents.intending(
            allOf(
                hasAction(MediaStore.ACTION_PICK_IMAGES),
                hasType("image/*")
            )
        ).respondWith(activityResult)

        // 3. Click the "Select Photos" button
        composeTestRule.onNodeWithText("Select Photos", useUnmergedTree = true).performClick()

        // 4. Verify that items for the selected photos are displayed
        // This assumes that newly added items will result in Composables
        // containing the text "Queued" (e.g., in an UploadCard).
        composeTestRule.onAllNodesWithText("Queued").assertCountEquals(resourceUris.size)

        // Verify that the "No photos yet..." message is gone
        composeTestRule.onNodeWithText("No photos yet. Tap \'Select Photos\' to begin.").assertDoesNotExist()
    }
}
