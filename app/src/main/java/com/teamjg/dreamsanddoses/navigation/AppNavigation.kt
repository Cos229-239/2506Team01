package com.teamjg.dreamsanddoses.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// UI Screens
import com.teamjg.dreamsanddoses.uis.*
import com.teamjg.dreamsanddoses.uis.calendarUI.CalendarScreen
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamsColorPicker
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamsEditorScreen
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamsHomeScreen
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamsScreen
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamsTemplateScreen
import com.teamjg.dreamsanddoses.uis.filesScreen.FilesScreen
import com.teamjg.dreamsanddoses.uis.journalUI.*
import com.teamjg.dreamsanddoses.uis.loginUI.LoginScreen
import com.teamjg.dreamsanddoses.uis.loginUI.RegisterScreen
import com.teamjg.dreamsanddoses.uis.settingsUI.SettingsScreen

/* Centralized route definitions used throughout the app */
object Routes {
    // Authentication
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Home & Main Tabs
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val CALENDAR = "calendar"
    const val PILLS = "pills"
    const val FILES = "files"

    const val DREAMS = "dreams"
    const val DREAMS_HOME = "dreams_home"
    const val DREAMS_EDITOR = "dreams_editor"
    const val DREAMS_TEMPLATE = "dreams_template"
    const val COLOR_PICKER = "color_picker"

    const val CANVAS = "canvas"

    // Journal & Tabs
    const val JOURNAL_HOME = "journal_home"
    const val JOURNAL = "journal?tab={tab}&compose={compose}"

    // Editors
    const val NEW_JOURNAL = "journal/new"
    const val NEW_NOTE = "notes/new"
    const val LISTS_EDITOR = "lists/new"
    const val CANVAS_EDITOR = "canvas_editor"

    // Viewers
    const val PDF_VIEWER = "pdf_viewer/{fileName}"

    /** Builds dynamic route for journal tab navigation */
    fun journalRoute(tab: String): String = "$JOURNAL_HOME?tab=$tab"

    /** Builds route to open PDF viewer */
    fun createPDFViewerRoute(fileName: String): String = "pdf_viewer/$fileName"
}

/* Application's navigation graph */
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // ← change to Routes.HOME when skipping login
    ) {

        // Auth Screens
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Main Tabs
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.CALENDAR) { CalendarScreen(navController) }
        composable(Routes.PILLS) { PillsScreen(navController) }
        composable(Routes.FILES) { FilesScreen(navController) }
        composable(Routes.DREAMS) { DreamsScreen(navController) }
        composable(Routes.CANVAS) { CanvasScreen(navController) }
        composable(Routes.DREAMS_HOME) { DreamsHomeScreen(navController) }
        composable(Routes.DREAMS_EDITOR) { DreamsEditorScreen(navController) }
        composable(Routes.DREAMS_TEMPLATE) { DreamsTemplateScreen(navController) }
        composable(Routes.COLOR_PICKER) { DreamsColorPicker(navController) }

        // Journal Tabs
        composable(Routes.JOURNAL_HOME) { JournalHomeScreen(navController) }
        composable(
            route = "${Routes.JOURNAL_HOME}?tab={tab}",
            arguments = listOf(
                navArgument("tab") {
                    defaultValue = "journal"
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val tabArg = backStackEntry.arguments?.getString("tab") ?: "journal"
            JournalHomeScreen(navController = navController, defaultTab = tabArg)
        }

        // Combined Journal Screen
        composable(Routes.JOURNAL) { JournalScreen(navController) }

        // Editor Screens
        composable(Routes.NEW_JOURNAL) { JournalEditorScreen(navController, entryId = null) }

        composable("editor/{entryId}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            JournalEditorScreen(navController, entryId)
        }
        composable("notes/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NotesEditorScreen(navController, noteId)
        }
        composable(Routes.LISTS_EDITOR) { ListsEditorScreen(navController) }

        composable("lists/{listId}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")
            ListsEditorScreen(navController, listId)
        }

        composable(Routes.CANVAS_EDITOR) { CanvasEditorScreen(navController) }

        // File Viewers
        composable(Routes.PDF_VIEWER) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            PDFViewerScreen(navController, fileName)
        }
    }
}