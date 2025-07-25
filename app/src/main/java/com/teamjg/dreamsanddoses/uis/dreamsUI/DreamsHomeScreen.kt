package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.*
import com.teamjg.dreamsanddoses.uis.FirestoreService
import com.teamjg.dreamsanddoses.uis.settingsUI.ToggleRow

/**
 * The main Dreams Home screen composable.
 * Displays a header, recent dream entries preview, and various UI elements including toggles and quick access grid.
 * Uses a Scaffold layout with top and bottom navigation bars and a floating action button for creating a new dream.
 *
 * @param navController NavController for navigation actions.
 */
@Composable
fun DreamsHomeScreen(navController: NavController) {
    // State to toggle morning notification preference (currently UI only)
    var morningNotification by remember { mutableStateOf(false) }

    Box(modifier = Modifier) {
        Scaffold(
            containerColor = Color.LightGray,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            topBar = {
                // Top app bar with Dreams navigation type and optional search icon callback
                TopNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* backlog search logic */ }
                )
            },
            bottomBar = {
                // Bottom navigation bar configured for Dreams tab and a compose button to open editor
                BottomNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    onCompose = { navController.navigate(Routes.DREAMS_EDITOR) },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)            // Padding to avoid system bars & scaffold content overlap
                    .verticalScroll(rememberScrollState())  // Enable vertical scrolling of content
                    .background(Color.LightGray)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Section showing recent dream entries with title and previews
                DreamsWidgetSection(navController)

                Spacer(modifier = Modifier.height(18.dp))

                // UI toggle for morning notifications
                Box(modifier = Modifier.padding(horizontal = 36.dp)) {
                    ToggleRow("Morning Notification", morningNotification) { morningNotification = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Placeholder for quick access grid or other widget elements
                DreamsQuickAccessGrid(navController)
            }
        }

        // Floating "New Dream" icon button manually placed above bottom navigation bar
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)                         // Large tap target
                .align(Alignment.BottomCenter)       // Positioned at bottom center
                .offset(y = (-32).dp)                 // Overlaps bottom navigation slightly
                .clickable {
                    // Navigate to the dream editor screen on tap
                    navController.navigate(Routes.DREAMS_EDITOR)
                },
            tint = Color.Unspecified                // Preserve original icon colors
        )
    }
}

/**
 * Widget section composable showing recent dreams preview.
 * Fetches the recent dreams for the logged-in user from Firestore on composition.
 * Displays a card-like container with a title, navigation icon, and up to six recent dreams.
 *
 * @param navController NavController for navigation (used for "go to memories" action).
 */
@Composable
fun DreamsWidgetSection(navController: NavController) {
    // State list holding recent DreamEntry objects
    val dreamEntries = remember { mutableStateListOf<DreamEntry>() }

    // Current user UID from Firebase Authentication
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Launch a side effect to fetch recent dreams whenever userId changes
    LaunchedEffect(userId) {
        userId?.let {
            // FirestoreService callback populates the dreamEntries list asynchronously
            FirestoreService.fetchRecentDreams(it) { entries ->
                dreamEntries.clear()
                dreamEntries.addAll(entries)
            }
        }
    }

    // Container Box styled as a card with light blue background and rounded corners
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)                             // Fixed height for the preview container
                .background(Color(0xFFE6F0FF), shape = RoundedCornerShape(16.dp))
                .padding(20.dp)                            // Inner padding for content spacing
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header row containing title centered and navigation arrow at right edge
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Memories",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to memories",
                        tint = Color(0xFF1A1A1A),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                // Navigate to Dreams full list screen with single top launch and pop up to Home
                                navController.navigate(Routes.DREAMS) {
                                    launchSingleTop = true
                                    popUpTo(Routes.HOME)
                                }
                            }
                    )
                }

                // Column listing up to six recent dream entries as title and date rows
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Show preview rows for each dream entry (max 6)
                    dreamEntries.take(6).forEach { dream ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // TODO: Implement navigation to detailed dream view or editing
                                }
                        ) {
                            Text(
                                text = dream.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = dream.date,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        }
                    }

                    // If no dreams are available, display a placeholder message
                    if (dreamEntries.isEmpty()) {
                        Text("No recent dreams yet", color = Color.Gray)
                    }
                }
            }
        }
    }
}
