package com.example.uploader.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.uploadkit.domain.UploadItem
import com.example.uploadkit.domain.UploadStatus
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    stateFlow: StateFlow<List<UploadItem>>,
    onPick: () -> Unit,
    onRetry: () -> Unit,
) {
    val items by stateFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Offline Photo Uploader") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onPick,
                text = { Text("Select Photos") },
                icon = { Icon(Icons.Default.Add, "Select Photos") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No photos yet. Tap 'Select Photos' to begin.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        UploadCard(item = item)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            FilledTonalButton(onClick = onRetry) {
                Text("Retry Failed")
            }
        }
    }
}

@Composable
private fun UploadCard(item: UploadItem) {
    ElevatedCard(Modifier) {
        AsyncImage(
            model = item.localPath,
            contentDescription = "Photo ${item.id}",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (item.status) {
                        UploadStatus.QUEUED -> "Queued"
                        UploadStatus.UPLOADING -> "Uploading ${item.progress}%"
                        UploadStatus.SUCCESS -> "Success"
                        UploadStatus.FAILED -> "Failed"
                    },
                    fontWeight = FontWeight.SemiBold
                )
                if (item.status == UploadStatus.UPLOADING) {
                    LinearProgressIndicator(progress = item.progress / 100f, modifier = Modifier.width(120.dp))
                }
            }
            item.error?.let { e ->
                if (item.status == UploadStatus.FAILED) {
                    Text(
                        text = e,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
