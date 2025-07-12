package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import com.teamjg.dreamsanddoses.uis.filesScreen.FileCardList
import androidx.navigation.NavController

// Notes tab content – currently uses dummy file list
@Composable
fun JournalNotesTabContent(navController: NavController) {
    val notes = listOf("...", "..", "...")

    FileCardList(
        files = notes,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            // Navigate to note editor screen (can be adapted to open actual saved note later)
            navController.navigate("notes/${fileName}")
        }
    )
}
