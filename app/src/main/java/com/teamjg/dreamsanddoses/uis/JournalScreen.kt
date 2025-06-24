package com.teamjg.dreamsanddoses.uis

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

/**  Journal screen composable
     Uses Scaffold with a top navigation bar configured for Journal
     Placeholder for journal content and future search functionality  **/
@Composable
fun JournalScreen(navController: NavController) {

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