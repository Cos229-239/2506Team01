package com.teamjg.dreamsanddoses.uis.journalUI

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
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

/* Data class representing a single checklist item */
data class ChecklistItem(
    var text: String,
    var isChecked: Boolean = false
)

/**
 * Screen for creating or editing a checklist-style list.
 *
 * @param navController Navigation controller for screen transitions.
 * @param listId Optional ID for editing existing list; null for new list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsEditorScreen(
    navController: NavController,
    listId: String? = null // null indicates a new list
) {
    var title by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var checklistItems by remember { mutableStateOf(mutableListOf(ChecklistItem(""))) }
    val lastEdited = remember { Date() }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            TopAppBar(
                title = { Text(if (listId == null) "New List" else "Edit List") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.journalRoute(tab = "lists")) {
                            popUpTo(Routes.HOME)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // List title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("List title") },
                textStyle = TextStyle(fontSize = 22.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional tag input
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

            Text("Checklist", style = MaterialTheme.typography.titleMedium)

            // Checklist items with checkbox, editable text, and delete button
            checklistItems.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checked ->
                            checklistItems = checklistItems.toMutableList().also {
                                it[index] = it[index].copy(isChecked = checked)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = item.text,
                        onValueChange = { newText ->
                            checklistItems = checklistItems.toMutableList().also {
                                it[index] = it[index].copy(text = newText)
                            }
                        },
                        placeholder = { Text("List item") },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (checklistItems.size > 1) {
                        IconButton(onClick = {
                            checklistItems = checklistItems.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add new checklist item button
            TextButton(onClick = {
                checklistItems = checklistItems.toMutableList().also { it.add(ChecklistItem("")) }
            }) {
                Text("+ Add Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Templates button placeholder
            OutlinedButton(
                onClick = { /* TODO: Show list template picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Templates", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    // TODO: Save logic here
                    navController.navigate(Routes.journalRoute(tab = "lists")) {
                        popUpTo(Routes.HOME)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Last edited info
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
