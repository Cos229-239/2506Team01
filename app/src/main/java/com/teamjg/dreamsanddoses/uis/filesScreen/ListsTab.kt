package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Lists" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun ListsTabContent() {
    // Placeholder list of lists file names (these would come from a backend or storage later)
    val lists = listOf(
        "Grocery list",
        "Christmas 2025",
        "House projects"
    )

    // Render the file list using the shared card component
    FileCardList(
        files = lists,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}


