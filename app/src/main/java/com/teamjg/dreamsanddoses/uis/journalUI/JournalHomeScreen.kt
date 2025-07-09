package com.teamjg.dreamsanddoses.uis.journalUI

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import kotlinx.coroutines.launch


// Tabs used in the Journal screen pager
sealed class JournalTab(val title: String) {
    object Notes : JournalTab("Notes")
    object Journal : JournalTab("Home")
    object Lists : JournalTab("Lists")
}

// Main Journal screen composable with top/bottom navigation and pager for tabs
@Composable
fun JournalHomeScreen(navController: NavController) {

    val tabs = listOf(JournalTab.Notes, JournalTab.Journal, JournalTab.Lists)

    // Starts on the center tab (Journal)
    val pagerState = rememberPagerState(initialPage = 1) { tabs.size }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.Journal,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* Handle search */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Journal
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
        ) {
            // Tab row across the top
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.LightGray,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = tab.title,
                                maxLines = 1,
                                softWrap = false,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // Pager content for each tab
            HorizontalPager(
                state = pagerState,
                pageSpacing = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                when (tabs[page]) {
                    is JournalTab.Notes -> NotesJournalTabContent()
                    is JournalTab.Journal -> JournalOverviewTabContent(navController)
                    is JournalTab.Lists -> ListsJournalTabContent()
                }
            }
        }
    }
}

