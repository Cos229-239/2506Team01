package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Notes" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun NotesTabContent() {
    // Placeholder list of notes file names (these would come from a backend or storage later)
    val notes = listOf(
        "Wife's VIN #...",
        "Meeting with therapist notes",
        "Order reference #..."
    )

    // Render the file list using the shared card component
    FileCardList(
        files = notes,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}
