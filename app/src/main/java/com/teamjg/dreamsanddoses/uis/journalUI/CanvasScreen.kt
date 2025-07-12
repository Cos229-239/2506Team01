package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun CanvasScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Canvas,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* backlog search logic */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Canvas,
                onCompose = {
                    navController.navigate(Routes.CANVAS_EDITOR)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CanvasRowSection("Recents", onSeeAll = { /* TODO */ })
            CanvasRowSection("This Month", onSeeAll = { /* TODO */ })
            CanvasRowSection("This Year", onSeeAll = { /* TODO */ })
        }
    }
}

@Composable
fun CanvasRowSection(title: String, onSeeAll: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onSeeAll) {
                Text("See All")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(5) { index ->
                CanvasProjectCard(name = "$title Project ${'$'}{index + 1}")
            }
        }
    }
}

@Composable
fun CanvasProjectCard(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Dimensions close to 8.5 x 11 inch paper scaled down (portrait aspect ratio)
        Card(
            modifier = Modifier
                .width(90.dp)
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {}

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(90.dp),
            maxLines = 1
        )
    }
}
