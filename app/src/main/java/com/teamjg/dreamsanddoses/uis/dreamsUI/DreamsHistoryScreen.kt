package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.Routes


data class DreamEntry(
    val title: String,
    val date: String,
    val description: String = ""
)

val sampleDreams = listOf(
    DreamEntry("Flying Through Clouds", "July 10, 2025", "I flew above mountains with lavender clouds."),
    DreamEntry("Falling Elevator", "July 8, 2025", "The floor vanished and I woke up startled."),
    DreamEntry("Endless Forest", "July 6, 2025", "Trees whispered my name as I walked deeper...")
)


// Dreams screen implementation using the back navigation wrapper
@Composable
fun DreamsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.DreamsHistory,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* TODO */ }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    type = NavigationBarType.Dreams,
                    onCompose = { navController.navigate(Routes.DREAMS) },
                    includeCenterFab = false
                )
            },
            containerColor = Color.LightGray
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(sampleDreams) { dream ->
                    DreamCard(dream)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Custom Compose Button manually overlaid
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp) // This cuts into the BottomNavigationBar
                .clickable {
                    navController.navigate(Routes.CANVAS_EDITOR)
                },
            tint = Color.Unspecified // preserves original icon coloring
        )
    }
}



@Composable
fun DreamCard(dream: DreamEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(dream.title, style = MaterialTheme.typography.titleMedium)
            Text(dream.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            if (dream.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    dream.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }
        }
    }
}

