package com.teamjg.dreamsanddoses.uis

// Required imports for UI components and state
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// This is the popup dialog for creating a new medication reminder
@Composable
fun ReminderFormDialog(
    onDismiss: () -> Unit,                        // Called when user cancels or closes the dialog
    onSave: (String, String, String) -> Unit      // Called when user hits "Save" with title, time, and notes
) {
    // State to store user inputs for the new reminder
    var title by remember { mutableStateOf("") }  // Name of the medication
    var time by remember { mutableStateOf("") }   // Time for the reminder
    var notes by remember { mutableStateOf("") }  // Optional notes

    // Main alert dialog UI
    AlertDialog(
        onDismissRequest = onDismiss,             // Dismiss when tapping outside or pressing back
        title = { Text("New Reminder") },         // Dialog title
        text = {
            // Inputs for title, time, and notes stacked vertically
            Column {
                // Input for medication name
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Medication Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp)) // Space between fields

                // Input for time of reminder
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g. 8:00 AM)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp)) // Space between fields

                // Input for any additional notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            // When "Save" is clicked, send data to parent and close the dialog
            Button(onClick = {
                onSave(title, time, notes) // Pass input data to onSave callback
                onDismiss()                // Then close the dialog
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            // User clicked cancel, just close the dialog
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}