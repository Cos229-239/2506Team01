package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.Routes

@Composable
fun DreamsQuickAccessGrid(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DreamsQuickAccessButton("AI Image Generator", Modifier.weight(10f).height(64.dp)) {
                navController.navigate(Routes.DREAMS_HOME) // TODO
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DreamsQuickAccessButton("Gallery", Modifier.weight(10f).height(64.dp)) {
                navController.navigate(Routes.DREAMS_HOME) // TODO
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DreamsQuickAccessButton("Template", Modifier.weight(10f).height(64.dp)) {
                navController.navigate(Routes.DREAMS_TEMPLATE)
            }

        }
    }
}

@Composable
fun DreamsQuickAccessButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button title text
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}