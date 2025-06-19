package com.teamjg.dreamsanddoses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.teamjg.dreamsanddoses.ui.theme.MyApplicationTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.modifier.modifierLocalOf
import java.time.LocalDate
import java.time.DayOfWeek

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Set screen to display calendar
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalendarScreen() //Load the Calendar. This will probably change once integration with teammates work starts

                }
            }
        }
    }
}
//Empty greeting.
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}


@OptIn(ExperimentalFoundationApi::class) //Allow LazyVerticalGrid usage
@Composable
fun CalendarScreen() //Function for holding the calendar.
{
    //Variables to hold current month
    val currentMonth = remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    //Determine days in month, and first day of week
    val daysInMonth = currentMonth.value.lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(currentMonth.value.year, currentMonth.value.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // This makes Sunday = 0

    //Actually build list of days to display
    val dayItems = mutableListOf<String>()

    //Add empty slots before first day to align.
    for (i in 0 until firstDayOfWeek) {
        dayItems.add("")
    }

    //Add days of month
    for (day in 1..daysInMonth) {
        dayItems.add(day.toString())
    }
//Layout of main calendar screen, utilizing column for easy vertical stacking
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        //Row for header with navigation buttons and month display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            //Go to previous month button
            Button(onClick = { currentMonth.value = currentMonth.value.minusMonths(1) })
            {
                Text(text = "Previous Month")
            }
            //Display current month and year
            Text(text = currentMonth.value.format(monthFormatter))

            //Next month button
            Button(onClick = { currentMonth.value = currentMonth.value.plusMonths(1) })
            {
                Text(text = "Next Month")
            }
        }

//Display calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), //7 columns for each day of week
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        {
            //Loop through each day, empty and actual days
            items(dayItems) { day ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // Makes each box square
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (day.isNotEmpty()) {
                        //Show the numerical day if box is not empty.
                        Text(text = day)
                    }
                }
            }
        }
    }
}


