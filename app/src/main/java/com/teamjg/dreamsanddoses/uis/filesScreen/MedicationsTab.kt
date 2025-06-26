package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.runtime.Composable

/**
 * UI content for the "Medications" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun MedicationsTabContent() {
    // Placeholder list of medication reminders file names (these would come from a backend or storage later)
    val meds = listOf(
        "Melatonin 3mg - Nightly",
        "Ashwagandha - Morning & Evening",
        "Vitamin D3 - Weekly"
    )

    // Render the file list using the shared card component
    FileCardList(
        files = meds,
        showActionsOnTap = true // Enables tap-to-reveal action buttons
    )
}