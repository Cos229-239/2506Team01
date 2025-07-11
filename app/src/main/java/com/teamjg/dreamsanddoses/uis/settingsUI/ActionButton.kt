package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A reusable styled button used throughout the app.
 *
 * @param text The label displayed inside the button.
 * @param backgroundColor The background color of the button.
 * @param contentColor The color of the button's text and icon.
 * @param modifier For layout customization.
 * @param onClick Lambda triggered when the button is clicked.
 */

@Composable
fun ActionButton(
    text: String,
    backgroundColor: Color,
    contentColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}
