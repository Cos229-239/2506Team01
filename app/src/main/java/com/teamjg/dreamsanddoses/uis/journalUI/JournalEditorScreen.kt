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
import com.teamjg.dreamsanddoses.data.FireStoreService.addJournal
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for creating or editing a journal entry.
 *
 * @param navController Navigation controller to handle back navigation and routing.
 * @param entryId Optional ID of an existing journal entry to edit. Null means creating a new entry.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditorScreen(
    navController: NavController,
    entryId: String? = null // Null indicates new entry creation.
) {
    // State holding the current input for the journal title.
    var titleState by remember { mutableStateOf("") }
    // State holding the current input for the journal content/body.
    var contentState by remember { mutableStateOf("") }
    // Remember the timestamp representing when the editor was opened.
    // Used to display "last edited" info.
    val lastEdited = remember { Date() }
    // Formatter to display the date in a readable format according to user locale.
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    // Scaffold provides basic screen structure with a top bar and content area.
    Scaffold(
        containerColor = Color.LightGray, // Background color of entire screen scaffold.
        topBar = {
            // Top app bar with back navigation and screen title.
            TopAppBar(
                // Padding to avoid overlapping status bar on devices with display cutouts.
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text("Compose") }, // Screen title text.
                navigationIcon = {
                    // Back button icon on top app bar.
                    IconButton(onClick = {
                        // Navigate back to the Journal listing screen,
                        // clearing intermediate navigation stack to HOME screen for clean backstack.
                        navController.navigate(Routes.JOURNAL) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back" // Accessibility description.
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content column with vertical scroll enabled for smaller screens.
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding provided by Scaffold.
                .padding(horizontal = 16.dp) // Horizontal padding for consistent margins.
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling.
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Top spacing.

            // Title input field: large font, outlined text field.
            OutlinedTextField(
                value = titleState, // Current title text.
                onValueChange = { titleState = it }, // Update state on input change.
                placeholder = { Text("Title") }, // Placeholder text shown when empty.
                textStyle = TextStyle(fontSize = 24.sp), // Large font size for title.
                modifier = Modifier.fillMaxWidth(), // Take full horizontal width.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White, // Background color when focused.
                    unfocusedContainerColor = Color.White, // Background color when unfocused.
                    disabledContainerColor = Color.White // Background color if disabled.
                )
            )

            Spacer(modifier = Modifier.height(24.dp)) // Spacing between inputs.

            // Content input field: multiline outlined text field for journal body.
            OutlinedTextField(
                value = contentState, // Current content text.
                onValueChange = { contentState = it }, // Update state on input change.
                placeholder = { Text("Write your thoughts...") }, // Placeholder text.
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 500.dp), // Minimum height for larger typing area.
                textStyle = TextStyle(fontSize = 16.sp), // Regular font size for body text.
                maxLines = Int.MAX_VALUE, // Allow unlimited lines.
                singleLine = false, // Explicitly multiline.
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp)) // Spacing below content field.

            // Templates button (planned future feature).
            OutlinedButton(
                onClick = {
                    // TODO: Implement showing a template picker dialog or sheet.
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Standard button height.
                shape = MaterialTheme.shapes.medium // Rounded corners from theme.
            ) {
                Text("Templates", modifier = Modifier.weight(1f)) // Text fills available space.
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates") // Arrow icon.
            }

            Spacer(modifier = Modifier.height(16.dp)) // Space between buttons.

            // Save button to submit the journal entry.
            Button(
                onClick = {
                    // Get current Firebase user ID.
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        // Call Firestore service to add journal entry.
                        addJournal(userId, titleState, contentState)
                    }

                    // Navigate back to journal listing screen,
                    // clearing intermediate backstack and ensuring only one top instance.
                    navController.navigate(Routes.JOURNAL) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Standard button height.
            ) {
                Text("Save") // Save button label.
            }

            Spacer(modifier = Modifier.height(12.dp)) // Small space before timestamp.

            // Display last edited timestamp centered below buttons.
            Text(
                text = "Last edited: ${dateFormat.format(lastEdited)}", // Formatted date string.
                style = MaterialTheme.typography.labelSmall, // Small label style.
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Subtle color variant.
                modifier = Modifier.align(Alignment.CenterHorizontally) // Center horizontally.
            )

            Spacer(modifier = Modifier.height(32.dp)) // Bottom spacing.
        }
    }
}
