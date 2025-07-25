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

/**
 * Data class representing a single drawn line,
 * which consists of a mutable list of points (Offset).
 */
data class Line(val points: MutableList<Offset>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasEditorScreen(navController: NavController) {
    // Holds the list of completed lines drawn on the canvas
    var paths by remember { mutableStateOf<List<Line>>(emptyList()) }
    // Holds the currently drawn line (in-progress stroke)
    var currentPath by remember { mutableStateOf<Line?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Canvas Editor") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigate back to Canvas screen, keeping HOME in back stack
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
                // Clear all drawn lines (clear canvas)
                IconButton(onClick = {
                    paths = emptyList()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Canvas")
                }
                // Undo last drawn line (remove last path)
                IconButton(onClick = {
                    if (paths.isNotEmpty()) {
                        paths = paths.dropLast(1)
                    }
                }) {
                    Icon(Icons.Default.History, contentDescription = "Undo")
                }
                // Save current canvas to Firestore (or backend)
                IconButton(onClick = {
                    saveCanvas(
                        lines = paths,
                        title = "${System.currentTimeMillis()}", // Use timestamp as a temporary title
                        previewUrl = null // Could pass a preview image URL if available
                    ) { success ->
                        // On successful save, navigate back to Canvas screen
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
                // Listen for drag gestures to draw on the canvas
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // When drag starts, initialize a new Line with the start point
                            currentPath = Line(mutableListOf(offset))
                        },
                        onDrag = { change, _ ->
                            // While dragging, add new points to the current line
                            currentPath?.points?.add(change.position)
                        },
                        onDragEnd = {
                            // When drag ends, add the completed line to paths and reset currentPath
                            currentPath?.let {
                                paths = paths + it
                            }
                            currentPath = null
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw all completed lines plus the line currently being drawn (if any)
                (paths + listOfNotNull(currentPath)).forEach { line ->
                    val path = Path().apply {
                        // Move to the first point if available
                        if (line.points.isNotEmpty()) moveTo(line.points.first().x, line.points.first().y)
                        // Connect all subsequent points with line segments
                        for (point in line.points.drop(1)) {
                            lineTo(point.x, point.y)
                        }
                    }
                    // Draw the path with a black stroke, rounded caps and joins for smooth lines
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
