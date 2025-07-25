package com.teamjg.dreamsanddoses.uis.journalUI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.uis.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale


@Composable
fun CanvasScreen(navController: NavController, viewModel: CanvasViewModel = viewModel()) {
    val canvasGroups by viewModel.canvasGroups.collectAsState()
    val timeLabels = listOf("Past Week", "This Month", "This Year", "Older")

    val backgroundGray = Color.LightGray

    Scaffold(
        containerColor = backgroundGray,
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Canvas,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* backlog search logic */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Canvas,
                onCompose = {
                    navController.navigate(Routes.CANVAS_EDITOR)
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                timeLabels.forEach { label ->
                    val previews = canvasGroups[label] ?: emptyList()
                    CanvasRowSection(
                        title = label,
                        canvasPreviews = previews,
                        onSeeAll = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasRowSection(
    title: String,
    canvasPreviews: List<FirestoreService.CanvasPreview>,
    onSeeAll: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onSeeAll) {
                Text("See All")
            }
        }

        if (canvasPreviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "No projects yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                canvasPreviews.forEach { preview ->
                    CanvasProjectCard(
                        name = preview.title,
                        previewUrl = preview.previewUrl
                    )
                }
            }
        }
    }
}

@Composable
fun CanvasProjectCard(name: String, previewUrl: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .width(90.dp)
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            if (previewUrl != null) {
                Log.d("CanvasCard", "Loading image for: $name, url: $previewUrl")
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            } else {
                Log.d("CanvasCard", "No preview URL for: $name")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val formattedDate = try {
            val timestamp = name.toLong()
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date)
        } catch (e: Exception) {
            name // fallback if not a number
        }

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(90.dp),
            maxLines = 1
        )

    }
}

class CanvasViewModel : ViewModel() {
    private val _canvasGroups = MutableStateFlow<Map<String, List<FirestoreService.CanvasPreview>>>(emptyMap())
    val canvasGroups: StateFlow<Map<String, List<FirestoreService.CanvasPreview>>> = _canvasGroups

    init {
        fetchAndGroupCanvases()
    }

    private fun fetchAndGroupCanvases() {
        FirestoreService.fetchCanvasPreviews(
            onResult = { previews ->
                Log.d("CanvasViewModel", "Fetched ${previews.size} previews")
                val grouped = previews.groupBy { preview ->
                    val daysAgo = preview.createdAt.toDate().let {
                        val diff = System.currentTimeMillis() - it.time
                        TimeUnit.MILLISECONDS.toDays(diff)
                    }

                    when {
                        daysAgo < 7 -> "Past Week"
                        daysAgo < 30 -> "This Month"
                        daysAgo < 365 -> "This Year"
                        else -> "Older"
                    }
                }
                _canvasGroups.value = grouped
            },
            onError = { error ->
                Log.e("CanvasViewModel", "Failed to fetch canvas previews", error)
            }
        )
    }
}
