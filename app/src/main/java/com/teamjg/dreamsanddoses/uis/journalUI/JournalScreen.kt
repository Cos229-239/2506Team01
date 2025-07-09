package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Journal screen implementation using the back navigation wrapper
@Composable
fun JournalScreen(navController: NavController) {
    TopNavigationBar(
        type = NavigationBarType.Journal,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* backlog search logic */ }
    )

}