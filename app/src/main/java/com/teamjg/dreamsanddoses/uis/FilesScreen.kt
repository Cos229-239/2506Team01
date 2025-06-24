package com.teamjg.dreamsanddoses.uis

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Files screen implementation using the back navigation wrapper
@Composable
fun FilesScreen(navController: NavController) {
    TopNavigationBar(
        type = NavigationBarType.Files,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* backlog search logic */ }
    )
}
