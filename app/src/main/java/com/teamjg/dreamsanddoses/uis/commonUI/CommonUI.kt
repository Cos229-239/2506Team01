package com.teamjg.dreamsanddoses.uis.commonUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer// Dante created for the Medication reminder screen
import androidx.compose.foundation.layout.height// Dante created for the Medication reminder screen
import androidx.compose.material3.Card// Dante created for the Medication reminder screen
import androidx.compose.material3.CardDefaults/// Dante created for the Medication reminder screen

@Composable
fun PasswordRequirements(password: String) {
    val hasUpper = password.any { it.isUpperCase() }
    val hasLower = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }
    val minLength = password.length >= 6
    val maxLength = password.length <= 8

    val requirements = listOf(
        "• Uppercase letter" to hasUpper,
        "• Lowercase letter" to hasLower,
        "• Special character" to hasSpecial,
        "• Numeric digit" to hasDigit,
        "• Minimum 6 characters" to minLength,
        "• Maximum 8 characters" to maxLength
    )

    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Text(
            text = "Password must include:",
            fontWeight = FontWeight.Companion.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Companion.Center
        )
        requirements.forEach { (label, met) ->
            Text(
                text = label,
                fontSize = 13.sp,
                textAlign = TextAlign.Companion.Center,
                color = if (met) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

@Composable
fun ShowPasswordCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(text = "Show Password")
    }
}

@Composable
fun ReminderCard(title: String, time: String, notes: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Time: $time", fontSize = 14.sp)
            Text(text = "Notes: $notes", fontSize = 14.sp, color = Color.Gray)
        }
    }
}