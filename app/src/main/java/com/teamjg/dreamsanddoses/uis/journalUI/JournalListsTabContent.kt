package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import com.teamjg.dreamsanddoses.uis.filesScreen.FileCardList
import androidx.navigation.NavController

// Lists tab content – currently uses dummy file list
@Composable
fun ListsJournalTabContent(navController: NavController) {
    val lists = listOf("...", "..", "...")

    FileCardList(
        files = lists,
        showActionsOnTap = true,
        onViewClick = { fileName ->
            // Navigate to note editor screen (can be adapted to open actual saved note later)
            navController.navigate("lists/${fileName}")
        }
    )
}