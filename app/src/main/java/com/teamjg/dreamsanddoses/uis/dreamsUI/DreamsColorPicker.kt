package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Enum to represent gradient color schemes
enum class GradientMode {
    DEFAULT, WARM, COOL, PASTEL
}

@Composable
fun DreamsColorPicker(navController: NavController) {
    var selectedColor by remember { mutableStateOf(Color.White) } // Selected color state
    val swatches = remember { mutableStateListOf<Color>() } // Saved swatches
    var gradientMode by remember { mutableStateOf(GradientMode.DEFAULT) } // Current gradient mode
    var showDialog by remember { mutableStateOf(false) } // Show gradient picker dialog
    var brightness by remember { mutableFloatStateOf(1f) } // Brightness control (alpha)

    // Predefined gradient color pairs for different modes
    val gradientAnchors = mapOf(
        GradientMode.DEFAULT to Pair(Color(0xFFFFA726), Color(0xFFAB47BC)), // orange to purple
        GradientMode.WARM to Pair(Color(0xFFFF7043), Color(0xFFFFC107)),
        GradientMode.COOL to Pair(Color(0xFF4FC3F7), Color(0xFF1DE9B6)),
        GradientMode.PASTEL to Pair(Color(0xFFF8BBD0), Color(0xFFCE93D8))
    )

    // Use current gradient anchors or fallback to gray tones
    val anchorColors = gradientAnchors[gradientMode] ?: Pair(Color.Gray, Color.DarkGray)

    // Generate intermediate gradient shades between anchor colors
    val gradientShades = generateGradientShades(anchorColors.first, anchorColors.second)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .windowInsetsPadding(WindowInsets.statusBars),
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* TODO: Search logic */ }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    onCompose = { navController.navigate(Routes.DREAMS_EDITOR) },
                    includeCenterFab = false,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.LightGray)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select a color to associate!", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive color wheel
                ColorWheel(
                    gradientColors = gradientShades.map { it.copy(alpha = brightness) },
                    onColorSelected = { selectedColor = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Preview of selected color
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(selectedColor, shape = CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Gradient picker dialog
                if (showDialog) {
                    GradientPickerDialog(
                        currentMode = gradientMode,
                        gradientAnchors = gradientAnchors,
                        onSelect = {
                            gradientMode = it
                            showDialog = false
                        },
                        onDismiss = { showDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Swatch selector and action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(gradientShades) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, shape = CircleShape)
                                    .border(
                                        width = if (selectedColor == color) 3.dp else 1.dp,
                                        color = if (selectedColor == color) Color.Black else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Add color to swatches
                        IconButton(onClick = {
                            if (!swatches.contains(selectedColor)) {
                                swatches.add(selectedColor)
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Color")
                        }

                        // Open gradient picker
                        IconButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Change Gradient")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brightness adjustment slider
                Text("Brightness", style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = brightness,
                    onValueChange = { brightness = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Floating Compose Icon
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp)
                .clickable {
                    navController.navigate(Routes.DREAMS_EDITOR)
                },
            tint = Color.Unspecified
        )
    }
}

@Composable
fun ColorWheel(
    gradientColors: List<Color>,
    onColorSelected: (Color) -> Unit
) {
    val wheelSize = 200.dp
    val wheelPx = with(LocalDensity.current) { wheelSize.toPx() }

    Box(
        modifier = Modifier
            .size(wheelSize)
            .pointerInput(gradientColors) {
                detectTapGestures { offset ->
                    // Convert tap Y coordinate into a color selection
                    val yRatio = offset.y / wheelPx
                    val clampedRatio = yRatio.coerceIn(0f, 1f)
                    val index = ((gradientColors.size - 1) * clampedRatio).toInt()
                    val selected = gradientColors.getOrNull(index) ?: Color.Gray
                    onColorSelected(selected)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.verticalGradient(colors = gradientColors),
                radius = size.minDimension / 2,
                center = center
            )
        }

        Text("Color Wheel", color = Color.White)
    }
}

@Composable
fun GradientPickerDialog(
    currentMode: GradientMode,
    gradientAnchors: Map<GradientMode, Pair<Color, Color>>,
    onSelect: (GradientMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Gradient") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GradientMode.entries.forEach { mode ->
                    val previewColor = gradientAnchors[mode]?.first ?: Color.Gray
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(previewColor, CircleShape)
                                .border(
                                    width = if (mode == currentMode) 2.dp else 0.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(mode.name)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Generates 'steps' colors blending from start to end color
fun generateGradientShades(start: Color, end: Color, steps: Int = 5): List<Color> {
    return List(steps) { i ->
        val ratio = i / (steps - 1).toFloat() // Compute position along gradient
        Color(
            red = lerp(start.red, end.red, ratio),
            green = lerp(start.green, end.green, ratio),
            blue = lerp(start.blue, end.blue, ratio),
            alpha = 1f
        )
    }
}

// Linearly interpolates between start and end based on ratio (0f..1f)
fun lerp(start: Float, end: Float, ratio: Float): Float {
    return start + (end - start) * ratio
}
