package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Dreams" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun DreamsTabContent() {
    // Placeholder list of dream journal file names (these would come from a backend or storage later)
    val dreams = listOf(
        "Dream about the forest with glowing leaves",
        "Sailing on an ocean of green water",
        "Lucid dream"
    )

    // Render the file list using the shared card component
    FileCardList(
        files = dreams,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}