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
import com.teamjg.dreamsanddoses.data.ChecklistItem

/**
 * Composable for the "Lists" tab content in the Journal screen.
 *
 * It fetches the user's lists from Firestore and displays them
 * using a reusable FileCardList component.
 *
 * @param navController Navigation controller to handle navigation events.
 */
@Composable
fun ListsJournalTabContent(navController: NavController) {
    // Get the currently authenticated user's ID (null if not logged in)
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Holds the list of list titles fetched from Firestore, starts empty
    var lists by remember { mutableStateOf<List<String>>(emptyList()) }

    // Launches a side-effect to fetch lists when userId changes or first composition
    LaunchedEffect(userId) {
        if (userId != null) {
            // Fetch the list entries from Firestore asynchronously
            FireStoreService.fetchListEntries(userId) { entries ->
                // Extract only the title of each list entry for display
                lists = entries.map { it.title }
            }
        }
    }

    // Display the list of list titles as file cards.
    // showActionsOnTap enables UI actions on tapping the card (e.g., edit, delete).
    FileCardList(
        files = lists,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            // Navigate to the detailed view for the selected list by its title
            navController.navigate("lists/${fileName}")
        }
    )
}
