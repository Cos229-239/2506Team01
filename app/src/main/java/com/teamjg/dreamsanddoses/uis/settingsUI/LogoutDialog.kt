package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

/**
 * A reusable dialog component to confirm logout actions.
 *
 * @param onConfirm Callback invoked when the user confirms logout.
 * @param onDismiss Callback invoked when the user cancels or dismisses the dialog.
 */

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to log out?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
