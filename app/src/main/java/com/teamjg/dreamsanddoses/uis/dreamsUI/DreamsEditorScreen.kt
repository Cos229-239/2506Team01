package com.teamjg.dreamsanddoses.uis.dreamsUI

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.uis.FirestoreService
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamsEditorScreen(
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    val lastEdited = remember { Date() }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy · hh:mm a", Locale.getDefault())

    val descriptions = remember {
        mutableStateOf(mapOf(
            "Location" to "",
            "Time" to "",
            "People" to "",
            "Mood/Vibe" to ""
        ))
    }
    val showDialog = remember { mutableStateOf<String?>(null) }

    val keyPrompts = mapOf(
        "Location" to "Where?",
        "Time" to "When?",
        "People" to "Who?",
        "Mood/Vibe" to "How did you feel?"
    )


    Scaffold(
        containerColor = Color.LightGray,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title") },
                        textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
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
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                val keys = listOf("People", "Time", "Location",  "Mood/Vibe")
                for (i in keys.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp) // tighter horizontal spacing
                    ) {
                        keys.subList(i, minOf(i + 2, keys.size)).forEach { key ->
                            Button(
                                onClick = { showDialog.value = key },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp) // optional: slightly shorter buttons
                            ) {
                                val text = descriptions.value[key] ?: ""
                                Text(if (text.isEmpty()) key else "$key: $text")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp)) // only one spacer needed
                }
            }

            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Write what you recall...") },
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
            }

            item {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                        FirestoreService.saveDreamEntry(
                            userId = userId,
                            title = title,
                            content = content,
                            location = descriptions.value["Location"],
                            time = descriptions.value["Time"],
                            people = descriptions.value["People"],
                            mood = descriptions.value["Mood/Vibe"],
                            colorHex = null, // TODO: hook up selectedColor from template/picker flow
                            onSuccess = {
                                navController.navigate(Routes.DREAMS) {
                                    popUpTo(Routes.HOME) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onFailure = { e ->
                                Log.e("DreamsEditor", "Error saving dream entry", e)
                                // TODO: Show user feedback (e.g., snackbar or dialog)
                            }
                        )
                        navController.navigate(Routes.DREAMS) {
                            popUpTo(Routes.HOME) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Save")
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Last edited: ${dateFormat.format(lastEdited)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        showDialog.value?.let { key ->
            var inputText by remember { mutableStateOf(descriptions.value[key] ?: "") }

            AlertDialog(
                onDismissRequest = { showDialog.value = null },
                confirmButton = {
                    TextButton(onClick = {
                        descriptions.value = descriptions.value.toMutableMap().apply {
                            this[key] = inputText
                        }
                        showDialog.value = null
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = null }) { Text("Cancel") }
                },
                text = {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = keyPrompts[key] ?: "Enter $key") }
                    )
                }
            )
        }
    }
}