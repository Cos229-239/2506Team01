package com.teamjg.dreamsanddoses.uis.calendarUI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// Total days in a week (used for grid and indexing logic)
private const val DAYS_IN_WEEK = 7

// Represents a day in the calendar view
data class CalendarDay(
    val date: LocalDate?,        // Actual date object, null for padding cells
    val label: String,           // Day label (e.g., "1", "2", or "")
    val isCurrentMonth: Boolean  // Whether this day is in the displayed month
)

/**
 * CalendarHeader composable
 * Displays the top section of the calendar with:
 * - Month and year title
 * - Left and right buttons to navigate months
 */
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,        // Current month shown in the calendar
    onPreviousMonth: () -> Unit,    // Callback when left arrow is tapped
    onNextMonth: () -> Unit         // Callback when right arrow is tapped
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // Responsive horizontal padding based on screen size
    val horizontalPadding = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        // Row containing the previous arrow, month label, and next arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month button
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Current month/year text label (e.g., "July 2025")
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Next month button
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * CalendarWeekdayHeader composable
 * Displays static weekday labels ("Sun", "Mon", ..., "Sat")
 */
@Composable
fun CalendarWeekdayHeader() {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Evenly spaced day headers
        for (day in daysOfWeek) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * CalendarGrid composable
 * Creates a 7-column x 6-row layout of days for the selected month
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,             // The month to display
    selectedDate: LocalDate?,            // Currently selected day
    onDayClick: (LocalDate) -> Unit      // Called when a day is tapped
) {
    // Build the list of CalendarDay objects to display
    val days = remember(currentMonth) { buildCalendarDays(currentMonth) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)

    // Card background for the grid area
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), // 7 days per row
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Render each CalendarDay in the grid
            items(days) { day ->
                CalendarDayCell(
                    day = day,
                    isSelected = day.date == selectedDate,
                    onClick = { if (day.date != null) onDayClick(day.date) }
                )
            }
        }
    }
}

/**
 * CalendarDayCell composable
 * Represents a single calendar day cell
 * - Applies different styles if selected, today, or outside current month
 */
@Composable
fun CalendarDayCell(
    day: CalendarDay,           // Day object (date + label)
    isSelected: Boolean,        // If this day is selected
    onClick: () -> Unit         // Click action for selecting a day
) {
    val today = LocalDate.now()
    val isToday = day.date == today

    // Choose background color based on state
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        day.isCurrentMonth -> MaterialTheme.colorScheme.surface
        else -> Color.Transparent
    }

    // Choose text color based on state
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Forces square cells
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = day.date != null, onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Only draw label if non-empty (skip for blank padding cells)
        if (day.label.isNotEmpty()) {
            Text(
                text = day.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = when {
                        isSelected -> FontWeight.Bold
                        isToday -> FontWeight.SemiBold
                        else -> FontWeight.Normal
                    }
                ),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * buildCalendarDays utility function
 * Generates all calendar grid entries:
 * - Blank padding cells before the 1st day
 * - All actual days of the month
 * - Filler cells to complete a 6-row grid (42 total)
 */
private fun buildCalendarDays(currentMonth: YearMonth): List<CalendarDay> {
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayIndex = firstDayOfMonth.dayOfWeek.value % DAYS_IN_WEEK // Normalize Sunday = 0
    val daysInMonth = currentMonth.lengthOfMonth()
    val dayList = mutableListOf<CalendarDay>()

    // Add placeholders for days before the first of the month
    repeat(firstDayIndex) {
        dayList.add(CalendarDay(date = null, label = "", isCurrentMonth = false))
    }

    // Add actual days of the month
    for (day in 1..daysInMonth) {
        val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
        dayList.add(CalendarDay(date = date, label = day.toString(), isCurrentMonth = true))
    }

    // Fill out the remaining cells to complete a 6-week grid (6x7 = 42)
    val remainingCells = 42 - dayList.size
    repeat(remainingCells) {
        dayList.add(CalendarDay(date = null, label = "", isCurrentMonth = false))
    }

    return dayList
}
