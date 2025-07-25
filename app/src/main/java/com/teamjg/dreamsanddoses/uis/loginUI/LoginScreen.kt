package com.teamjg.dreamsanddoses.uis.loginUI

// Dante added for UX Password
import com.teamjg.dreamsanddoses.uis.commonUI.ShowPasswordCheckbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Dante added concerning FireBase Authentication setup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.Routes

// Dante added concerning Login screen
@Composable
fun LoginScreen(navController: NavController) {

    // Stores what the user types for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Show/hide password toggle
    var showPassword by remember { mutableStateOf(false) } // Dante added for UX Password

    // Firebase setup for authentication
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Main vertical layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // App title at the top
        Text(
            text = "Dreams and Doses",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        // App icon logo below the title
        Image(
            painter = painterResource(id = R.drawable.ic_main_logo_icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(115.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Section header for login
        Text(
            text = "Sign in",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email input field
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("email@domain.com") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password input field with visibility toggle
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Dante added for UX Password
            modifier = Modifier.fillMaxWidth()
        )

        // Password visibility checkbox (Show/Hide password)
        ShowPasswordCheckbox(
            isChecked = showPassword,
            onCheckedChange = { showPassword = it }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Forgot password link on the right
        TextButton(
            onClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot Password?",
                color = Color(0xFF1E88E5),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main login button
        Button(
            onClick = {
                // Only try login if both fields are filled
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") // go home screen after login
                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        // Dante added concerning Login screen
        Spacer(modifier = Modifier.height(8.dp))

        // Simple text divider
        Text(text = "or")

        Spacer(modifier = Modifier.height(8.dp))

        // Button to navigate to Register screen
        TextButton(
            onClick = { navController.navigate(Routes.REGISTER) }
        ) {
            Text(text = "Register now")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Guest login area (for users who don’t want to log in)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    // ----TODO: Temporary login for guest users----
                    navController.navigate("home")
                }
            ) {
                Text(text = "Continue as guest")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Info about limited access for guest users
            Text(
                text = "Limited access – some features may be restricted",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

    }
}