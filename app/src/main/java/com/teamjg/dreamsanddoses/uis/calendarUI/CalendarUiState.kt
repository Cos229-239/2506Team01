package com.teamjg.dreamsanddoses.uis.calendarUI

import java.time.YearMonth


/**  UI state for the Calendar screen,
     currently holds the selected month to display  **/
data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now()
)
