package com.teamjg.dreamsanddoses.uis.calendarUI

// Import necessary Jetpack Compose and Android components
import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// Custom composables for layout and navigation
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.navigation.Routes

// Java time and utility
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * CalendarScreen composable provides the main UI for the Calendar section of the app.
 * Includes: animated transitions, top & bottom navigation bars, date picker dialog,
 * bottom sheet modal for reminders, and calendar grid.
 *
 * @param navController for navigation handling
 * @param viewModel provides calendar state and logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    // Observe state from ViewModel
    val state by viewModel.uiState.collectAsState()

    // Get screen height for responsive layout
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Bottom sheet and dialog visibility states
    var showBottomSheet = remember { mutableStateOf(false) }
    var launchedFromCompose by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    // Create modal bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Show bottom sheet if needed
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            // Container card inside the modal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = screenHeight * 0.7f)
                    .padding(16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                // Scrollable column content inside the sheet
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Drag handle indicator
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            .align(androidx.compose.ui.Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Section label
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Display selected date or fallback message
                    Text(
                        text = state.selectedDate?.format(
                            DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
                        ) ?: "No date selected",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Placeholder for medication reminders
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(
                                text = "Medication Reminders",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Feature coming soon! You'll be able to add and manage medication reminders for this date.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons at the bottom of the sheet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBottomSheet.value = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Close")
                        }

                        Button(
                            onClick = {
                                showBottomSheet.value = false
                                navController.navigate(Routes.PILLS) // Navigate to pills/reminders
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add Reminder")
                        }
                    }
                }
            }
        }
    }

    // Show native Android DatePicker dialog
    if (showDateDialog) {
        DatePickerDialog(
            initialDate = LocalDate.now(),
            onDateSelected = { date ->
                viewModel.onDaySelected(date)  // Update view model
                showDateDialog = false

                // Open bottom sheet if launched from FAB
                if (launchedFromCompose) {
                    showBottomSheet.value = true
                    launchedFromCompose = false
                }
            },
            onDismiss = { showDateDialog = false }
        )
    }

    // Apply screen animations and layout
    AnimatedScreenWrapper(navController = navController) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Calendar,
                    navController = navController,
                    useIconHeader = true
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    type = NavigationBarType.Calendar,
                    navController = navController,
                    onCompose = {
                        launchedFromCompose = true
                        showDateDialog = true
                    },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(innerPadding)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Calendar month header with navigation arrows
                CalendarHeader(
                    currentMonth = state.currentMonth,
                    onPreviousMonth = viewModel::goToPreviousMonth,
                    onNextMonth = viewModel::goToNextMonth
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Row showing weekday labels (Sun, Mon, etc.)
                CalendarWeekdayHeader()

                Spacer(modifier = Modifier.height(8.dp))

                // Grid of days in the current month
                CalendarGrid(
                    currentMonth = state.currentMonth,
                    selectedDate = state.selectedDate,
                    onDayClick = { date ->
                        viewModel.onDaySelected(date) // Update selected date
                        showBottomSheet.value = true  // Show modal sheet
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Native Android Date Picker dialog wrapped in Compose-friendly API
 */
@Composable
fun DatePickerDialog(
    initialDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply {
        set(initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth)
    }

    // Launch Android DatePicker dialog with selected values
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth)) // Handle result
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnCancelListener { onDismiss() } // Dismiss callback
    }.show()
}
