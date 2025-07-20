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

// Constants for calendar layout logic
private const val DAYS_IN_WEEK = 7

// Represents a single day in the calendar, including whether it belongs to the current month
data class CalendarDay(
    val date: LocalDate?,
    val label: String,
    val isCurrentMonth: Boolean
)

/**
 * FIXED: Composable to display the calendar's header section with proper structure
 * - Responsive padding based on screen size
 * - Modern card design with elevation
 * - Icon buttons for navigation
 */
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    // Get screen config for better dimension matching
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)
    val formatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy") }

    // Create card container for top of the calendar
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
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

            // Enhanced typography for the display of month and year
            Text(
                text = currentMonth.format(formatter),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Create icon button for next month, that matches previous month
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
 * FIXED: Weekday header as a separate function (was nested inside CalendarHeader)
 * - Gives consistent spacing and adapts to screen size
 * - Modern typography with proper font weights
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
 * FIXED: Calendar grid as a separate function with proper structure
 * - Responsive design that adapts to screen size
 * - Modern card container with elevation
 * - Proper grid spacing and layout
 * - UPDATED: Now uses surfaceVariant background to match header
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit
) {
    val days = remember(currentMonth) { buildCalendarDays(currentMonth) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = (screenWidth * 0.04f).coerceIn(12.dp, 24.dp)

    // Create the container for the entire grid with matching background color
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
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
 * - Shows different states (selected, today, current month)
 * - Proper color handling and typography
 */
@Composable
fun CalendarDayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = day.date == today

    // Define colors based on state
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        day.isCurrentMonth -> MaterialTheme.colorScheme.surface
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = day.date != null, onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
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
 * FIXED: Utility function moved to package level (not inside another function)
 * - Generates all 42 calendar cells (7 days × 6 weeks)
 * - Fills overflow days from previous and next months for full grid display
 */
private fun buildCalendarDays(currentMonth: YearMonth): List<CalendarDay> {
    val firstDayOfMonth = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayIndex = firstDayOfMonth.dayOfWeek.value % DAYS_IN_WEEK
    val daysInMonth = currentMonth.lengthOfMonth()
    val dayList = mutableListOf<CalendarDay>()

    // Add empty placeholders before first day of month
    repeat(firstDayIndex) {
        dayList.add(CalendarDay(date = null, label = "", isCurrentMonth = false))
    }

    // Add all days for current month
    for (day in 1..daysInMonth) {
        val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
        dayList.add(CalendarDay(date = date, label = day.toString(), isCurrentMonth = true))
    }

    // Fill remaining cells to complete the grid
    val remainingCells = 42 - dayList.size
    repeat(remainingCells) {
        dayList.add(CalendarDay(date = null, label = "", isCurrentMonth = false))
    }

    return dayList
}