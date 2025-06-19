package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay



// Main entry point of the application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation() // Set up the app's navigation
        }
    }
}

// Root navigation controller with route definitions
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("journal") { JournalScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("pills") { PillsScreen(navController) }
    }
}

// Main Home screen with bottom navigation and FAB
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.DarkGray,
                            )
                        }
                        IconButton(onClick = { navController.navigate("pills") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_prescription_dosage_assistant),
                                contentDescription = "Pills"
                            )
                        }
                        IconButton(
                            onClick = {
                                // TODO: Add "Create New" logic
                            },
                            modifier = Modifier.size(64.dp) // Make the button and icon larger
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create New",
                                modifier = Modifier.size(64.dp) // Larger icon size
                            )
                        }
                        IconButton(onClick = { navController.navigate("journal") }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Journal")
                        }
                        IconButton(onClick = { navController.navigate("calendar") }) {
                            Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content area of Home Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Home Screen", color = Color.Black)
                }
            }
        }
    }
}

// Generic reusable screen wrapper with back navigation and entrance/exit animation
@Composable
fun ScreenWithBackNavigation(
    navController: NavController,
    content: @Composable ColumnScope.() -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    // Handle navigation after fade/slide animation
    LaunchedEffect(visible) {
        if (!visible) {
            delay(300)
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
        .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            exit = slideOutHorizontally { fullWidth -> fullWidth } + fadeOut(tween(300)),
            enter = fadeIn(),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top back button
                Row(
                    modifier = Modifier
                    .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.DarkGray,
                        modifier = Modifier
                        .size(48.dp)
                            .clickable { visible = false }
                    )
                }
                content() // Insert screen-specific content
            }
        }
    }
}

// Journal screen implementation using the back navigation wrapper
@Composable
fun JournalScreen(navController: NavController) {
    ScreenWithBackNavigation(navController) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Journal Screen", color = Color.Black)
        }
    }
}

// Calendar screen implementation using the back navigation wrapper
@Composable
fun CalendarScreen(navController: NavController) {
    ScreenWithBackNavigation(navController) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Calendar Screen", color = Color.Black)
        }
    }
}

// Settings screen implementation using the back navigation wrapper
@Composable
fun SettingsScreen(navController: NavController) {
    ScreenWithBackNavigation(navController) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Settings Screen", color = Color.Black)
        }
    }
}

// Pills screen implementation using the back navigation wrapper
@Composable
fun PillsScreen(navController: NavController) {
    ScreenWithBackNavigation(navController) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Prescription Dosage Assistant", color = Color.Black)
        }
    }
}
