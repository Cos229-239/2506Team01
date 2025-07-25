package com.teamjg.dreamsanddoses.uis

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Added for scrolling
import androidx.compose.foundation.verticalScroll // Added for scrolling
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Added for OpenFDA API
import com.teamjg.dreamsanddoses.network.DrugInfoService

@Composable
fun PillsScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var showDialog by remember { mutableStateOf(false) } // Controls whether the reminder dialog is visible
    var reminders by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) } // Holds list of medication reminders

    // Lock mechanism: Prevents users from accessing anything until meds are confirmed
    var isLocked by remember { mutableStateOf(true) }

    // Automatically fetch reminders from Firestore when screen first loads
    LaunchedEffect(Unit) {
        if (userId != null) {
            FirestoreService.fetchReminders(userId) { result ->
                reminders = result
            }
        }
    }

    // If the screen is locked, display the medication confirmation overlay instead of the main screen
    if (isLocked) {
        MedicationLockOverlay(
            onConfirmed = {
                isLocked = false
                Toast.makeText(context, "Access granted. Stay healthy!", Toast.LENGTH_SHORT).show()
            },
            onEmergency = {
                isLocked = false
                Toast.makeText(context, "Emergency override activated", Toast.LENGTH_LONG).show()
                // TODO: Consider logging the override or alerting backend for safety
            }
        )
        return // Exit early so that the rest of the screen doesn’t draw behind the overlay
    }

    // Main screen scaffold with top and bottom nav bars
    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Pills,
                navController = navController,
                useIconHeader = true
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Pills
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        // Content section that allows scrolling
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Reminders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display either the reminder list or a default message
            if (reminders.isEmpty()) {
                Text(
                    text = "No reminders yet. Create some below!",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else {
                // Loop through reminders and show each with a delete button
                reminders.forEach { (reminderId, title, details) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = title, fontWeight = FontWeight.Bold)
                        Text(text = details, fontSize = 14.sp, color = Color.Gray)

                        Button(
                            onClick = {
                                // Delete reminder and refresh list
                                if (userId != null) {
                                    FirestoreService.deleteReminder(userId, reminderId) { success ->
                                        if (success) {
                                            FirestoreService.fetchReminders(userId) { result ->
                                                reminders = result
                                            }
                                            Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button to open the Add Reminder dialog
            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Add Reminder")
            }

            // --- OpenFDA API Section Below ---

            var selectedDrug by remember { mutableStateOf("") } // The name of the drug to be searched
            var drugInfo by remember { mutableStateOf<String?>(null) } // Info fetched from the OpenFDA API
            var isLoading by remember { mutableStateOf(false) } // Loading spinner control
            var fetchRequested by remember { mutableStateOf(false) } // Triggers fetch call

            Spacer(modifier = Modifier.height(16.dp))

            // Input field for searching drug info
            OutlinedTextField(
                value = selectedDrug,
                onValueChange = { selectedDrug = it },
                label = { Text("Search Medication Info") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Button that triggers drug info lookup
            Button(
                onClick = {
                    if (selectedDrug.isNotBlank()) {
                        isLoading = true
                        drugInfo = null
                        fetchRequested = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fetch Info")
            }

            // Launch the drug info fetch when requested
            LaunchedEffect(fetchRequested) {
                if (fetchRequested) {
                    drugInfo = DrugInfoService.fetchDrugInfo(selectedDrug)
                    isLoading = false
                    fetchRequested = false
                }
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                // Show drug info if it's available
                drugInfo?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            // --- OpenFDA API Section End ---

            Spacer(modifier = Modifier.weight(1f)) // Pushes content to the top
        }
    }

    // Display dialog for creating new medication reminders
    if (showDialog) {
        ReminderFormDialog(
            onDismiss = { showDialog = false },
            onSave = { title, time, notes ->
                if (userId != null) {
                    // Save new reminder to Firestore
                    FirestoreService.saveReminder(userId, title, time, notes)
                    Toast.makeText(context, "Reminder Saved: $title at $time", Toast.LENGTH_SHORT).show()

                    // Refresh the list
                    FirestoreService.fetchReminders(userId) { result ->
                        reminders = result
                    }
                } else {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun MedicationLockOverlay(
    onConfirmed: () -> Unit,
    onEmergency: () -> Unit
) {
    // Fullscreen semi-transparent overlay to block the screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xDD000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Medication Reminder",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Please confirm you’ve taken all required medications before continuing."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Button to confirm that meds were taken
                Button(onClick = onConfirmed) {
                    Text("I Took My Meds")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to bypass in case of emergency
                TextButton(onClick = onEmergency) {
                    Text("Emergency Bypass", color = Color.Red)
                }
            }
        }
    }
}