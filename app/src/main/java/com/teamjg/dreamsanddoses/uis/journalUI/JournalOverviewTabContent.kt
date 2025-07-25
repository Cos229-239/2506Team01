package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Main overview content for the central Journal tab.
 *
 * Displays key widgets such as Achievements and a quick access grid
 * for navigating to common journal features.
 *
 * @param navController Navigation controller to handle navigation actions.
 */
@Composable
fun JournalOverviewTabContent(navController: NavController) {
    // Use a vertical column layout to arrange content vertically with spacing
    Column(
        modifier = Modifier
            .fillMaxSize()               // Fill the maximum available size
            .padding(horizontal = 16.dp), // Horizontal padding around content
        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between child composables
    ) {
        // Spacer to add some vertical space at the top
        Spacer(Modifier.height(16.dp))

        // Section displaying user achievements (implementation assumed elsewhere)
        AchievementsSection()

        // Grid of quick access buttons for navigating journal-related screens
        JournalQuickAccessGrid(navController)
    }
}
