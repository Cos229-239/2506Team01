package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.uis.QuickAccessButton


// Quick-access buttons beneath the achievements section
@Composable
fun JournalQuickAccessGrid(navController: NavController) {
    // Column to arrange rows vertically with spacing of 16.dp between rows
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()  // Make column fill the entire available width
    ) {
        // First row contains two quick access buttons, spaced evenly with 16.dp between
        Row(
            modifier = Modifier.fillMaxWidth(),                  // Row fills full width
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Spacing between buttons
        ) {
            // QuickAccessButton for "Journal" that takes up half the row's width
            QuickAccessButton(
                "Journal",
                Modifier.weight(1f),                             // Equal weight for equal width
                R.drawable.ic_journal_icon,
                75.dp
            ) {
                // Navigate to Journal screen on click
                navController.navigate(Routes.JOURNAL)
            }

            // QuickAccessButton for "Canvas" that takes up half the row's width
            QuickAccessButton(
                "Canvas",
                Modifier.weight(1f),                             // Equal weight for equal width
                R.drawable.ic_main_logo_icon,
                75.dp
            ) {
                // Navigate to Canvas screen on click
                navController.navigate(Routes.CANVAS)
            }
        }

        // Second row contains a single "Dreams" quick access button centered horizontally
        Row(
            modifier = Modifier.fillMaxWidth(),                  // Row fills full width
            horizontalArrangement = Arrangement.Center            // Center content horizontally
        ) {
            // QuickAccessButton for "Dreams" occupying half the width of the row
            QuickAccessButton(
                "Dreams",
                Modifier.fillMaxWidth(0.5f),                      // Fill 50% of row width
                R.drawable.ic_dreams_icon,
                75.dp
            ) {
                // Navigate to Dreams Home screen on click
                navController.navigate(Routes.DREAMS_HOME)
            }
        }
    }
}
