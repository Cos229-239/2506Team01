package com.teamjg.dreamsanddoses.uis.dreamsUI

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.uis.FirestoreService
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable screen for editing or creating a dream entry.
 * Includes title input, content area, and metadata inputs via buttons and dialogs.
 *
 * @param navController NavController instance for navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamsEditorScreen(
    navController: NavController
) {
    // State for dream title input, mutable and retained across recompositions
    var title by remember { mutableStateOf("") }

    // State for dream content (main text body)
    var content by remember { mutableStateOf("") }

    // Fixed timestamp for last edited, initialized once on composition
    val lastEdited = remember { Date() }

    // Formatter for displaying the last edited date/time in UI
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    // State holding a map of metadata descriptions keyed by category
    // Categories: Location, Time, People, Mood/Vibe
    val descriptions = remember {
        mutableStateOf(mapOf(
            "Location" to "",
            "Time" to "",
            "People" to "",
            "Mood/Vibe" to ""
        ))
    }

    // State controlling which metadata key dialog is currently shown; null means no dialog
    val showDialog = remember { mutableStateOf<String?>(null) }

    // Prompts used as placeholders inside dialogs based on metadata key
    val keyPrompts = mapOf(
        "Location" to "Where?",
        "Time" to "When?",
        "People" to "Who?",
        "Mood/Vibe" to "How did you feel?"
    )

    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            // Top app bar with back navigation and title input field
            TopAppBar(
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                // The title area is replaced by an outlined text field for editing the dream title
                title = {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title") },
                        textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    // Back button navigates back to the Dreams list screen without popping HOME screen
                    IconButton(onClick = {
                        navController.navigate(Routes.DREAMS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main scrollable content area using LazyColumn for efficient vertical scrolling and spacing
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp) // Horizontal padding for content edges
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Vertical spacing between items
        ) {

            // Metadata buttons section (Location, Time, People, Mood/Vibe)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // List of metadata keys in a desired display order
                val keys = listOf("People", "Time", "Location",  "Mood/Vibe")
                // Layout buttons in rows with two buttons each
                for (i in keys.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp) // Spacing between buttons in row
                    ) {
                        // Take up to 2 keys from the list starting at index i
                        keys.subList(i, minOf(i + 2, keys.size)).forEach { key ->
                            Button(
                                onClick = { showDialog.value = key }, // Open dialog to edit description for key
                                modifier = Modifier
                                    .weight(1f) // Buttons share available horizontal space equally
                                    .height(48.dp) // Fixed button height for consistency
                            ) {
                                // Display either the key name or key with current description if non-empty
                                val text = descriptions.value[key] ?: ""
                                Text(if (text.isEmpty()) key else "$key: $text")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp)) // Space between rows of buttons
                }
            }

            // Dream content text field, multiline and scrollable inside LazyColumn
            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Write what you recall...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 500.dp), // Minimum height for large editing area
                    textStyle = TextStyle(fontSize = 16.sp),
                    maxLines = Int.MAX_VALUE, // Unlimited lines for free form input
                    singleLine = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White
                    )
                )
            }

            // Save button triggers Firestore save and navigates back to dreams list on success
            item {
                Button(
                    onClick = {
                        // Obtain current user ID from FirebaseAuth; abort if not signed in
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                        // Call FirestoreService to save the dream entry with provided fields
                        FirestoreService.saveDreamEntry(
                            userId = userId,
                            title = title,
                            content = content,
                            location = descriptions.value["Location"],
                            time = descriptions.value["Time"],
                            people = descriptions.value["People"],
                            mood = descriptions.value["Mood/Vibe"],
                            colorHex = null, // TODO: Connect to selected color from color picker
                            onSuccess = {
                                // Navigate back to Dreams list screen on successful save
                                navController.navigate(Routes.DREAMS) {
                                    popUpTo(Routes.HOME) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onFailure = { e ->
                                // Log error for debugging purposes
                                Log.e("DreamsEditor", "Error saving dream entry", e)
                                // TODO: Add user-facing error feedback (snackbar/dialog)
                            }
                        )

                        // Extra navigation after save: likely redundant due to onSuccess callback
                        navController.navigate(Routes.DREAMS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // Taller button for easy tap target
                ) {
                    Text("Save")
                }
            }

            // Display last edited date/time below the save button for user reference
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Last edited: ${dateFormat.format(lastEdited)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center) // Center text horizontally
                    )
                }
                Spacer(modifier = Modifier.height(32.dp)) // Extra spacing after last edited text
            }
        }

        // Dialog for editing metadata descriptions keyed by the selected category (e.g. Location, Time)
        showDialog.value?.let { key ->
            // Local state for editing text input in dialog, initialized with current description value
            var inputText by remember { mutableStateOf(descriptions.value[key] ?: "") }

            AlertDialog(
                onDismissRequest = { showDialog.value = null }, // Close dialog on outside tap or back press
                confirmButton = {
                    TextButton(onClick = {
                        // Update descriptions map with new input text for the selected key
                        descriptions.value = descriptions.value.toMutableMap().apply {
                            this[key] = inputText
                        }
                        // Close dialog after saving changes
                        showDialog.value = null
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = null }) { Text("Cancel") }
                },
                text = {
                    // Text input for metadata description, with placeholder prompt for the key
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = keyPrompts[key] ?: "Enter $key") }
                    )
                }
            )
        }
    }
}
