package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


// Central tab for Journal – includes widgets and shortcuts
@Composable
fun JournalOverviewTabContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        AchievementsSection()
        JournalQuickAccessGrid(navController)
    }
}
