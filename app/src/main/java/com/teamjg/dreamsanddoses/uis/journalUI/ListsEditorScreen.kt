package com.teamjg.dreamsanddoses.uis.journalUI

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.data.FireStoreService.addList
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

/* Data class representing a single checklist item with text and checked state */
data class ChecklistItem(
    var text: String,
    var isChecked: Boolean = false
)

/**
 * Screen composable for creating or editing a checklist-style list.
 *
 * @param navController Navigation controller used to navigate between screens.
 * @param listId Optional ID string for editing an existing list; null indicates creating a new list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsEditorScreen(
    navController: NavController,
    listId: String? = null // null means new list creation
) {
    // State variables for list title, optional tag, and checklist items
    var title by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var checklistItems by remember { mutableStateOf(mutableListOf(ChecklistItem(""))) }

    // Remember the last edited date (current time on first composition)
    val lastEdited = remember { Date() }
    // Formatter to display date in "MMM dd, yyyy · hh:mm a" format
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    // Scaffold provides the basic screen layout with top app bar and content area
    Scaffold(
        containerColor = Color.LightGray, // Background color of the scaffold
        topBar = {
            TopAppBar(
                title = { Text(if (listId == null) "New List" else "Edit List") }, // Dynamic title
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigate back to journal screen showing lists tab
                        navController.navigate(Routes.journalRoute(tab = "lists")) {
                            popUpTo(Routes.HOME)          // Pop backstack to HOME (non-inclusive)
                            launchSingleTop = true        // Avoid duplicate destination
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,                           // Background color of top bar
                    titleContentColor = MaterialTheme.colorScheme.onSurface     // Title text color
                ),
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()) // Padding for status bar
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)                  // Apply scaffold padding to content
                .padding(horizontal = 16.dp)            // Horizontal padding inside content
                .verticalScroll(rememberScrollState())  // Enable vertical scrolling
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Text field for entering the list title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("List title") },
                textStyle = TextStyle(fontSize = 22.sp),
                modifier = Modifier.fillMaxWidth(),  // Full width input
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional tag input field (single line)
            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                placeholder = { Text("Tag (optional)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section header for checklist items
            Text("Checklist", style = MaterialTheme.typography.titleMedium)

            // Iterate over checklist items and render each with checkbox, text input, and delete button
            checklistItems.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                        .padding(8.dp)
                ) {
                    // Checkbox to mark item completed or not
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checked ->
                            // Update the isChecked state immutably by copying list
                            checklistItems = checklistItems.toMutableList().also {
                                it[index] = it[index].copy(isChecked = checked)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Editable text field for checklist item text
                    OutlinedTextField(
                        value = item.text,
                        onValueChange = { newText ->
                            // Update text for this checklist item immutably
                            checklistItems = checklistItems.toMutableList().also {
                                it[index] = it[index].copy(text = newText)
                            }
                        },
                        placeholder = { Text("List item") },
                        modifier = Modifier.weight(1f),  // Take remaining horizontal space
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            // Strike-through text if item is checked/completed
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Show delete button only if more than one item remains
                    if (checklistItems.size > 1) {
                        IconButton(onClick = {
                            // Remove the checklist item at this index immutably
                            checklistItems = checklistItems.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button to add a new empty checklist item
            TextButton(onClick = {
                checklistItems = checklistItems.toMutableList().also { it.add(ChecklistItem("")) }
            }) {
                Text("+ Add Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder button for list templates (future feature)
            OutlinedButton(
                onClick = { /* TODO: Show list template picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Templates", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button for persisting the list to Firestore
            Button(
                onClick = {
                    addList(
                        title = title,
                        tag = tag,
                        checklistItems = checklistItems,
                        onSuccess = {
                            // On success, navigate back to lists tab in journal screen
                            navController.navigate(Routes.journalRoute(tab = "lists")) {
                                popUpTo(Routes.HOME)          // Pop backstack to HOME (non-inclusive)
                                launchSingleTop = true        // Avoid duplicate destinations
                            }
                        },
                        onFailure = { e ->
                            // Log any errors encountered during saving
                            Log.e("Firestore", "Failed to save list: ${e.message}")
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Text displaying the last edited date/time, centered horizontally
            Text(
                text = "Last edited: ${dateFormat.format(lastEdited)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
