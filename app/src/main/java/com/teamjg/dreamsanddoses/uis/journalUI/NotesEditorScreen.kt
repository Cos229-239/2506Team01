package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesEditorScreen(
    navController: NavController,
    noteId: String? = null // null means new note
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    val lastEdited = remember { Date() }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                title = { Text("New Note") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Routes.journalRoute(tab = "notes")) {
                            popUpTo(Routes.HOME)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Note title") },
                textStyle = TextStyle(fontSize = 22.sp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                placeholder = { Text("Tag (optional)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start writing your note...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = Int.MAX_VALUE,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Templates Button (placeholder for now)
            OutlinedButton(
                onClick = { /* TODO: show template picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Templates", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Templates")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: Save logic
                    navController.navigate(Routes.journalRoute(tab = "notes")) {
                        popUpTo(Routes.HOME)
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
