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
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessButton("Journal", Modifier.weight(1f), R.drawable.ic_journal_icon, 75.dp) {
                navController.navigate(Routes.JOURNAL)
            }
            QuickAccessButton("Canvas", Modifier.weight(1f), R.drawable.ic_main_logo_icon, 75.dp) {
                navController.navigate(Routes.CANVAS)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            QuickAccessButton("Dreams", Modifier.fillMaxWidth(0.5f), R.drawable.ic_dreams_icon, 75.dp) {
                navController.navigate(Routes.DREAMS_HOME)
            }
        }
    }
}
