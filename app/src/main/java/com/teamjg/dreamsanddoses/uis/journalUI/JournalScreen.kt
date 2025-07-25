package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.data.FireStoreService
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import java.text.SimpleDateFormat
import java.util.*


/* Data class representing a journal entry with a title, subtitle, and date */
data class JournalEntry(
    val title: String,
    val subtitle: String,
    val date: Date
)

/**
 * Screen composable that displays a scrollable list of journal entries.
 * It includes a top app bar and a bottom navigation bar.
 *
 * @param navController NavController to handle navigation between screens.
 */
@Composable
fun JournalScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid  // Get current user ID
    var entries by remember { mutableStateOf<List<JournalEntry>>(emptyList()) }  // State holding journal entries list

    // Fetch journal entries from Firestore whenever userId changes
    LaunchedEffect(userId) {
        if (userId != null) {
            FireStoreService.fetchJournalEntries(userId) { result ->
                entries = result  // Update entries state with fetched data
            }
        }
    }

    // Scaffold layout providing top and bottom bars and content area
    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Journal,  // Use journal-specific top bar styling
                navController = navController,
                useIconHeader = true,               // Use icon in header
                onSearchClick = { /* TODO: Implement search */ }  // Placeholder for search action
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Journal,  // Use journal-specific bottom navigation
                onCompose = { navController.navigate(Routes.NEW_JOURNAL) }  // Navigate to new journal creation screen on compose
            )
        }
    ) { innerPadding ->
        // LazyColumn for efficient vertical scrolling of a list of entries
        LazyColumn(
            contentPadding = innerPadding,      // Padding from scaffold inner insets
            modifier = Modifier
                .fillMaxSize()                   // Fill all available space
                .background(Color.LightGray)    // Light gray background color for the list
                .padding(horizontal = 16.dp)    // Horizontal padding inside the list
        ) {
            // Iterate over each journal entry and display a card for it
            items(entries) { entry ->
                JournalEntryCard(entry = entry, navController = navController)  // Show individual entry card
                Spacer(modifier = Modifier.height(12.dp))                      // Spacing between entries
            }
        }
    }
}

/**
 * Card composable displaying details of a single journal entry.
 * Shows the title, subtitle, and formatted date. Card is clickable.
 *
 * @param entry JournalEntry object containing entry data.
 * @param navController NavController for navigation on card click.
 */
@Composable
fun JournalEntryCard(entry: JournalEntry, navController: NavController) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Formatter for displaying the date

    Card(
        modifier = Modifier
            .fillMaxWidth()               // Card fills the width of its parent
            .wrapContentHeight()          // Height wraps content
            .clickable {
                // Navigate to the editor screen for this journal entry when card is clicked
                // NOTE: This currently passes the title as identifier; ideally pass a unique ID instead
                navController.navigate("editor/${entry.title}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),  // Card background color
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)                      // Card elevation/shadow
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display journal entry title in large typography style
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Display journal entry subtitle with medium body typography and surface variant color
            Text(
                text = entry.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display formatted date of the journal entry with small label typography
            Text(
                text = dateFormat.format(entry.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
