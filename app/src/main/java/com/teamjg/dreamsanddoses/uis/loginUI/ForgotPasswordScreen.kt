package com.teamjg.dreamsanddoses.uis.loginUI

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.teamjg.dreamsanddoses.navigation.Routes

@Composable
fun ForgotPasswordScreen(navController: NavController) {

    // Store what the user types into the email field
    var email by remember { mutableStateOf("") }

    // Flag to track if we're in the middle of sending the reset email
    var isSending by remember { mutableStateOf(false) }

    // Access to context (needed for toasts)
    val context = LocalContext.current

    // Get Firebase authentication instance
    val auth = remember { FirebaseAuth.getInstance() }

    // The layout for the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Space before the title
        Spacer(modifier = Modifier.height(50.dp))

        // Main heading for the screen
        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // A friendly explanation of what the user should do here
        Text(
            text = "Enter the email you used to register and we'll send you a reset link.",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input field for email
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("email@domain.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button to send the reset email
        Button(
            onClick = {
                // Make sure the user typed something in
                if (email.isBlank()) {
                    Toast.makeText(context, "Please enter your email.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Set sending flag so we can show loading state
                isSending = true

                // Ask Firebase to send the reset email
                auth.sendPasswordResetEmail(email.trim())
                    .addOnCompleteListener { task ->
                        isSending = false
                        if (task.isSuccessful) {
                            // Let the user know it worked
                            Toast.makeText(
                                context,
                                "Reset email sent. Check your inbox or spam.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Return user to Login screen
                            navController.popBackStack(Routes.LOGIN, inclusive = false)
                        } else {
                            // Show the error message if something went wrong
                            Toast.makeText(
                                context,
                                "Error: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSending // Disable button while sending to prevent double taps
        ) {
            // Button text changes depending on loading state
            Text(if (isSending) "Sending..." else "Send Reset Link")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Back button to return to the previous screen
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Back")
        }
    }
}