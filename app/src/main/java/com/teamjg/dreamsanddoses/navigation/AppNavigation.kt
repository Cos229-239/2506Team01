package com.teamjg.dreamsanddoses.navigation

// Core AndroidX Compose and Navigation imports
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Importing all UI screens used in the navigation graph
import com.teamjg.dreamsanddoses.uis.*
import com.teamjg.dreamsanddoses.uis.calendarUI.CalendarScreen
import com.teamjg.dreamsanddoses.uis.dreamsUI.*
import com.teamjg.dreamsanddoses.uis.filesScreen.FilesScreen
import com.teamjg.dreamsanddoses.uis.journalUI.*
import com.teamjg.dreamsanddoses.uis.loginUI.*
import com.teamjg.dreamsanddoses.uis.settingsUI.SettingsScreen

/**
 * Centralized list of all navigation route constants used throughout the app.
 * Helps keep navigation logic consistent and easy to update.
 */
object Routes {
    // Authentication Screens
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password" // Added for Forgot Password support

    // Primary Home Tabs
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val CALENDAR = "calendar"
    const val PILLS = "pills"
    const val FILES = "files"
    const val DREAMS = "dreams"
    const val DREAMS_HOME = "dreams_home"
    const val DREAMS_EDITOR = "dreams/new"
    const val DREAMS_TEMPLATE = "dreams_template"
    const val COLOR_PICKER = "color_picker"
    const val CANVAS = "canvas"

    // Journal tabs and composition
    const val JOURNAL_HOME = "journal_home"
    const val JOURNAL = "journal?tab={tab}&compose={compose}"

    // Editor Screens
    const val NEW_JOURNAL = "journal/new"
    const val NEW_NOTE = "notes/new"
    const val LISTS_EDITOR = "lists/new"
    const val CANVAS_EDITOR = "canvas_editor"

    // File-related screens
    const val PDF_VIEWER = "pdf_viewer/{fileName}"
    const val SCANNER = "scanner"

    // Helper to create dynamic journal tab route
    fun journalRoute(tab: String): String = "$JOURNAL_HOME?tab=$tab"

    // Helper to create PDF viewer route with a specific filename
    fun createPDFViewerRoute(fileName: String): String = "pdf_viewer/$fileName"
}

/**
 * App's top-level navigation graph declaration using Jetpack Compose Navigation.
 * This function builds out all routes and screen destinations used in the app.
 *
 * @param navController The NavHostController driving navigation stack. If not passed in, a default one is created.
 */
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN // ← Change to Routes.HOME to skip login during development
    ) {
        // Authentication Screens
        composable(Routes.LOGIN) { LoginScreen(navController) }
        composable(Routes.REGISTER) { RegisterScreen(navController) }
        composable(Routes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }

        // Main Application Tabs
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.CALENDAR) { CalendarScreen(navController) }
        composable(Routes.PILLS) { PillsScreen(navController) }
        composable(Routes.FILES) { FilesScreen(navController) }
        composable(Routes.DREAMS) { DreamsScreen(navController) }
        composable(Routes.CANVAS) { CanvasScreen(navController) }

        // Dreams-specific screens
        composable(Routes.DREAMS_HOME) { DreamsHomeScreen(navController) }
        composable(Routes.DREAMS_EDITOR) { DreamsEditorScreen(navController) }
        composable(Routes.DREAMS_TEMPLATE) { DreamsTemplateScreen(navController) }
        composable(Routes.COLOR_PICKER) { DreamsColorPicker(navController) }

        // Journal Home screen with optional tab argument
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

        // Unified Journal screen for all types (optional: tab + compose param)
        composable(Routes.JOURNAL) { JournalScreen(navController) }

        // Journal entry creation
        composable(Routes.NEW_JOURNAL) { JournalEditorScreen(navController, entryId = null) }

        // Editor route to edit existing journal entry by ID
        composable("editor/{entryId}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId")
            JournalEditorScreen(navController, entryId)
        }

        // Notes editor for editing individual notes
        composable("notes/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            NotesEditorScreen(navController, noteId)
        }

        // New list editor screen
        composable(Routes.LISTS_EDITOR) { ListsEditorScreen(navController) }

        // Open an existing list by ID
        composable("lists/{listId}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")
            ListsEditorScreen(navController, listId)
        }

        // Dream template editor for existing dream entries
        composable("dreams/{dreamId}") { backStackEntry ->
            val dreamId = backStackEntry.arguments?.getString("dreamId")
            DreamsTemplateScreen(navController, dreamId)
        }

        // Canvas-based drawing screen
        composable(Routes.CANVAS_EDITOR) { CanvasEditorScreen(navController) }

        // View a PDF file by filename (used for recent files / scanned files)
        composable(Routes.PDF_VIEWER) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            PDFViewerScreen(navController, fileName)
        }

        // Scanner screen used to scan documents or notes
        composable(Routes.SCANNER) { ScannerScreen(navController) }
    }
}

/**
 * Modal bottom sheet used to show "What would you like to compose?" options.
 * Acts as a floating action menu to choose between new entry types.
 *
 * @param onDismiss Called when the bottom sheet is dismissed
 * @param onSelect Called with selected entry type string ("reminder", "journal", etc.)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposePickerSheet(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("What would you like to compose?", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            // Compose Options
            ComposeOption("New Reminder") { onSelect("reminder") }
            ComposeOption("New Journal Entry") { onSelect("journal") }
            ComposeOption("New List") { onSelect("lists") }
            ComposeOption("New Note") { onSelect("notes") }
            ComposeOption("New Dream") { onSelect("dreams") }
            ComposeOption("Draw Something") { onSelect("canvas_editor") }
        }
    }
}

/**
 * A selectable row representing a single compose action inside the bottom sheet.
 *
 * @param text Label to display
 * @param onClick Action to trigger on selection
 */
@Composable
fun ComposeOption(text: String, onClick: () -> Unit) {
    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}
