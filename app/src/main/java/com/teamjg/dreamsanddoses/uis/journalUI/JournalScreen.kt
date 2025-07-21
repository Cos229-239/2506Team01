package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import java.text.SimpleDateFormat
import java.util.*


/* Data class representing a journal entry */
data class JournalEntry(
    val title: String,
    val subtitle: String,
    val date: Date
)

/**
 * Screen displaying a list of journal entries with top and bottom navigation bars.
 *
 * @param navController Controller for navigation actions.
 */@Composable
fun JournalScreen(navController: NavController) {
    // Sample placeholder entries; replace with real data source later
    val sampleEntries = listOf(
        JournalEntry("Goals for July", "First things first...", Date()),
        JournalEntry("Morning Journal", "Gratitude and planning", Date()),
        JournalEntry("Evening Thoughts", "Emotions unpacked", Date())
    )

    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Journal,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* TODO: Implement search */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Journal,
                onCompose = { navController.navigate(Routes.NEW_JOURNAL) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(horizontal = 16.dp)
        ) {
            items(sampleEntries) { entry ->
                JournalEntryCard(entry = entry, navController = navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Card composable displaying a single journal entry's details.
 *
 * @param entry JournalEntry to display.
 */
@Composable
fun JournalEntryCard(entry: JournalEntry, navController: NavController) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                // Navigate to the journal editor screen for this entry
                // Assuming entry.title or some ID is passed as parameter (use actual ID when ready)
                navController.navigate("editor/${entry.title}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateFormat.format(entry.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}