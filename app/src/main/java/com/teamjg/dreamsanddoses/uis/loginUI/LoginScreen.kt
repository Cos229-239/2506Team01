package com.teamjg.dreamsanddoses.uis.loginUI
import com.teamjg.dreamsanddoses.uis.commonUI.ShowPasswordCheckbox// Dante added for UX Password

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.navigation.Routes

//Dante added concerning Login screen
@Composable
fun LoginScreen(navController: NavController) {
    // User input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }// Dante added for UX Password

    // Dante added concerning FireBase Authentication setup
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        //App title for Login screen
        Text(
            text = "Dreams and Doses",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        // icon placeholder
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(2.dp, Color.Black)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Sign in",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User email input
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("email@domain.com") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // User password input
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(), // Dante added for UX Password
            modifier = Modifier.fillMaxWidth()
        )

        // Dante added for UX Password
        ShowPasswordCheckbox(
            isChecked = showPassword,
            onCheckedChange = { showPassword = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                navController.navigate("home") // or your destination
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

        //Dante added concerning Login screen
        Spacer(modifier = Modifier.height(8.dp))

// "or" separator
        Text(text = "or")

        Spacer(modifier = Modifier.height(8.dp))

// Register now
        TextButton(
            onClick = { navController.navigate(Routes.REGISTER) }
        ) {
            Text(text = "Register now")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Handle Google sign-in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(text = "\uD83D\uDD0D  Continue with Google") // 🔍 icon as a placeholder
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Handle Apple sign-in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(text = "\uF8FF  Continue with Apple") // Apple logo character
        }

        Spacer(modifier = Modifier.weight(1f))

// Guest button area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { /* ----TODO: Temporary login !!---- */
                    navController.navigate("home") }
            ) {
                Text(text = "Continue as guest")
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Limited access – some features may be restricted",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

    }
}