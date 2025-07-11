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
 * UI content for the "Medications" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun MedicationsTabContent(navController: NavController) {
    val context = LocalContext.current

    // PLACEHOLDER MEDICATIONS - In a real app, this would come from a database
    val medications = listOf(
        "Melatonin 3mg - Nightly",
        "Ashwagandha - Morning & Evening",
        "Vitamin D3 - Weekly",
        "Ibuprofen 200mg - As needed",
        "Multivitamin - Daily"
    )

    // Keep track of which medications are selected (using their index in the list)
    var selectedMedications by remember { mutableStateOf(setOf<Int>()) }

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
                    text = "Select Medications to Export",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (selectedMedications.isEmpty()) {
                        "Tap medications to select them for export"
                    } else {
                        "${selectedMedications.size} medication(s) selected"
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
                            selectedMedications = medications.indices.toSet()
                            isSelectionMode = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select All")
                    }

                    // Clear Selection button
                    Button(
                        onClick = {
                            selectedMedications = setOf()
                            isSelectionMode = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }

                    // Export Selected button
                    Button(
                        onClick = {
                            if (selectedMedications.isEmpty()) {
                                Toast.makeText(context, "Please select at least one medication", Toast.LENGTH_SHORT).show()
                            } else {
                                // Get the selected medications as strings
                                val selectedMedicationList = selectedMedications.map { index ->
                                    medications[index]
                                }

                                // Export to PDF
                                PDFExporter.exportMedicationsToPDF(
                                    context = context,
                                    medications = selectedMedicationList,
                                    onComplete = { pdfFile ->
                                        Toast.makeText(context, "PDF created! Opening...", Toast.LENGTH_SHORT).show()
                                        // Open PDF within the app instead of external viewer
                                        PDFExporter.openPDFInApp(navController, pdfFile.name)

                                        // Clear selection after successful export
                                        selectedMedications = setOf()
                                        isSelectionMode = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedMedications.isNotEmpty()
                    ) {
                        Text("Export PDF")
                    }
                }
            }
        }

        // List of medications with checkboxes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(medications) { index, medication ->
                SelectableMedicationCard(
                    medication = medication,
                    isSelected = selectedMedications.contains(index),
                    onSelectionChanged = { isSelected ->
                        selectedMedications = if (isSelected) {
                            selectedMedications + index
                        } else {
                            selectedMedications - index
                        }
                        isSelectionMode = selectedMedications.isNotEmpty()
                    }
                )
            }
        }
    }
}

/**
 * A card that displays a medication with a checkbox for selection
 */
@Composable
fun SelectableMedicationCard(
    medication: String,
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

            // Medication name
            Text(
                text = medication,
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