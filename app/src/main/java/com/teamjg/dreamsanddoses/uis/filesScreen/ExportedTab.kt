package com.teamjg.dreamsanddoses.uis.filesScreen

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.io.File

/**
 * UI content for the "Exports" tab.
 *
 * Displays a list of exported files using FileCardList.
 * Currently, uses placeholder data to simulate exported prescriptions.
 */

@Composable
fun ExportedTabContent(navController: NavController) {
    val context = LocalContext.current

    // Remember the list of exported files and update it when the tab is viewed
    val exportedFiles = remember { mutableStateOf<List<String>>(emptyList()) }

    val refreshFileList = {
        val pdfFiles = PDFExporter.getExportedFiles(context)
        exportedFiles.value = pdfFiles
    }
    //scan for PDF files
    LaunchedEffect(Unit)
    {
        refreshFileList()
    }

    // Display the exported files using our reusable FileCardList component
    FileCardList(
        files = exportedFiles.value,
        showActionsOnTap = true,
        onExportClick = null, // Don't show export button since these are already exported
        onViewClick = { fileName ->
            // When user clicks to view a PDF, open it within the app
            PDFExporter.openPDFInApp(navController, fileName)
        },
        onDeleteClick = { fileName ->
            // UPDATED: Handle PDF file deletion when delete button is clicked
            PDFExporter.deletePDFFile(
                context = context,
                fileName = fileName,
                onSuccess = {
                    // Refresh the file list after successful deletion
                    refreshFileList()
                },
                onError = { errorMessage ->
                    // Error handling is already done in PDFExporter.deletePDFFile
                    // (shows toast messages), so we don't need to do anything here
                }
            )
        }
    )
}