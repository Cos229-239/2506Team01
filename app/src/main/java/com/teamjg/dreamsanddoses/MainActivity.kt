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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

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
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.DarkGray,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController.navigate("settings")
                    }
            )
        }
    }
}

@Composable
fun JournalScreen(navController: NavController) {
    Text(text = "Journal Screen")
}

@Composable
fun CalendarScreen(navController: NavController) {
    Text(text = "Calendar Screen")
}


@Composable
fun SettingsScreen(navController: NavController) {
    var visible by remember { mutableStateOf(true) }

    // Launch navigation only after exit animation completes
    LaunchedEffect(visible) {
        if (!visible) {
            delay(300) // Match animation duration
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.fillMaxSize(),
            exit = slideOutHorizontally { fullWidth -> fullWidth } + fadeOut(animationSpec = tween(300)),
            enter = fadeIn()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.DarkGray,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                visible = false
                            }
                    )
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Settings Screen", color = Color.Black)
                }
            }
        }
    }
}