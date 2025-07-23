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

// Lists tab content
@Composable
fun ListsJournalTabContent(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var lists by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            FireStoreService.fetchListEntries(userId) { entries ->
                // You can use title only for display, or show tag if needed later
                lists = entries.map { it.title }
            }
        }
    }

    FileCardList(
        files = lists,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            navController.navigate("lists/${fileName}")
        }
    )

}