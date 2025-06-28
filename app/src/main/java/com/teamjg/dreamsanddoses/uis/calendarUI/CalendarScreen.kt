package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper
import java.time.format.DateTimeFormatter

/**
 * Main Calendar screen composable that displays
 * a calendar header and grid, wrapped in a scaffold
 * with a top navigation bar and animated screen transition
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

    // State for controlling bottom sheet visibility
    var showBottomSheet = remember { mutableStateOf(false) }


    // Bottom sheet state (controls animation/behavior)
    val sheetState = rememberModalBottomSheetState()

    // Bottom Sheet that will overlay the calendar
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false }, // Called when user taps outside
            sheetState = sheetState // Controls animation behavior
        ) {
            // Content of the overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Display selected date
                Text(
                    text = "Selected: ${state.selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                // PLACEHOLDER: Will be replaced with medication input later
                Text("Medication reminder entry will go here")
            }
        }
    }

    // Wrap screen content with enter/exit animations and optional auto pop back
    AnimatedScreenWrapper(navController = navController) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.Calendar,
                    navController = navController,
                    useIconHeader = true
                )
            }
        ) { innerPadding ->
            // Main content column with padding applied from scaffold
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Calendar month navigation header (Previous, Month-Year, Next)
                CalendarHeader(
                    currentMonth = state.currentMonth,
                    onPreviousMonth = viewModel::goToPreviousMonth,
                    onNextMonth = viewModel::goToNextMonth
                )

                //The Calendar grid displays days of week
                CalendarWeekdayHeader()

                // The calendar grid displaying days for currentMonth
                CalendarGrid(
                    currentMonth = state.currentMonth,
                    selectedDate = state.selectedDate,
                    onDayClick = { date ->
                        viewModel.onDaySelected(date)  // Updates selected date
                        showBottomSheet.value = true   // Shows the bottom sheet
                    }
                )
            }
        }
    }
}