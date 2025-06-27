package com.teamjg.dreamsanddoses.uis.loginUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R

//Dante added concerning Login screen
@Composable
fun LoginScreen(navController: NavController) {
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
            text = "Dreams & Doses",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_main_logo_icon),
            contentDescription = "App Logo",
            tint = Color.Black,
            modifier = Modifier.size(80.dp)
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
            onClick = { /* ----TODO: Temporary login !!---- */
                navController.navigate("home") },
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
                onClick = { /* ----TODO: Temporary login !!---- */
                    navController.navigate("home")
//                    {
//                        popUpTo("login") {
//                            inclusive = true
//                        }
//                        launchSingleTop = true
//                    }
                }
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