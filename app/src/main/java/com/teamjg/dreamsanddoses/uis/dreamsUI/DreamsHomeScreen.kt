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


// Pills screen implementation using the back navigation wrapper
@Composable
fun DreamsHomeScreen(navController: NavController) {
    var morningNotification by remember { mutableStateOf(false) }
    Box(modifier = Modifier) {
        Scaffold(
            containerColor = Color.LightGray,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* backlog search logic */ }
                )
            },
            bottomBar = {
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
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.LightGray)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                DreamsWidgetSection(navController)
                Spacer(modifier = Modifier.height(18.dp))
                Box(modifier = Modifier.padding(horizontal = 36.dp)) {
                    ToggleRow("Morning Notification", morningNotification) { morningNotification = it }
                }
                Spacer(modifier = Modifier.height(12.dp))

                DreamsQuickAccessGrid(navController)
            }
        }
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp)   // This cuts into the BottomNavigationBar
                .clickable {
                    navController.navigate(Routes.DREAMS_EDITOR)
                },
            tint = Color.Unspecified    // preserves original icon coloring
        )
    }
}

@Composable
fun DreamsWidgetSection(navController: NavController) {
    val dreamEntries = remember { mutableStateListOf<DreamEntry>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            FirestoreService.fetchRecentDreams(it) { entries ->
                dreamEntries.clear()
                dreamEntries.addAll(entries)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFFE6F0FF), shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Header row
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
                        modifier = Modifier.align(Alignment.CenterEnd).clickable {
                            navController.navigate(Routes.DREAMS) {
                                launchSingleTop = true
                                popUpTo(Routes.HOME)
                            }
                        }
                    )
                }

                // Dream entry previews
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    dreamEntries.take(6).forEach { dream ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // TODO: Navigate to detailed view/edit
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

                    if (dreamEntries.isEmpty()) {
                        Text("No recent dreams yet", color = Color.Gray)
                    }
                }
            }
        }
    }
}


