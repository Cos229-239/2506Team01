package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "All Files" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun AllFilesTabContent() {
    // Example logic - show all files from every category
    val allFiles = listOf(
        "Dream_Notes.txt", "medication_log.pdf", "June_Journal.md", "todo_list.json"
    )
    // Render the file list using the shared card component
    FileCardList(
        files = allFiles,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}
