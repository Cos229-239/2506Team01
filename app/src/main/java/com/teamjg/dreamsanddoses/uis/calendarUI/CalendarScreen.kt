package com.teamjg.dreamsanddoses.uis.calendarUI

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
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * - Better spacing and layout that adapts to different screen sizes
 * - Improved visual hierarchy with cards and proper elevation
 * - Modern bottom sheet design for better user experience
 * - Consistent color scheme throughout
 *
 * @param navController for navigation handling and back stack management
 * @param viewModel provides calendar UI state and navigation callbacks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    // Collect UI state from ViewModel, auto-updates on changes
    val state by viewModel.uiState.collectAsState()

    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // State for controlling bottom sheet visibility
    var showBottomSheet = remember { mutableStateOf(false) }

    var launchedFromCompose by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    // Bottom sheet state with improved configuration
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false // Allow partial expansion for better UX
    )

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = screenHeight * 0.7f)
                    .padding(16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            .align(androidx.compose.ui.Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = state.selectedDate?.format(
                            DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
                        ) ?: "No date selected",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Medication Reminders",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
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
                                // TODO: Add medication reminder functionality
                                showBottomSheet.value = false
                                navController.navigate(Routes.PILLS)  // Dante added - This will open the Pills/reminder screen
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
        if (showDateDialog) {
            DatePickerDialog(
                initialDate = LocalDate.now(),
                onDateSelected = { date ->
                    viewModel.onDaySelected(date)
                    showDateDialog = false

                    if (launchedFromCompose) {
                        showBottomSheet.value = true
                        launchedFromCompose = false
                    }
                },
                onDismiss = { showDateDialog = false }
            )
        }



    // Wrap screen content with enter/exit animations
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
                    includeCenterFab = false,
                )
            }
        ) { innerPadding ->
            // FIXED: Main content WITHOUT verticalScroll() to avoid conflict with LazyVerticalGrid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(innerPadding)
                // REMOVED: .verticalScroll(rememberScrollState()) - This was causing the crash!
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Calendar month navigation header with improved styling
                CalendarHeader(
                    currentMonth = state.currentMonth,
                    onPreviousMonth = viewModel::goToPreviousMonth,
                    onNextMonth = viewModel::goToNextMonth
                )

                Spacer(modifier = Modifier.height(16.dp))

                // The Calendar weekday headers with modern styling
                CalendarWeekdayHeader()

                Spacer(modifier = Modifier.height(8.dp))

                // The calendar grid with improved design - this contains LazyVerticalGrid
                CalendarGrid(
                    currentMonth = state.currentMonth,
                    selectedDate = state.selectedDate,
                    onDayClick = { date ->
                        viewModel.onDaySelected(date)  // Updates selected date
                        showBottomSheet.value = true   // Shows the bottom sheet
                    }
                )

                // IMPROVED: Add some bottom padding for better appearance
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

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

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnCancelListener { onDismiss() }
    }.show()
}
