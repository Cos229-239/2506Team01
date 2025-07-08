package com.teamjg.dreamsanddoses.uis.filesScreen

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Handles PDF creation and file operations
 */
object PDFExporter {
    private const val TAG = "PDFExporter"

    /**
     * Gets all PDF files from the Documents directory by scanning the file system
     * This ensures PDFs persist even after the app is closed and reopened
     * @param context - Android context needed to access external files directory
     * @return List of PDF filenames found in the Documents directory
     */

    fun getExportedFiles(context: Context): List<String>
    {
        val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        return if (documentsDir != null && documentsDir.exists())
        {
            Log.d(TAG, "Scanning documents directory: ${documentsDir.absolutePath}")

            //Get all files in directory, filter for PDFs
            val pdfFiles = documentsDir.listFiles { file ->
                file.isFile && file.name.endsWith(".pdf", ignoreCase = true)
            }?.map { it.name }?.sorted() ?: emptyList()

            Log.d(TAG, "Found ${pdfFiles.size} PDF files: $pdfFiles")
            pdfFiles
        } else {
            Log.w(TAG, "Documents directory not found or doesn't exist")
            emptyList()
        }
    }

    /**
     * Creates a PDF from a list of medications and saves it to the device
     * @param context - Android context needed for file operations
     * @param medications - List of medication strings to include in the PDF
     * @param onComplete - Callback function that runs when PDF is successfully created
     */

