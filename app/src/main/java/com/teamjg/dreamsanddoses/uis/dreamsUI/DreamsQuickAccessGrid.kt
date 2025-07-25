package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.Routes

/**
 * A grid of quick access buttons for Dreams-related features.
 * Each button navigates to a different part of the app or triggers functionality.
 *
 * @param navController Navigation controller used for navigation actions.
 */
@Composable
fun DreamsQuickAccessGrid(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),        // Vertical spacing between rows
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)                                        // Outer padding around the grid
    ) {
        // First row with a single quick access button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)   // Horizontal spacing between buttons
        ) {
            DreamsQuickAccessButton(
                title = "AI Image Generator",
                modifier = Modifier
                    .weight(10f)                                   // Fill most available horizontal space proportionally
                    .height(64.dp),                               // Fixed height for uniformity
                onClick = {
                    // Navigate to the AI Image Generator screen - TODO: Update route when implemented
                    navController.navigate(Routes.DREAMS_HOME)
                }
            )
        }

        // Second row with a single quick access button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DreamsQuickAccessButton(
                title = "Gallery",
                modifier = Modifier
                    .background(Color(0xFFE6F0FF), shape = RoundedCornerShape(16.dp))  // Background with rounded corners
                    .weight(10f)
                    .height(64.dp),
                onClick = {
                    // Navigate to Gallery screen - TODO: Update route when implemented
                    navController.navigate(Routes.DREAMS_HOME)
                }
            )
        }

        // Third row with a single quick access button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DreamsQuickAccessButton(
                title = "Template",
                modifier = Modifier
                    .weight(10f)
                    .height(64.dp),
                onClick = {
                    // Navigate to the Dream template screen
                    navController.navigate(Routes.DREAMS_TEMPLATE)
                }
            )
        }
    }
}

/**
 * A composable for an individual quick access button with a consistent look and feel.
 *
 * @param title The button's displayed text.
 * @param modifier Modifier to customize layout or behavior.
 * @param onClick Callback executed when the button is clicked.
 */
@Composable
fun DreamsQuickAccessButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(80.dp)                         // Ensures consistent button height
            .clickable { onClick() },              // Makes the surface clickable
        shape = RoundedCornerShape(16.dp),        // Rounded corners for a modern look
        tonalElevation = 4.dp,                     // Slight elevation for visual layering
        color = Color(0xFFE6F0FF)                  // Light pastel blue background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),                   // Inner padding for content spacing
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button title text centered inside the button
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}
