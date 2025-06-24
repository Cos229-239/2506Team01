package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.navigation.AnimatedScreenWrapper

/**
 * Main Calendar screen composable that displays
 * a calendar header and grid, wrapped in a scaffold
 * with a top navigation bar and animated screen transition
 *
 * @param navController for navigation handling and back stack management
 * @param viewModel provides calendar UI state and navigation callbacks
 */
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    // Collect UI state from ViewModel, auto-updates on changes
    val state by viewModel.uiState.collectAsState()

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

                // The calendar grid displaying days for currentMonth
                CalendarGrid(currentMonth = state.currentMonth)
            }
        }
    }
}

