package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Journal" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun JournalTabContent() {
    // Placeholder list of journal entry file names (these would come from a backend or storage later)
    val journalEntries = listOf(
        "6/26/25 Journal Entry",
        "6/25/25 Journal Entry",
        "6/24/25 Journal Entry"
    )

    // Render the file list using the shared card component
    FileCardList(
        files = journalEntries,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}
