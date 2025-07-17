package com.teamjg.dreamsanddoses.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teamjg.dreamsanddoses.uis.*
import com.teamjg.dreamsanddoses.uis.calendarUI.CalendarScreen
import com.teamjg.dreamsanddoses.uis.filesScreen.FilesScreen
import com.teamjg.dreamsanddoses.uis.journalUI.JournalScreen
import com.teamjg.dreamsanddoses.uis.loginUI.LoginScreen
import com.teamjg.dreamsanddoses.uis.loginUI.RegisterScreen
import com.teamjg.dreamsanddoses.uis.settingsUI.SettingsScreen

/* Centralized object to store route names */
object Routes {
    const val HOME = "home"
    const val JOURNAL = "journal"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val PILLS = "pills"
    const val FILES = "files"
    const val DREAMS = "dreams"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PDF_VIEWER = "pdf_viewer/{fileName}"

    //Create a PDF viewer route with the name of file
    fun createPDFViewerRoute(fileName: String): String
    {
        return "pdf_viewer/$fileName"
    }

}



//Main navigation host, managing all top-level screens
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Sets up the navigation graph with a start destination
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // Each screen is added as a route with its corresponding composable
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.JOURNAL) { JournalScreen(navController) }
        composable(Routes.CALENDAR) { CalendarScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
        composable(Routes.PILLS) { PillsScreen(navController) }
        composable(Routes.FILES) { FilesScreen(navController) }
        composable(Routes.DREAMS) { DreamsScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }

        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
                }

        composable(Routes.PDF_VIEWER) {backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            PDFViewerScreen(navController, fileName)
        }
            }
        }