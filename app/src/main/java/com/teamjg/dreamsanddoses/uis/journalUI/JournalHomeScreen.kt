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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.uis.ComposePickerSheet
import kotlinx.coroutines.launch


/* Tabs representing the main sections inside Journal screen */
sealed class JournalTab(val title: String) {
    object Notes : JournalTab("Notes")
    object Journal : JournalTab("Home")
    object Lists : JournalTab("Lists")
}

/**
 * Main Journal screen with a top bar, bottom bar, and horizontal pager
 * to switch between Notes, Journal, and Lists tabs.
 *
 * @param navController Navigation controller for screen navigation.
 * @param defaultTab Default tab to show if no tab argument is passed.
 */@Composable
fun JournalHomeScreen(navController: NavController, defaultTab: String = "journal") {
    val tabs = listOf(JournalTab.Notes, JournalTab.Journal, JournalTab.Lists)

    // Get 'tab' parameter from navigation arguments or use defaultTab
    val tabParam = navController.currentBackStackEntry?.arguments?.getString("tab") ?: defaultTab

    // Map tab string to index for initial pager page
    val initialTabIndex = when (tabParam.lowercase()) {
        "notes" -> 0
        "journal" -> 1
        "lists" -> 2
        else -> 1
    }

    val pagerState = rememberPagerState(
        initialPage = initialTabIndex,
        pageCount = { tabs.size }
    )

    val coroutineScope = rememberCoroutineScope()

    var showComposePicker by remember { mutableStateOf(false) }

    val onCompose: () -> Unit = when (tabs[pagerState.currentPage]) {
        is JournalTab.Notes -> { { navController.navigate("notes/new") } }
        is JournalTab.Journal -> { { showComposePicker = true } }
        is JournalTab.Lists -> { { navController.navigate("lists/new") } }
    }

    Scaffold(
        topBar = {
            TopNavigationBar(
                type = NavigationBarType.JournalHome,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* TODO: Implement search functionality */ }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                type = NavigationBarType.Journal,
                onCompose = onCompose
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(innerPadding)
        ) {
            // Tab Row for switching tabs
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.LightGray,
                divider = {}, // No default divider
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
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

            if (showComposePicker) {
                ComposePickerSheet(
                    onDismiss = { showComposePicker = false },
                    onSelect = { selected ->
                        showComposePicker = false
                        when (selected) {
                            "reminder" -> navController.navigate("reminder/new")
                            "journal" -> navController.navigate("journal/new")
                            "notes" -> navController.navigate("notes/new")
                            "lists" -> navController.navigate("lists/new")
                            "canvas_editor" -> navController.navigate("canvas_editor")
                        }
                    }
                )
            }

            // Horizontal pager showing content for each tab
            HorizontalPager(
                state = pagerState,
                pageSpacing = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                when (tabs[page]) {
                    is JournalTab.Notes -> JournalNotesTabContent(navController)
                    is JournalTab.Journal -> JournalOverviewTabContent(navController)
                    is JournalTab.Lists -> ListsJournalTabContent(navController)
                }
            }
        }
    }
}