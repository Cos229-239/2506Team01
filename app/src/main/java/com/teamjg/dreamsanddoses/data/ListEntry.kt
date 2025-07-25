package com.teamjg.dreamsanddoses.data

// Represents a single checklist item with a text description and a checked status
data class ChecklistItem(
    val text: String,           // The description or label of the checklist item
    val isChecked: Boolean      // Whether the item is checked (true) or not (false)
)

// Represents a list entry containing a title, an optional tag, and a list of checklist items
data class ListEntry(
    val title: String,                          // Title of the list
    val tag: String,                            // Optional tag or category for the list
    val checklistItems: List<ChecklistItem>     // Collection of checklist items in this entry
)
