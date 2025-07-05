package com.teamjg.dreamsanddoses.uis

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

/**
 * Simple PDF viewer that shows PDF information and provides options to open it
 * This approach is more reliable than trying to display PDFs in WebView
 *
 * @param navController for navigation and back button handling
 * @param fileName the name of the PDF file to display
 */
@Composable
fun PDFViewerScreen(
    navController: NavController,
    fileName: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Get the full file path
    val pdfFile = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS), fileName)

    // State for file information
    var fileExists by remember { mutableStateOf(false) }
    var fileSize by remember { mutableStateOf(0L) }
    var lastModified by remember { mutableStateOf(0L) }

    // Load file information
    LaunchedEffect(fileName) {
        Log.d("PDFViewer", "Loading file info for: $fileName")
        Log.d("PDFViewer", "File path: ${pdfFile.absolutePath}")

        fileExists = pdfFile.exists()
        if (fileExists) {
            fileSize = pdfFile.length()
            lastModified = pdfFile.lastModified()
            Log.d("PDFViewer", "File exists, size: $fileSize bytes")
        } else {
            Log.e("PDFViewer", "File does not exist!")
        }
    }

    // Wrap the screen with animated transitions
    AnimatedScreenWrapper(navController = navController) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Files,
                    navController = navController,
                    useIconHeader = false,
                    onSearchClick = null // Remove search button to avoid confusion
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // PDF File Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "File Info",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "PDF Information",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

                        // File name
                        Text(
                            text = "File Name:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )

                        if (fileExists) {
                            // File size
                            Text(
                                text = "Size:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "${fileSize} bytes (${String.format("%.2f", fileSize / 1024.0)} KB)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            // Last modified
                            Text(
                                text = "Created:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(Date(lastModified)),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Action Buttons Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "View Options",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )

                        if (fileExists) {
                            // Open with external app button
                            Button(
                                onClick = {
                                    try {
                                        Log.d("PDFViewer", "Opening PDF externally: ${pdfFile.absolutePath}")
                                        com.teamjg.dreamsanddoses.uis.filesScreen.PDFExporter.openPDF(context, pdfFile)
                                    } catch (e: Exception) {
                                        Log.e("PDFViewer", "Failed to open PDF externally", e)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open with PDF Viewer")
                            }

                            // Info text
                            Text(
                                text = "This will open the PDF in your device's default PDF viewer app (like Adobe Reader, Google PDF Viewer, etc.)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        } else {
                            // File not found message
                            Text(
                                text = "PDF file not found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "The PDF file may have been moved or deleted.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Instructions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "About PDF Viewing",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "• PDFs are best viewed in dedicated PDF apps\n• Popular PDF viewers: Adobe Reader, Google PDF Viewer\n• You can zoom, search, and share from external viewers\n• The PDF will open in a new app and you can return here using the back button",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Back button
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Files")
                }
            }
        }
    }
}