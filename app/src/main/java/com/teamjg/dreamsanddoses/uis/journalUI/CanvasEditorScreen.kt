package com.teamjg.dreamsanddoses.uis.journalUI


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.uis.FirestoreService.saveCanvas

data class Line(val points: MutableList<Offset>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasEditorScreen(navController: NavController) {
    var paths by remember { mutableStateOf<List<Line>>(emptyList()) }
    var currentPath by remember { mutableStateOf<Line?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canvas Editor") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.CANVAS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = {
                    paths = emptyList()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Canvas")
                }
                IconButton(onClick = {
                    if (paths.isNotEmpty()) {
                        paths = paths.dropLast(1)
                    }
                }) {
                    Icon(Icons.Default.History, contentDescription = "Undo") }
                IconButton(onClick = {
                    saveCanvas(
                        lines = paths,
                        title = "${System.currentTimeMillis()}",
                        previewUrl = null // or pass a URL if generated
                    ) { success ->
                        if (success) navController.navigate(Routes.CANVAS)
                    }
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save Canvas")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Line(mutableListOf(offset))
                        },
                        onDrag = { change, _ ->
                            currentPath?.points?.add(change.position)
                        },
                        onDragEnd = {
                            currentPath?.let {
                                paths = paths + it
                            }
                            currentPath = null
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                (paths + listOfNotNull(currentPath)).forEach { line ->
                    val path = Path().apply {
                        if (line.points.isNotEmpty()) moveTo(line.points.first().x, line.points.first().y)
                        for (point in line.points.drop(1)) {
                            lineTo(point.x, point.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }
    }
}
