package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar


/**
 * A reusable composable dialog for entering or editing text associated with a label.
 *
 * @param label The title label for the dialog.
 * @param value The current text value in the text field.
 * @param onValueChange Callback to update the text value when user types.
 * @param onDismiss Callback to dismiss the dialog.
 */
@Composable
fun EntryDialog(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = { Text(label) },
        text = {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = false // Allows multi-line input for longer text
            )
        }
    )
}


/**
 * A square grid item button with rounded corners, centered bold text,
 * and a click handler.
 *
 * @param label The text label shown inside the grid item.
 * @param onClick Callback invoked when the item is clicked.
 * @param modifier Modifier for custom layout adjustments.
 */
@Composable
fun GridItem(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Ensures a square shape regardless of width
            .background(Color.White, shape = RoundedCornerShape(12.dp)) // White background with rounded corners
            .clickable { onClick() }
            .padding(16.dp), // Inner padding around the text
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}


/**
 * The main template screen for streamlining dream journaling.
 * Allows quick access to enter common metadata fields such as Vibe, Location, People, and Time.
 * Also provides navigation to a color picker and journaling editor.
 *
 * @param navController Navigation controller to handle navigation actions.
 * @param entryId Optional dream entry ID for editing existing entries (not yet implemented).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamsTemplateScreen(
    navController: NavController,
    entryId: String? = null
) {
    // State variables holding the current input values for each metadata field
    var vibe by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    // Tracks which dialog is currently active; null means no dialog open
    var activeDialog by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.LightGray,
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* backlog search logic - TODO */ }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    onCompose = { navController.navigate(Routes.DREAMS_EDITOR) },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()) // Enable scrolling if content is too tall
                    .background(Color.LightGray)
                    .padding(16.dp), // Outer padding around content
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Instructional text at top, centered and styled prominently
                Text(
                    text = "Streamline your dreams, or skip right to journaling through Compose →",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                // Button to navigate to color picker screen
                Button(
                    onClick = { navController.navigate(Routes.COLOR_PICKER) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start with a color")
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Grid of quick access items to enter metadata fields
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // First row: Vibe and Location fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GridItem("Vibe/Feeling", { activeDialog = "Vibe" }, modifier = Modifier.weight(1f))
                        GridItem("Location", { activeDialog = "Location" }, modifier = Modifier.weight(1f))
                    }
                    // Second row: People and Time fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GridItem("People", { activeDialog = "People" }, modifier = Modifier.weight(1f))
                        GridItem("Time", { activeDialog = "Time" }, modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Show appropriate EntryDialog based on which field is active for editing
            when (activeDialog) {
                "Vibe" -> EntryDialog("Vibe/Feeling", vibe, { vibe = it }) { activeDialog = null }
                "Location" -> EntryDialog("Location", location, { location = it }) { activeDialog = null }
                "People" -> EntryDialog("People", people, { people = it }) { activeDialog = null }
                "Time" -> EntryDialog("Time", time, { time = it }) { activeDialog = null }
            }
        }

        // Large floating compose icon to start a new dream entry directly
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp) // Slightly overlaps bottom navigation bar for prominence
                .clickable {
                    navController.navigate(Routes.DREAMS_EDITOR)
                },
            tint = Color.Unspecified // Preserve original icon colors
        )
    }
}
