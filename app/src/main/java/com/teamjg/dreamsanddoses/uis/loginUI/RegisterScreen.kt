package com.teamjg.dreamsanddoses.uis.loginUI
import com.teamjg.dreamsanddoses.uis.commonUI.PasswordRequirements// Dante added for UX Password
import com.teamjg.dreamsanddoses.uis.commonUI.ShowPasswordCheckbox// Dante added for UX Password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation// Dante added for UX Password creation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dante added concerning FireBase Authentication setup
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation// Dante added for UX Password creation
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.teamjg.dreamsanddoses.navigation.Routes

@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }// Dante added for UX Password creation

    // Dante added concerning FireBase Authentication setup
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Dante added for UX Password creation.
            modifier = Modifier.fillMaxWidth()
        )

        // Dante added for UX Password creation
        ShowPasswordCheckbox(
            isChecked = showPassword,
            onCheckedChange = { showPassword = it }
        )

        // Dante added for UX Password creation details. This is a helper function
        PasswordRequirements(password = password)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            // Dante added concerning FireBase Authentication setup
            onClick = {

                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()

                                // Geno added regarding Firestore user storage
                                val db = FirebaseFirestore.getInstance()

                                val userId = auth.currentUser?.uid
                                val userProfile = hashMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis()
                                )
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

                                navController.navigate(Routes.LOGIN)

                            } else {
                                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
            Text("Back to Login")
        }
    }
}