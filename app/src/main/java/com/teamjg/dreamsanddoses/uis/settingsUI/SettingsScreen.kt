package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    // Disable (Android) back button and back swipe to prevent accidental exit
    BackHandler { /* no-op */ }

    // State holders for toggles and dialog visibility
    var darkMode by remember { mutableStateOf(false) }
    var orientation by remember { mutableStateOf(true) }
    var orangeTint by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = remember { mutableStateOf(false) }
    var currentSetting by remember { mutableStateOf("Account") }

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
                    onClick = {
                        currentSetting = label
                        showBottomSheet.value = true
                    }
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

        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet.value = false },
                sheetState = sheetState,
                dragHandle = null,
                containerColor = Color.Transparent // So we can shape our own container
            ) {
                // Card-like modal content
                Box(
                    modifier = Modifier
                        .fillMaxSize()  /*  TODO/BUG: Clicking dimmed portion will not close modal  */
                        .padding(horizontal = 16.dp, vertical = 80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Top row with close icon and title
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showBottomSheet.value = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }
                            Text(
                                text = "$currentSetting Settings",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // TODO: Content block (placeholder)
                        when (currentSetting) {
                            "Account" -> Text("Account settings content here...")
                            "Notifications" -> Text("Notification settings content here...")
                            "Privacy" -> Text("Privacy settings content here...")
                            "Appearance" -> Text("Appearance settings content here...")
                        }
                    }
                }
            }
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


@Composable
fun AccountSettingsScreen(navController: NavController) {

    Scaffold(
        // Top navigation bar with journal icon and optional search
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Journal,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* TODO: implement search action */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Respect Scaffold padding
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Spacing at top
            // TODO: Insert journal content here
        }
    }
}
