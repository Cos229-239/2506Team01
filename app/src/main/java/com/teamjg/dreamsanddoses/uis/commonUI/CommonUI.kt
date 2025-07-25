package com.teamjg.dreamsanddoses.uis.commonUI

// Required imports for layout, UI elements, and styling
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Dante created for the Medication reminder screen
/*import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults*/

// Composable that displays a checklist of password requirements
@Composable
fun PasswordRequirements(password: String) {
    // Check whether the password meets various criteria
    val hasUpper = password.any { it.isUpperCase() }         // Must contain uppercase letters
    val hasLower = password.any { it.isLowerCase() }         // Must contain lowercase letters
    val hasDigit = password.any { it.isDigit() }             // Must contain numbers
    val hasSpecial = password.any { !it.isLetterOrDigit() }  // Must contain a special character
    val minLength = password.length >= 6                     // At least 6 characters
    val maxLength = password.length <= 8                     // No more than 8 characters

    // Store all requirements in a list with labels and boolean values
    val requirements = listOf(
        "• Uppercase letter" to hasUpper,
        "• Lowercase letter" to hasLower,
        "• Special character" to hasSpecial,
        "• Numeric digit" to hasDigit,
        "• Minimum 6 characters" to minLength,
        "• Maximum 8 characters" to maxLength
    )

    // Show the requirements on screen in a vertical list
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title above the list
        Text(
            text = "Password must include:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        // Loop through each requirement and show its status
        requirements.forEach { (label, met) ->
            Text(
                text = label,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                color = if (met) Color(0xFF2E7D32) else Color(0xFFC62828) // Green if met, red if not
            )
        }
    }
}

// Reusable checkbox that toggles password visibility
@Composable
fun ShowPasswordCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Horizontal layout for checkbox and label
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp)
    ) {
        // Checkbox to toggle showing/hiding password
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(text = "Show Password")
    }
}

// Reusable card that displays a single reminder's title, time, and notes
/*@Composable
fun ReminderCard(title: String, time: String, notes: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Inside the card, stack the text vertically
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp)) // Adds a bit of spacing between lines
            Text(text = "Time: $time", fontSize = 14.sp)
            Text(text = "Notes: $notes", fontSize = 14.sp, color = Color.Gray)
        }
    }
}*/