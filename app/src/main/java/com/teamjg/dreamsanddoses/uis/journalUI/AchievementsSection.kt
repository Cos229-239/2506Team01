package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


// Achievement widget card with hardcoded values for now
@Composable
fun AchievementsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F0FF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Achievements",
                style = MaterialTheme.typography.titleLarge
            )

            // Two rows of achievement items with spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AchievementItem(label = "Days Streak", value = 9999, modifier = Modifier.weight(1f))
                AchievementItem(label = "Pages", value = 9999, modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AchievementItem(label = "Wordle Score", value = 9999, modifier = Modifier.weight(1f))
                AchievementItem(label = "Dreams", value = 9999, modifier = Modifier.weight(1f))
            }
        }
    }
}

// Achievement widget card with hardcoded values for now
@Composable
fun AchievementItem(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
