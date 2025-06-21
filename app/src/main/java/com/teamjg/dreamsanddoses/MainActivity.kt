package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

//Dante added concerning Login screen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.border
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight


//// Main activity inherits from ComponentActivity, the base class for Jetpack Compose activities
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

// Composable function displays a simple blank screen
/*@Composable
fun LoginScreen() {
    Box( //// Box is a layout that lets you stack children on top of each other
        modifier = Modifier
            .fillMaxSize() //// Tells the Box to take up the full screen
            .background(Color.LightGray) //// Applies a light gray background to visualize the space
    )
}*/
//Dante added concerning Login screen
@Composable
fun LoginScreen() {
    // User input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Handle login */ },
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
            onClick = { /* Navigate to register screen */ }
        ) {
            Text(text = "Register now")
        }

        Spacer(modifier = Modifier.height(16.dp))

// Google sign-in button
        /*Button(
            onClick = { /* Handle Google sign-in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(text = "Continue with Google")
        }*/

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

// Apple sign-in button
        /*Button(
            onClick = { /* Handle Apple sign-in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(text = "Continue with Apple")
        }*/

        Button(
            onClick = { /* Handle Apple sign-in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(text = "\uF8FF  Continue with Apple") // Apple logo character
        }

        Spacer(modifier = Modifier.weight(1f)) // pushes content below to bottom

// Guest button area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = { /* Handle guest access */ }
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
