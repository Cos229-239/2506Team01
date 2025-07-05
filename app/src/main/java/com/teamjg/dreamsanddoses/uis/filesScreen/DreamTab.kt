package com.teamjg.dreamsanddoses.uis.filesScreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.navigation.NavController

/**
 * UI content for the "Dreams" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun DreamsTabContent(navController: NavController) {
    val context = LocalContext.current

    // PLACEHOLDER DREAMS - In a real app, this would come from a database
    val dreams = listOf(
        "Dream about the forest with glowing leaves",
        "Sailing on an ocean of green water",
        "Lucid dream"
    )

    // Keep track of which dreams are selected (using their index in the list)
    var selectedDreams by remember { mutableStateOf(setOf<Int>()) }

    // State to track if we're in selection mode
    var isSelectionMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header section with selection controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Dreams to Export",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (selectedDreams.isEmpty()) {
                        "Tap dreams to select them for export"
                    } else {
                        "${selectedDreams.size} dream(s) selected"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Control buttons row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Select All button
                    Button(
                        onClick = {
                            selectedDreams = dreams.indices.toSet()
                            isSelectionMode = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select All")
                    }

                    // Clear Selection button
                    Button(
                        onClick = {
                            selectedDreams = setOf()
                            isSelectionMode = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }

                    // Export Selected button
                    Button(
                        onClick = {
                            if (selectedDreams.isEmpty()) {
                                Toast.makeText(context, "Please select at least one dream", Toast.LENGTH_SHORT).show()
                            } else {
                                // Get the selected dreams as strings
                                val selectedDreamsList = selectedDreams.map { index ->
                                    dreams[index]
                                }

                                // Export to PDF
                                PDFExporter.exportDreamsToPDF(
                                    context = context,
                                    dreams = selectedDreamsList,
                                    onComplete = { pdfFile ->
                                        Toast.makeText(context, "PDF created! Opening...", Toast.LENGTH_SHORT).show()
                                        // Open PDF within the app
                                        PDFExporter.openPDFInApp(navController, pdfFile.name)

                                        // Clear selection after successful export
                                        selectedDreams = setOf()
                                        isSelectionMode = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedDreams.isNotEmpty()
                    ) {
                        Text("Export PDF")
                    }
                }
            }
        }

        // List of dreams with checkboxes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(dreams) { index, dream ->
                SelectableDreamCard(
                    dream = dream,
                    isSelected = selectedDreams.contains(index),
                    onSelectionChanged = { isSelected ->
                        selectedDreams = if (isSelected) {
                            selectedDreams + index
                        } else {
                            selectedDreams - index
                        }
                        isSelectionMode = selectedDreams.isNotEmpty()
                    }
                )
            }
        }
    }
}

/**
 * A card that displays a dream with a checkbox for selection
 * Based on SelectableMedicationCard but for dreams
 */
@Composable
fun SelectableDreamCard(
    dream: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Dream text
            Text(
                text = dream,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}