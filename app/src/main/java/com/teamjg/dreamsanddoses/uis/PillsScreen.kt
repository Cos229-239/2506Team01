package com.teamjg.dreamsanddoses.uis

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun PillsScreen(navController: NavController) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var showDialog by remember { mutableStateOf(false) }
    var reminders by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }

    // Fetch reminders on load
    LaunchedEffect(Unit) {
        if (userId != null) {
            FirestoreService.fetchReminders(userId) { result ->
                reminders = result
            }
        }
    }

    // Top and Bottom Navi bars
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
        Column(
            modifier = Modifier
                .fillMaxSize()
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

            if (reminders.isEmpty()) {
                Text(
                    text = "No reminders yet. Create some below!",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            } else {
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

            // Add reminder button
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

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // Show the reminder input form dialog
    if (showDialog) {
        ReminderFormDialog(
            onDismiss = { showDialog = false },
            onSave = { title, time, notes ->
                if (userId != null) {
                    FirestoreService.saveReminder(userId, title, time, notes)
                    Toast.makeText(context, "Reminder Saved: $title at $time", Toast.LENGTH_SHORT).show()

                    // Refresh reminders after saving
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