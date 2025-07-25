package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.teamjg.dreamsanddoses.uis.filesScreen.FileCardList
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.data.FireStoreService

/**
 * Composable displaying the Notes tab content within the Journal screen.
 *
 * Fetches note entries from Firestore for the logged-in user and
 * displays them as a list of cards. Supports navigation to individual note details.
 *
 * @param navController Navigation controller to handle navigation actions.
 */
@Composable
fun JournalNotesTabContent(navController: NavController) {
    // Obtain current user's ID from Firebase Authentication
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // State holding the list of note titles to display
    var notes by remember { mutableStateOf<List<String>>(emptyList()) }

    // Effect to fetch notes whenever the userId changes or on first composition
    LaunchedEffect(userId) {
        if (userId != null) {
            // Fetch notes entries asynchronously from Firestore for the user
            FireStoreService.fetchNotesEntries(userId) { entries ->
                // Extract the title from each note entry for display in the UI
                notes = entries.map { it.title }
            }
        }
    }

    // Display the list of note titles as cards with tap actions enabled
    FileCardList(
        files = notes,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            // Navigate to the detailed view screen for the selected note by its title
            navController.navigate("notes/${fileName}")
        }
    )
}
