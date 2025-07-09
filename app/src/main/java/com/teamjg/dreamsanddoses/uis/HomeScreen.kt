package com.teamjg.dreamsanddoses.uis

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType

// Main Home screen with logo, widgets, quick access buttons, and bottom navigation
@Composable
fun HomeScreen(navController: NavController) {
    // This will override system back navigation on this screen
    BackHandler { /* Do nothing = disable back press & back swipe */ }
    
        Scaffold(
        bottomBar = { BottomNavigationBar(navController, NavigationBarType.Home) } // Bottom navigation bar
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray) // Background color for the entire screen
                .padding(innerPadding) // Account for Scaffold padding
                .padding(horizontal = 16.dp, vertical = 8.dp) // Outer padding
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp)) // Spacing from top

                // App logo centered horizontally
                Image(
                    painter = painterResource(id = R.drawable.ic_main_logo_icon),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(115.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 24.dp)
                )

                HomeWidgetSection() // Calendar & Reminders widget section

                Spacer(modifier = Modifier.height(24.dp)) // Space between widgets and quick access

                HomeQuickAccessGrid(navController) // Quick access buttons grid

            }
        }
    }
}

/**  Widget section combining calendar preview and reminders
     Uses a rounded box with soft background color  */
@Composable
fun HomeWidgetSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Unified Calendar & Reminders Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    color = Color(0xFFE6F0FF), // Soft pastel background color
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // Calendar section header and placeholder
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A) // Dark text for clarity
                    )
                    Text(
                        text = "[ Calendar Preview ]",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                // Reminders section header and placeholder
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Reminders",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A)
                    )
                    Text(
                        text = "[ Reminder 1, Reminder 2... ]",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

/**  Grid of quick access buttons for navigation
     Arranged in two rows with spacing  **/
@Composable
fun HomeQuickAccessGrid(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // First row with Journal and Reminders buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessButton(
                title = "Journal",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.ic_journal_icon,
                iconSize = 70.dp,
                onClick = {
                    navController.navigate(com.teamjg.dreamsanddoses.navigation.Routes.JOURNAL_HOME) {
                        launchSingleTop = true
                        popUpTo(com.teamjg.dreamsanddoses.navigation.Routes.HOME)
                    }
                }
            )

            QuickAccessButton(
                title = "Reminders",
                modifier = Modifier
                    .weight(1f),
                iconResId = R.drawable.ic_prescription_dosage_assistant,
                iconSize = 45.dp,
                onClick = {
                    navController.navigate(com.teamjg.dreamsanddoses.navigation.Routes.PILLS) {
                        launchSingleTop = true
                        popUpTo(com.teamjg.dreamsanddoses.navigation.Routes.HOME)
                    }
                }
            )
        }

        // Second row with Dreams and Files buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessButton(
                title = "Dreams",
                modifier = Modifier.weight(1f),
            iconResId = R.drawable.ic_dreams_icon,
            iconSize = 75.dp,
            onClick = {
                navController.navigate(com.teamjg.dreamsanddoses.navigation.Routes.DREAMS) {
                    launchSingleTop = true
                    popUpTo(com.teamjg.dreamsanddoses.navigation.Routes.HOME)
                }
            }
            )
            QuickAccessButton(
                title = "Files",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.ic_files_icon,
                iconSize = 75.dp,
                onClick = {
                    navController.navigate(com.teamjg.dreamsanddoses.navigation.Routes.FILES) {
                        launchSingleTop = true
                        popUpTo(com.teamjg.dreamsanddoses.navigation.Routes.HOME)
                    }
                }
            )
        }
    }
}

/**  A single quick access button with an icon and a label
     Supports custom icon size and click behavior  **/
@Composable
fun QuickAccessButton(
    title: String,
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    iconSize: Dp = 24.dp,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() }, // Handles button click
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Render icon with specific offset for "Reminders" button
            iconResId?.let {
                // Fix for alignment issues with the icon
                val iconModifier = if (title == "Reminders") {
                    Modifier
                        .size(iconSize)
                        .offset(x = (-6).dp, y = (8).dp)
                } else {
                    Modifier.size(iconSize)
                }

                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "$title Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = iconModifier
                )
            }

            // Button title text
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

