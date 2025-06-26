package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Exports" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun ExportedTabContent() {
    val lists = listOf(
        // Placeholder list of exported file names (these would come from a backend or storage later)
        "prescriptions_05222025.pdf",
        "prescriptions_02162025.pdf",
        "prescriptions_03182025.pdf"
    )

    // Render the file list using the shared card component
    FileCardList(
        files = lists,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}