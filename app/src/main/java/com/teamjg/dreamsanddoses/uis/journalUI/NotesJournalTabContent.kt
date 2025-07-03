package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.runtime.Composable
import com.teamjg.dreamsanddoses.uis.filesScreen.FileCardList

// Notes tab content – currently uses dummy file list
@Composable
fun NotesJournalTabContent() {
    val notes = listOf("...", "..", "...")

    FileCardList(
        files = notes,
        showActionsOnTap = true
    )
}
