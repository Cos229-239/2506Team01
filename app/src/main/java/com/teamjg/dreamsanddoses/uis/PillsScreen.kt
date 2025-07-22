package com.teamjg.dreamsanddoses.uis

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar

// Dante created for the Medication reminder screen
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamjg.dreamsanddoses.uis.commonUI.ReminderCard

// Pills screen implementation using the back navigation wrapper
@Composable
fun PillsScreen(navController: NavController) {
    TopNavigationBar(
        type = NavigationBarType.Pills,
        navController = navController,
        useIconHeader = true,
        onSearchClick = { /* logic */ }
    )

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        // Placeholder for reminders
        Text(
            text = "No reminders yet!",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🔔 Test reminder
        ReminderCard(
            title = "Vitamin D",
            time = "8:00 AM",
            notes = "Take after breakfast"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Add reminder button
        Button(
            onClick = {
                Toast.makeText(context, "Add reminder clicked!", Toast.LENGTH_SHORT).show()
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