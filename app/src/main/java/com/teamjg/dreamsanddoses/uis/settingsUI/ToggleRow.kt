package com.teamjg.dreamsanddoses.uis.settingsUI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * A reusable row with a label on the left and a toggle switch on the right.
 *
 * @param label The text displayed to the left of the switch.
 * @param checked Boolean representing the switch state.
 * @param onCheckedChange Callback triggered when the switch is toggled.
 * @param modifier For layout adjustments.
 */

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
