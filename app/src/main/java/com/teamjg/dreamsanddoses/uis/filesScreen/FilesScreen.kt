package com.teamjg.dreamsanddoses.uis.filesScreen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilesScreen(navController: NavController) {

    BackHandler { /* Do nothing = disable back press & back swipe */ }

    /* Spawn in the bottom toolbar */
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, NavigationBarType.Files) },
    ) { innerPadding ->
        // Padding space for the Scaffold's safe area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    // Add or remove tabs here
    val tabs = listOf("Notes", "Journal", "Lists", "All Files", "Medications", "Dreams", "Imports", "Exports")

    // Pager state remembers the currently selected tab (starts at "All Files")
    val pagerState = rememberPagerState(initialPage = 3) { tabs.size }

    // Coroutine scope for running animations (like scrolling between tabs)
    val coroutineScope = rememberCoroutineScope()

    // Spawn in top toolbar
    Column(modifier = Modifier.fillMaxSize()) {
        TopNavigationBar(
            type = NavigationBarType.Files,
            navController = navController,
            useIconHeader = true,
            onSearchClick = { /* ----TODO: handle search----- */ }
        )

        // Tab row (scrollable) for navigating between file categories
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.LightGray,
            edgePadding = 0.dp, // removes the gap at the start/end
            divider = { /* No divider */ },
            indicator = { tabPositions ->
                // Animated indicator that slides beneath the selected tab
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 2.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp) // Top shadow to create a cascading UI layer
        )
        {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            title,
                            maxLines = 1,
                            softWrap = false,
                            // Supposed to highlight the "All Files" tab button with a bold font
                            style = if (title == "All Files") {
                                MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            } else {
                                MaterialTheme.typography.labelLarge
                            }
                        )
                    }

                )

            }
        }
        // Section label for recent files
        Text(
            text = "This Week",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Horizontal pager (swipeable content below tab row)
        HorizontalPager(
            state = pagerState,
            pageSpacing = 0.dp,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Render the correct screen based on selected tab index
            when (page) {
                0 -> NotesTabContent()
                1 -> JournalTabContent()
                2 -> ListsTabContent()
                3 -> AllFilesTabContent()
                4 -> MedicationsTabContent()
                5 -> DreamsTabContent()
                6 -> ImportedTabContent()
                7 -> ExportedTabContent()
            }
        }

    }
}
