package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import com.teamjg.dreamsanddoses.uis.filesScreen.FileCardList

// Lists tab content – currently uses dummy file list
@Composable
fun ListsJournalTabContent() {
    val lists = listOf("...", "..", "...")

    FileCardList(
        files = lists,
        showActionsOnTap = true
    )
}