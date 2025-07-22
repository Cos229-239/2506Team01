package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SubSettingsScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        SettingItem("Account") { navController.navigate("ACCOUNT_SETTINGS") }
        SettingItem("Notifications") { navController.navigate("notification_settings") }
        SettingItem("Privacy") { navController.navigate("privacy_settings") }
        SettingItem("Appearance") { navController.navigate("appearance_settings") }
    }
}

@Composable
fun SettingItem(title: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(vertical = 16.dp)) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
