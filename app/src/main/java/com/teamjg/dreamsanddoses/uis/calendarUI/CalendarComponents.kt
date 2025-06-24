package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter


// Constants for calendar layout logic
private const val DAYS_IN_WEEK = 7
private const val MAX_WEEKS_DISPLAYED = 6
private const val TOTAL_CALENDAR_CELLS = DAYS_IN_WEEK * MAX_WEEKS_DISPLAYED

// Represents a single day in the calendar, including whether it belongs to the current month
data class CalendarDay(
    val label: String,
    val isCurrentMonth: Boolean
)

/** Composable to display the calendar's header section,
    including month/year label and navigation buttons **/
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPreviousMonth) {
            Text("Previous")
        }

        Text(
            text = currentMonth.format(formatter),
            style = MaterialTheme.typography.titleMedium
        )

        TextButton(onClick = onNextMonth) {
            Text("Next")
        }
    }
}


/**  Composable to render a scrollable 7x6 grid of days for a given month.
     Uses LazyVerticalGrid for performance and layout flexibility  **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarGrid(currentMonth: YearMonth) {
    // Recomputes when the currentMonth changes
    val days = remember(currentMonth) {
        buildCalendarDays(currentMonth)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(DAYS_IN_WEEK),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(days) { day ->
            CalendarDayCell(day)
        }
    }
}

/**  Composable that represents a single calendar day cell.
     Adjusts text color based on whether it belongs to the current month  **/
@Composable
fun CalendarDayCell(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Ensures square cells
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.label,
            color = if (day.isCurrentMonth)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**  Utility function to generate all 42 calendar cells (7 days × 6 weeks).
     Fills overflow days from previous and next months for full grid display  **/
private fun buildCalendarDays(currentMonth: YearMonth): List<CalendarDay> {
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayIndex = firstDayOfMonth.dayOfWeek.value % DAYS_IN_WEEK

    val daysInMonth = currentMonth.lengthOfMonth()
    val previousMonth = currentMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    val dayList = mutableListOf<CalendarDay>()

    // Add trailing days from the previous month to align first weekday
    for (i in firstDayIndex downTo 1) {
        val label = (daysInPreviousMonth - i + 1).toString()
        dayList.add(CalendarDay(label = label, isCurrentMonth = false))
    }

    // Add all days for the current month
    for (day in 1..daysInMonth) {
        dayList.add(CalendarDay(label = day.toString(), isCurrentMonth = true))
    }

    // Add leading days from next month to complete the 6-week grid
    val remaining = TOTAL_CALENDAR_CELLS - dayList.size
    for (day in 1..remaining) {
        dayList.add(CalendarDay(label = day.toString(), isCurrentMonth = false))
    }

    return dayList
}
