package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.border


// Constants for calendar layout logic
private const val DAYS_IN_WEEK = 7
//private const val MAX_WEEKS_DISPLAYED = 6
//private const val TOTAL_CALENDAR_CELLS = DAYS_IN_WEEK * MAX_WEEKS_DISPLAYED

// Represents a single day in the calendar, including whether it belongs to the current month
data class CalendarDay(
    val date: LocalDate?,
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

@Composable
fun CalendarWeekdayHeader()
{
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        for (day in daysOfWeek) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            )
            {
                Text(text = day, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/**  Composable to render a scrollable 7x6 grid of days for a given month.
Uses LazyVerticalGrid for performance and layout flexibility  **/
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarGrid(currentMonth: YearMonth, selectedDate: LocalDate?, onDayClick: (LocalDate) -> Unit) {
    val days = remember(currentMonth) { buildCalendarDays(currentMonth) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(days) { day ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(1.dp, Color.Black)
            ) {
                CalendarDayCell(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = { if (day.date != null) onDayClick(day.date) }
                )
            }
        }
    }
}

/**  Composable that represents a single calendar day cell.
Adjusts text color based on whether it belongs to the current month  **/
@Composable
fun CalendarDayCell(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.dp)
            .border(1.dp, Color.Transparent)
            .then(
                if (isSelected) Modifier.background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ) else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.label,
            modifier = Modifier.padding(4.dp),
            color = if (day.isCurrentMonth)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/**  Utility function to generate all 42 calendar cells (7 days × 6 weeks).
Fills overflow days from previous and next months for full grid display  **/
private fun buildCalendarDays(currentMonth: YearMonth): List<CalendarDay> {
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayIndex = firstDayOfMonth.dayOfWeek.value % DAYS_IN_WEEK

    val daysInMonth = currentMonth.lengthOfMonth()
    val dayList = mutableListOf<CalendarDay>()


    //Add empty placeholders before first day of month
    repeat(firstDayIndex)
    {
        dayList.add(CalendarDay(date = null, label = "", isCurrentMonth = false))
    }

    //Only add days for current month.
    for(day in 1..daysInMonth)
    { val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
        dayList.add(CalendarDay(date = date, label = day.toString(), isCurrentMonth = true))
    }

    return dayList
}