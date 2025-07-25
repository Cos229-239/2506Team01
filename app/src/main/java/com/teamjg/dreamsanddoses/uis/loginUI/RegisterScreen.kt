package com.teamjg.dreamsanddoses.uis.loginUI

import com.teamjg.dreamsanddoses.uis.commonUI.PasswordRequirements // Dante added for UX Password
import com.teamjg.dreamsanddoses.uis.commonUI.ShowPasswordCheckbox // Dante added for UX Password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation // Dante added for UX Password creation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dante added concerning FireBase Authentication setup
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation // Dante added for UX Password creation
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore // Geno added regarding Firestore user storage
import com.teamjg.dreamsanddoses.navigation.Routes

@Composable
fun RegisterScreen(navController: NavController) {

    // Track what the user types into each field
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Controls whether the password is visible or hidden
    var showPassword by remember { mutableStateOf(false) } // Dante added for UX Password creation

    // Get access to Firebase Auth and context for toasts
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Main layout column for the screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Heading at the top of the screen
        Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        // Input for the user's full name
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input for the user's email
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input for the user's password
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Dante added for UX Password creation.
            modifier = Modifier.fillMaxWidth()
        )

        // Show/hide password toggle for user convenience
        ShowPasswordCheckbox(
            isChecked = showPassword,
            onCheckedChange = { showPassword = it }
        )

        // Display list of helpful password rules below the field
        PasswordRequirements(password = password) // Dante added for UX Password creation details. This is a helper function

        Spacer(modifier = Modifier.height(24.dp))

        // Register button that triggers Firebase Auth account creation
        Button(
            // Dante added concerning FireBase Authentication setup
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    // Try creating the user in Firebase Auth
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()

                                // Geno added regarding Firestore user storage
                                // After account is made, store the user's name and email in Firestore
                                val db = FirebaseFirestore.getInstance()
                                val userId = auth.currentUser?.uid
                                val userProfile = hashMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis()
                                )

                                // Save this user profile to the Firestore database
                                if (userId != null) {
                                    db.collection("users")
                                        .document(userId)
                                        .set(userProfile)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "User profile saved", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }

                                // After successful registration, navigate back to the login screen
                                navController.navigate(Routes.LOGIN)
                            } else {
                                // Show error if registration fails
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    // Prompt user to complete all fields
                    Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Let users go back to the login screen if they already have an account
        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
            Text("Back to Login")
        }
    }
}