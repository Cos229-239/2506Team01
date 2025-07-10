package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.FirebaseApp
import com.teamjg.dreamsanddoses.navigation.AppNavigation
import com.teamjg.dreamsanddoses.ui.theme.DreamsAndDosesTheme

// Main entry point of the application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        setContent {
            DreamsAndDosesTheme {
                AppNavigation()
            }
        }
    }
}