package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.teamjg.dreamsanddoses.data.FireStoreService.addNote
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

/**
 * Composable screen for creating or editing a note.
 *
 * @param navController Navigation controller to handle screen navigation.
 * @param noteId Optional ID of note to edit; null means creating a new note.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditorScreen(
    navController: NavController,
    noteId: String? = null // null means new note creation
) {
    // State holders for note title, content, and optional tag
    var titleState by remember { mutableStateOf("") }
    var contentState by remember { mutableStateOf("") }
    var tagState by remember { mutableStateOf("") }

    // Record the current date/time when composable is first composed
    val lastEdited = remember { Date() }
    // Formatter for displaying the last edited timestamp
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    // Scaffold layout with top app bar and content area
    Scaffold(
        containerColor = Color.LightGray, // Background color of scaffold
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,                      // Top bar background color
                    titleContentColor = MaterialTheme.colorScheme.onSurface // Title text color
                ),
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()), // Add status bar padding
                title = { Text("New Note") }, // Static title (can be dynamic if editing note)
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigate back to notes tab of journal screen
                        navController.navigate(Routes.journalRoute(tab = "notes")) {
                            popUpTo(Routes.HOME)          // Pop backstack to HOME (non-inclusive)
                            launchSingleTop = true        // Avoid duplicate destinations
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content column with scrolling enabled
        Column(
            modifier = Modifier
                .padding(innerPadding)                    // Apply scaffold padding
                .padding(horizontal = 16.dp)              // Horizontal padding inside content
                .verticalScroll(rememberScrollState())    // Enable vertical scroll for long content
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title input text field with larger font size
            OutlinedTextField(
                value = titleState,
                onValueChange = { titleState = it },
                placeholder = { Text("Note title") },
                textStyle = TextStyle(fontSize = 22.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,      // White background when focused
                    unfocusedContainerColor = Color.White,    // White background when unfocused
                    disabledContainerColor = Color.White       // White background when disabled
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Optional tag input field, single line with smaller font size
            OutlinedTextField(
                value = tagState,
                onValueChange = { tagState = it },
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

            // Main note content input field: multiline, tall, scrollable
            OutlinedTextField(
                value = contentState,
                onValueChange = { contentState = it },
                placeholder = { Text("Start writing your note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp), // Minimum height for comfortable writing
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = Int.MAX_VALUE,  // Allow unlimited lines
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Templates button placeholder - future feature for note templates
            OutlinedButton(
                onClick = { /* TODO: show template picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Templates", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button for persisting the note to Firestore and navigating back
            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        // Call Firestore service to save note for the current user
                        addNote(userId, titleState, tagState, contentState)
                    }
                    // Navigate back to notes tab of journal screen after save
                    navController.navigate(Routes.journalRoute(tab = "notes")) {
                        popUpTo(Routes.HOME)          // Pop backstack to HOME (non-inclusive)
                        launchSingleTop = true        // Avoid duplicate destinations
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display last edited timestamp, centered horizontally below the save button
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
