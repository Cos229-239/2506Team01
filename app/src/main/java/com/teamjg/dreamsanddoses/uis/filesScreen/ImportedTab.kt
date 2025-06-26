package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Imports" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun ImportedTabContent() {
    val lists = listOf(
        // Placeholder list of imported file names (these would come from a backend or storage later)
        "Screenshot 453.jpeg",
        "Screenshot 222.jpeg",
        "Screenshot 125.png",
    )

    // Render the file list using the shared card component
    FileCardList(
        files = lists,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}