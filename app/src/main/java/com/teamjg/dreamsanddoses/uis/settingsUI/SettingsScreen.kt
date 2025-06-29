package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar


@Composable
fun SettingsScreen(navController: NavController) {

    // Disable (Android) back button and back swipe to prevent accidental exit
    BackHandler { /* no-op */ }

    // State holders for toggles and dialog visibility
    var darkMode by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf(true) }
    var orangeTint by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Scaffold provides layout slots for top/bottom bars and content
    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Settings,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* TODO: Implement search */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, type = NavigationBarType.Settings)
        }
    ) { innerPadding ->

        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Section: Navigation buttons to sub-settings screens
            listOf("Account", "Notifications", "Privacy", "Appearance").forEach { label ->
                ActionButton(
                    text = label,
                    backgroundColor = Color(0xFFE0E0E0),
                    onClick = { /* TODO: Navigate to $label settings */ }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Toggle switches for user preferences
            ToggleRow("Dark Mode", darkMode) { darkMode = it }
            ToggleRow("Orientation Locked", orientation) { orientation = it }
            ToggleRow("Orange Tint", orangeTint) { orangeTint = it }

            Spacer(modifier = Modifier.height(16.dp))

            // Section: Actions related to session and preferences
            ActionButton("Apply", Color(0xFFE0E0E0))
            ActionButton("Discard Changes", Color(0xFFE0E0E0))

            // Show logout confirmation dialog on click
            ActionButton("Logout", Color.Black, Color.White) {
                showLogoutDialog = true
            }

            // Placeholder for future account switch functionality
            ActionButton("Switch Account", Color.Gray, Color.White) {
                // TODO: Handle switch account logic
                showLogoutDialog = true
            }
        }

        // Dialog: Confirm logout action
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) // Clears backstack so user can't navigate back
                    }
                },
                onDismiss = { showLogoutDialog = false }
            )

        }
    }
}

//@Composable
//fun AccountSettingsScreen(navController: NavController) {
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)) {
//        Text("Account Settings", style = MaterialTheme.typography.headlineMedium)
//        // Add settings toggles, options, etc.
//    }
//}
