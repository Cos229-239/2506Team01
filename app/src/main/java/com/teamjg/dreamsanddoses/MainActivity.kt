package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("journal") { JournalScreen(navController) }
        composable("calendar") { CalendarScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}

@Composable
fun HomeScreen(navController: androidx.navigation.NavController) {
    Text(text = "Home Screen")
}

@Composable
fun JournalScreen(navController: androidx.navigation.NavController) {
    Text(text = "Journal Screen")
}

@Composable
fun CalendarScreen(navController: androidx.navigation.NavController) {
    Text(text = "Calendar Screen")
}

@Composable
fun SettingsScreen(navController: androidx.navigation.NavController) {
    Text(text = "Settings Screen")
}

//// Main activity inherits from ComponentActivity, the base class for Jetpack Compose activities
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

// Composable function displays a simple blank screen
@Composable
fun HomeScreen() {
    Box( //// Box is a layout that lets you stack children on top of each other
        modifier = Modifier
            .fillMaxSize() //// Tells the Box to take up the full screen
            .background(Color.LightGray) //// Applies a light gray background to visualize the space
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
