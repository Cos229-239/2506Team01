package com.teamjg.dreamsanddoses.uis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar


// Settings screen implementation using the back navigation wrapper
@Composable
fun SettingsScreen(navController: NavController) {

    BackHandler { /* Do nothing = disable back press & back swipe */ }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, type = NavigationBarType.Settings ) },
    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }


    TopNavigationBar(
        type = NavigationBarType.Settings,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* backlog search logic */ }
    )


}