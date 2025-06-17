package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color


//// Main activity inherits from ComponentActivity, the base class for Jetpack Compose activities
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SandboxScreen()
        }
    }
}

// Composable function displays a simple blank screen
@Composable
fun SandboxScreen() {
    Box( //// Box is a layout that lets you stack children on top of each other
        modifier = Modifier
            .fillMaxSize() //// Tells the Box to take up the full screen
            .background(Color.LightGray) //// Applies a light gray background to visualize the space
    )
}