    fun exportMedicationsToPDF(
        context: Context,
        medications: List<String>,
        onComplete: (File) -> Unit
    ) {
        Log.d(TAG, "Starting PDF export for ${medications.size} medications")

        //  Create document
        val document = PdfDocument()

        try {
            // Create a page with 612x792 points
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
            Log.d(TAG, "Created PDF page")

            val page = document.startPage(pageInfo)

            // Set up drawing tools
            val canvas = page.canvas
            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
                isAntiAlias = true
            }
            Log.d(TAG, "Initialized canvas")

            // Draw the PDF content
            val titlePaint = Paint().apply {
                textSize = 24f
                color = Color.BLACK
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText("My Medication List", 72f, 72f, titlePaint)

            // Add the current date under the title
            val datePaint = Paint().apply {
                textSize = 14f
                color = Color.GRAY
                isAntiAlias = true
            }
            val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $currentDate", 72f, 108f, datePaint)

            // Draw each medication as a bullet point
            var yPosition = 160f
            medications.forEachIndexed { index, medication ->
                Log.v(TAG, "Adding medication #${index + 1}: $medication")
                canvas.drawText("• $medication", 72f, yPosition, paint)
                yPosition += 30f

                // If we're getting close to the bottom of the page, we could add a new page, just leave for now

            }

            //Finish the page
            document.finishPage(page)
            Log.d(TAG, "Finished drawing content on the page")

            // Save the PDF to device storage
            val fileName = "Medications_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            // Check if we can access the documents directory
            if (outputDir == null) {
                Log.e(TAG, "Cannot access external files directory")
                Toast.makeText(context, "Cannot access file storage", Toast.LENGTH_SHORT).show()
                return
            }

            // Create the directory if it doesn't exist
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                Log.e(TAG, "Failed to create documents directory")
                Toast.makeText(context, "Failed to create storage directory", Toast.LENGTH_SHORT).show()
                return
            }

            val outputFile = File(outputDir, fileName)
            Log.d(TAG, "Saving PDF to: ${outputFile.absolutePath}")

            // Write the PDF data to the file
            try {
                FileOutputStream(outputFile).use { outputStream ->
                    document.writeTo(outputStream)
                }

                Log.i(TAG, "PDF successfully saved: ${outputFile.name}")
                Toast.makeText(context, "PDF exported successfully!", Toast.LENGTH_SHORT).show()

                // Call the completion callback
                onComplete(outputFile)

            } catch (e: Exception) {
                Log.e(TAG, "Failed to save PDF file", e)
                Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during PDF creation", e)
            Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            // close the document to free up memory
            document.close()
            Log.d(TAG, "PDF document closed")
        }
    }
    /**
     * Opens a PDF file using the device's default PDF viewer (external app)
     * @param context - Android context needed for launching intents
     * @param file - The PDF file to open
     */
    fun openPDF(context: Context, file: File) {
        try {
            // Use FileProvider for secure file sharing
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Give the PDF viewer app permission to read the file
            }

            context.startActivity(intent)
            Log.d(TAG, "Opened PDF: ${file.name}")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to open PDF", e)
            Toast.makeText(context, "No PDF viewer app found. Please install one.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Opens a PDF file within the app using navigation to PDFViewerScreen
     * This is the preferred method as it keeps users within your app
     * @param navController - Navigation controller to navigate to PDF viewer
     * @param fileName - Name of the PDF file to open
     */
    fun openPDFInApp(navController: androidx.navigation.NavController, fileName: String) {
        try {
            Log.d(TAG, "Opening PDF in app: $fileName")
            navController.navigate(com.teamjg.dreamsanddoses.navigation.Routes.createPDFViewerRoute(fileName))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to navigate to PDF viewer", e)
        }
    }

    /**
     * Creates a PDF from a list of journal entries and saves it to the device
     * @param context - Android context needed for file operations
     * @param journalEntries - List of journal entry strings to include in the PDF
     * @param onComplete - Callback function that runs when PDF is successfully created
     */
    fun exportJournalToPDF(
        context: Context,
        journalEntries: List<String>,
        onComplete: (File) -> Unit
    ) {
        Log.d(TAG, "Starting PDF export for ${journalEntries.size} journal entries")

        val document = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
            Log.d(TAG, "Created PDF page")

            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
                isAntiAlias = true
            }

            // Draw the PDF content
            val titlePaint = Paint().apply {
                textSize = 24f
                color = Color.BLACK
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText("My Journal Entries", 72f, 72f, titlePaint)

            val datePaint = Paint().apply {
                textSize = 14f
                color = Color.GRAY
                isAntiAlias = true
            }
            val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $currentDate", 72f, 108f, datePaint)

            // Draw each journal entry
            var yPosition = 160f
            journalEntries.forEachIndexed { index, entry ->
                Log.v(TAG, "Adding journal entry #${index + 1}: $entry")
                canvas.drawText("• $entry", 72f, yPosition, paint)
                yPosition += 30f
            }

            document.finishPage(page)
            Log.d(TAG, "Finished drawing journal content")

            // Save with Journal prefix
            val fileName = "Journal_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            saveAndComplete(context, document, fileName, onComplete)

        } catch (e: Exception) {
            Log.e(TAG, "Error during journal PDF creation", e)
            Toast.makeText(context, "Error creating journal PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
            Log.d(TAG, "Journal PDF document closed")
        }
    }

    /**
     * Creates a PDF from a list of dreams and saves it to the device
     * @param context - Android context needed for file operations
     * @param dreams - List of dream strings to include in the PDF
     * @param onComplete - Callback function that runs when PDF is successfully created
     */
    fun exportDreamsToPDF(
        context: Context,
        dreams: List<String>,
        onComplete: (File) -> Unit
    ) {
        Log.d(TAG, "Starting PDF export for ${dreams.size} dreams")

        val document = PdfDocument()

        try {
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
            Log.d(TAG, "Created PDF page")

            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
                isAntiAlias = true
            }

            // Draw the PDF content
            val titlePaint = Paint().apply {
                textSize = 24f
                color = Color.BLACK
                isAntiAlias = true
                isFakeBoldText = true
            }
            canvas.drawText("My Dream Journal", 72f, 72f, titlePaint)

            val datePaint = Paint().apply {
                textSize = 14f
                color = Color.GRAY
                isAntiAlias = true
            }
            val currentDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
            canvas.drawText("Generated on: $currentDate", 72f, 108f, datePaint)

            // Draw each dream
            var yPosition = 160f
            dreams.forEachIndexed { index, dream ->
                Log.v(TAG, "Adding dream #${index + 1}: $dream")
                canvas.drawText("• $dream", 72f, yPosition, paint)
                yPosition += 30f
            }

            document.finishPage(page)
            Log.d(TAG, "Finished drawing dreams content")

            // Save with Dreams prefix
            val fileName = "Dreams_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            saveAndComplete(context, document, fileName, onComplete)

        } catch (e: Exception) {
            Log.e(TAG, "Error during dreams PDF creation", e)
            Toast.makeText(context, "Error creating dreams PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
            Log.d(TAG, "Dreams PDF document closed")
        }
    }

    /**
     * Helper function to save PDF and handle completion callback
     * Reduces code duplication between different export functions
     */
    private fun saveAndComplete(
        context: Context,
        document: PdfDocument,
        fileName: String,
        onComplete: (File) -> Unit
    ) {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        if (outputDir == null) {
            Log.e(TAG, "Cannot access external files directory")
            Toast.makeText(context, "Cannot access file storage", Toast.LENGTH_SHORT).show()
            return
        }

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            Log.e(TAG, "Failed to create documents directory")
            Toast.makeText(context, "Failed to create storage directory", Toast.LENGTH_SHORT).show()
            return
        }

        val outputFile = File(outputDir, fileName)
        Log.d(TAG, "Saving PDF to: ${outputFile.absolutePath}")

        try {
            FileOutputStream(outputFile).use { outputStream ->
                document.writeTo(outputStream)
            }

            Log.i(TAG, "PDF successfully saved: ${outputFile.name}")
            Toast.makeText(context, "PDF exported successfully!", Toast.LENGTH_SHORT).show()
            onComplete(outputFile)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to save PDF file", e)
            Toast.makeText(context, "Failed to save PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** Delete a PDF file from the document directory
     * Handles actual file deletion
     * @param context - android context needed to access external files directory
     * @param fileName - name of PDF file to delete
     * @param onSuccess - Callback when file is deleted
     * @param onError - callback when deleting doesn't work
     */
    fun deletePDFFile(
        context: Context,
        fileName: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            if (documentsDir == null) {
                Log.e(ContentValues.TAG, "Cannot access documents directory for deletion")
                onError("Cannot access file storage")
                return
            }

            val fileToDelete = File(documentsDir, fileName)

            Log.d(ContentValues.TAG, "Attempting to delete file: ${fileToDelete.absolutePath}")
            Log.d(ContentValues.TAG, "File exists: ${fileToDelete.exists()}")

            if (!fileToDelete.exists()) {
                Log.w(ContentValues.TAG, "File does not exist: $fileName")
                onError("File not found")
                return
            }

            // Attempt to delete the file
            val deleteSuccessful = fileToDelete.delete()

            if (deleteSuccessful) {
                Log.i(ContentValues.TAG, "Successfully deleted file: $fileName")
                Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Log.e(ContentValues.TAG, "Failed to delete file: $fileName")
                onError("Failed to delete file")
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error deleting file: $fileName", e)
            onError("Error deleting file: ${e.message}")
            Toast.makeText(context, "Error deleting file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}