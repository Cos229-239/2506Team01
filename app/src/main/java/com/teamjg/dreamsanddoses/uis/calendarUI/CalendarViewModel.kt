package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate


/**  ViewModel managing the UI state for the Calendar screen.
     Holds the current month and exposes navigation functions  **/
class CalendarViewModel : ViewModel() {

    // Backing state flow holding the current calendar UI state
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState

    //Handles when a date is selected
    fun onDaySelected(date: LocalDate)
    {
        _uiState.value = _uiState.value.copy(
            selectedDate = date //Saves the date clicked
        )
    }
    // Navigate to the previous month by updating the UI state
    fun goToPreviousMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.minusMonths(1)
        )
    }

    // Navigate to the next month by updating the UI state
    fun goToNextMonth() {
        _uiState.value = _uiState.value.copy(
            currentMonth = _uiState.value.currentMonth.plusMonths(1)
        )
    }

}
