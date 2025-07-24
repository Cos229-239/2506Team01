package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar


@Composable
fun EntryDialog(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = { Text(label) },
        text = {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )
        }
    )
}


@Composable
fun GridItem(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // Keeps square shape
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamsTemplateScreen(
    navController: NavController,
    entryId: String? = null
) {
    var vibe by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var people by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    var activeDialog by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.LightGray,
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* backlog search logic */ }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    type = NavigationBarType.Dreams,
                    navController = navController,
                    onCompose = { navController.navigate(Routes.DREAMS_EDITOR) },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .background(Color.LightGray)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Streamline your dreams, or skip right to journaling through Compose →",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { navController.navigate(Routes.COLOR_PICKER) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start with a color")
                }

                Spacer(modifier = Modifier.height(48.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GridItem("Vibe/Feeling", { activeDialog = "Vibe" }, modifier = Modifier.weight(1f))
                        GridItem("Location", { activeDialog = "Location" }, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GridItem("People", { activeDialog = "People" }, modifier = Modifier.weight(1f))
                        GridItem("Time", { activeDialog = "Time" }, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Dialog logic
            when (activeDialog) {
                "Vibe" -> EntryDialog("Vibe/Feeling", vibe, { vibe = it }) { activeDialog = null }
                "Location" -> EntryDialog("Location", location, { location = it }) { activeDialog = null }
                "People" -> EntryDialog("People", people, { people = it }) { activeDialog = null }
                "Time" -> EntryDialog("Time", time, { time = it }) { activeDialog = null }
            }
        }

        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp)
                .clickable {
                    navController.navigate(Routes.DREAMS_EDITOR)
                },
            tint = Color.Unspecified
        )
    }
}