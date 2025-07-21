package com.teamjg.dreamsanddoses.uis.dreamsUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamsEditorScreen(
    navController: NavController,
    entryId: String? = null // null = new entry
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val lastEdited = remember { Date() }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { Text("Compose") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.DREAMS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title input field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title") },
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content input field
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Write your thoughts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 500.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Templates button (future feature)
            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.DREAMS_TEMPLATE) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Template", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = {
                    // TODO: Implement save logic (persist entry)
                    navController.navigate(Routes.DREAMS) {
                        popUpTo(Routes.HOME) { inclusive = false } // Clear backstack to HOME
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Last edited timestamp
            Text(
                text = "Last edited: ${dateFormat.format(lastEdited)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}