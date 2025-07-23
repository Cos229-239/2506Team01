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

// Notes tab content – currently uses dummy file list
@Composable
fun JournalNotesTabContent(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var notes by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            FireStoreService.fetchNotesEntries(userId) { entries ->
                // You can use title only for display, or show tag if needed later
                notes = entries.map { it.title }
            }
        }
    }

    FileCardList(
        files = notes,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            navController.navigate("notes/${fileName}")
        }
    )

}
