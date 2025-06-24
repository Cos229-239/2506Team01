package com.teamjg.dreamsanddoses.uis

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Pills screen implementation using the back navigation wrapper
@Composable
fun PillsScreen(navController: NavController) {
    TopNavigationBar(
        type = NavigationBarType.Pills,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* backlog search logic */ }
    )

}