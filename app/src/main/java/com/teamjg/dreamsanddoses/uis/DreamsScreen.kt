package com.teamjg.dreamsanddoses.uis

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar


// Dreams screen implementation using the back navigation wrapper
@Composable
fun DreamsScreen(navController: NavController) {
    TopNavigationBar(
        type = NavigationBarType.Dreams,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* backlog search logic */ }
    )
}
