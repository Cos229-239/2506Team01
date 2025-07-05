package com.teamjg.dreamsanddoses.uis.filesScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Represents a single file entry card.
 * This card shows a file name and provides action buttons for managing the file
 *
 * @param fileName Name of the file displayed on the card
 * @param showActionsOnTap When true, tapping the card toggles the visibility of action buttons
 * @param onArchiveClick Callback when Archive button is clicked
 * @param onDeleteClick Callback when Delete button is clicked
 * @param onExportClick Callback for Export button (only shown if non-null)
 * @param onViewClick Callback for View button (only shown if non-null)
 */

@Composable
fun FileCard(
    fileName: String,
    showActionsOnTap: Boolean = false,
    onArchiveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onExportClick: (() -> Unit)? = null,
    onViewClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier

) {
    // Tracks whether the action buttons are currently visible (expanded state)
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 110.dp)
            .clickable(enabled = showActionsOnTap) {
                if (showActionsOnTap) expanded = !expanded
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display the file name on the left side
            Text(
                fileName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            // Reveal actions only if toggled and enabled
            AnimatedVisibility(
                visible = if (showActionsOnTap) expanded else true,
                enter = expandHorizontally(),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // View button (only shown for exported files)
                    if (onViewClick != null) {
                        Button(
                            onClick = {
                                println("DEBUG: View button clicked for $fileName")
                                onViewClick.invoke()
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("View")
                        }
                    }

                    // Delete button (outlined style)
                    OutlinedButton(
                        onClick =
                            {
                            println("DEBUG: Delete button clicked for $fileName")
                            onDeleteClick()
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors
                            (
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }

                    // Export button (only shown if callback is provided)
                    if (onExportClick != null) {
                        Button(
                            onClick = {
                                println("DEBUG: Export button clicked for $fileName")
                                onExportClick.invoke()
                            }
                        ) {
                            Text("Export PDF")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Renders a vertical list of FileCards.
 *
 * @param files A list of file names to be rendered.
 * @param showActionsOnTap If true, actions will be hidden until a card is tapped.
 * @param onExportClick Callback for export functionality (shown on cards that can be exported)
 * @param onViewClick Callback for view functionality (shown on cards that can be viewed)
 */

@Composable
fun FileCardList(
    files: List<String>,
    showActionsOnTap: Boolean = false,
    onExportClick: (() -> Unit)? = null,
    onViewClick: ((String) -> Unit)? = null,
    onDeleteClick:((String) -> Unit)? = null,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(files.size) { index ->
            FileCard(
                fileName = files[index],
                showActionsOnTap = showActionsOnTap,
                onArchiveClick = {
                    // Pretty sure I don't need this onArchiveClick, but I'm keeping it just in case
                    println("DEBUG: Archive clicked for ${files[index]}")
                },
                onDeleteClick = {
                    println("DEBUG: Delete clicked for ${files[index]}")
                    onDeleteClick?.invoke(files[index])
                },
                onExportClick = onExportClick,
                onViewClick = if (onViewClick != null) {
                    { onViewClick(files[index]) }
                } else null
            )
        }
    }
}
