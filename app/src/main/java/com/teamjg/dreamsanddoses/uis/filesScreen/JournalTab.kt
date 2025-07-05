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
 * UI content for the "Journal" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun JournalTabContent(navController: NavController) {
    val context = LocalContext.current

    // Placeholder list of journal entry file names (these would come from a backend or storage later)
    val journalEntries = listOf(
        "6/26/25 Journal Entry",
        "6/25/25 Journal Entry",
        "6/24/25 Journal Entry"
    )

    //Keep track of which journal entries are selected
    var selectedEntries by remember { mutableStateOf(setOf<Int>()) }
    //Track if we're in selection mode
    var isSelectionMode by remember {mutableStateOf(false)}

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
                    text = "Select Journal Entries to Export",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = if (selectedEntries.isEmpty()) {
                        "Tap journal entries to select them for export"
                    } else {
                        "${selectedEntries.size} journal entry(s) selected"
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
                            selectedEntries = journalEntries.indices.toSet()
                            isSelectionMode = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select All")
                    }

                    // Clear Selection button
                    Button(
                        onClick = {
                            selectedEntries = setOf()
                            isSelectionMode = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }

                    // Export Selected button
                    Button(
                        onClick = {
                            if (selectedEntries.isEmpty()) {
                                Toast.makeText(context, "Please select at least one journal entry", Toast.LENGTH_SHORT).show()
                            } else {
                                // Get the selected journal entries as strings
                                val selectedEntriesList = selectedEntries.map { index ->
                                    journalEntries[index]
                                }

                                // Export to PDF
                                PDFExporter.exportJournalToPDF(
                                    context = context,
                                    journalEntries = selectedEntriesList,
                                    onComplete = { pdfFile ->
                                        Toast.makeText(context, "PDF created! Opening...", Toast.LENGTH_SHORT).show()
                                        // Open PDF within the app
                                        PDFExporter.openPDFInApp(navController, pdfFile.name)

                                        // Clear selection after successful export
                                        selectedEntries = setOf()
                                        isSelectionMode = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedEntries.isNotEmpty()
                    ) {
                        Text("Export PDF")
                    }
                }
            }
        }

        // List of journal entries with checkboxes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(journalEntries) { index, entry ->
                SelectableJournalCard(
                    journalEntry = entry,
                    isSelected = selectedEntries.contains(index),
                    onSelectionChanged = { isSelected ->
                        selectedEntries = if (isSelected) {
                            selectedEntries + index
                        } else {
                            selectedEntries - index
                        }
                        isSelectionMode = selectedEntries.isNotEmpty()
                    }
                )
            }
        }
    }
}

/**
 * A card that displays a journal entry with a checkbox for selection
 */
@Composable
fun SelectableJournalCard(
    journalEntry: String,
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

            // Journal entry text
            Text(
                text = journalEntry,
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
