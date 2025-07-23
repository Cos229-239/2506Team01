package com.teamjg.dreamsanddoses.data

data class ChecklistItem(
    val text: String,
    val isChecked: Boolean
)

data class ListEntry(
    val title: String,
    val tag: String,
    val checklistItems: List<ChecklistItem>
)
