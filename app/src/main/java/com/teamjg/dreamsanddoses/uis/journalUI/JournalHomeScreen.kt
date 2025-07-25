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
import com.teamjg.dreamsanddoses.navigation.ComposePickerSheet
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import kotlinx.coroutines.launch

/**
 * Sealed class representing the three main tabs in the Journal screen.
 * Each tab has a human-readable title.
 */
sealed class JournalTab(val title: String) {
    object Notes : JournalTab("Notes")
    object Journal : JournalTab("Home")
    object Lists : JournalTab("Lists")
}

/**
 * Main Journal screen composable containing:
 * - Top navigation bar
 * - Bottom navigation bar with contextual FAB actions
 * - TabRow for switching between Notes, Journal, and Lists tabs
 * - Horizontal pager to show the content for the selected tab
 *
 * @param navController Navigation controller for handling navigation between screens.
 * @param defaultTab String indicating which tab to select by default if no "tab" argument is passed.
 */
@Composable
fun JournalHomeScreen(navController: NavController, defaultTab: String = "journal") {
    // List of all tabs in fixed order
    val tabs = listOf(JournalTab.Notes, JournalTab.Journal, JournalTab.Lists)

    // Retrieve the current "tab" argument from navigation, fallback to defaultTab parameter.
    val tabParam = navController.currentBackStackEntry?.arguments?.getString("tab") ?: defaultTab

    // Map the tab string param to the corresponding index in the tabs list.
    val initialTabIndex = when (tabParam.lowercase()) {
        "notes" -> 0
        "journal" -> 1
        "lists" -> 2
        else -> 1 // Default fallback to Journal tab if unknown tab name.
    }

    // Remember pager state, initialized to the selected tab index.
    val pagerState = rememberPagerState(
        initialPage = initialTabIndex,
        pageCount = { tabs.size }
    )

    // Coroutine scope used for launching animations and side effects.
    val coroutineScope = rememberCoroutineScope()

    // State controlling visibility of the ComposePickerSheet (modal bottom sheet).
    var showComposePicker by remember { mutableStateOf(false) }

    // Determine what the FAB "compose" button should do depending on the current tab.
    val onCompose: () -> Unit = when (tabs[pagerState.currentPage]) {
        is JournalTab.Notes -> { { navController.navigate("notes/new") } }       // Navigate to new note screen.
        is JournalTab.Journal -> { { showComposePicker = true } }                // Show picker sheet for journal-related options.
        is JournalTab.Lists -> { { navController.navigate("lists/new") } }      // Navigate to new list screen.
    }

    Scaffold(
        topBar = {
            // Custom top navigation bar for the Journal Home screen.
            TopNavigationBar(
                type = NavigationBarType.JournalHome,
                navController = navController,
                useIconHeader = true,
                onSearchClick = { /* TODO: Implement search functionality */ }
            )
        },
        bottomBar = {
            // Custom bottom navigation bar with dynamic compose button action.
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
                .background(Color.LightGray) // Background color for the content area.
                .padding(innerPadding)       // Padding provided by Scaffold (e.g., for bottom bar)
        ) {
            // TabRow displays the horizontal list of tabs at the top of the screen.
            TabRow(
                selectedTabIndex = pagerState.currentPage, // Currently selected tab index.
                containerColor = Color.LightGray,           // Background color of tab row.
                divider = {},                               // Remove default divider line.
                indicator = { tabPositions ->
                    // Secondary indicator shows a 2dp underline below the selected tab.
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp)               // Subtle shadow below tab row.
            ) {
                // Create a Tab composable for each tab.
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index, // Highlight if selected.
                        onClick = {
                            // Animate pager scroll to the tapped tab.
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = tab.title,             // Tab label text.
                                maxLines = 1,                 // Single line text.
                                softWrap = false,             // Prevent wrapping.
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            // Show the ComposePickerSheet modal if triggered by FAB action.
            if (showComposePicker) {
                ComposePickerSheet(
                    onDismiss = { showComposePicker = false }, // Hide the sheet on dismiss.
                    onSelect = { selected ->
                        showComposePicker = false
                        // Navigate to the selected compose screen.
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

            // HorizontalPager shows the content corresponding to the selected tab.
            HorizontalPager(
                state = pagerState,
                pageSpacing = 0.dp, // No spacing between pages.
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                // Display tab content based on the selected page.
                when (tabs[page]) {
                    is JournalTab.Notes -> JournalNotesTabContent(navController)        // Notes tab content.
                    is JournalTab.Journal -> JournalOverviewTabContent(navController)   // Journal/Home tab content.
                    is JournalTab.Lists -> ListsJournalTabContent(navController)        // Lists tab content.
                }
            }
        }
    }
}
