package com.teamjg.dreamsanddoses.uis

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

object FirestoreService {

    @Suppress("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    // Save a test user
    fun saveTestUser(userId: String, name: String, email: String) {
        val userData = hashMapOf(
            "name" to name,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Error saving user", e)
            }
    }

    //  Test to read a user
    fun fetchUser(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    //Saved the scanned content based on type
    fun saveScannedContent(
        userID: String,
        title: String,
        content: String,
        type: String, //Medicine, journal entry, note, etc
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val timestamp = com.google.firebase.Timestamp.now()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        //Create document data
        val contentData = hashMapOf(
            "title" to title,
            "content" to content,
            "type" to type,
            "userId" to userID,
            "createdAt" to timestamp,
            "createdAtString" to dateFormat.format(timestamp.toDate()),
            "updatedAt" to timestamp
        )

        // TODO: Add extra fields for medications to extract dosage info. Was having issues with this so put it off for now.


        // Determine which collection to save to based on type
        val collectionName = when (type) {
            "Medication" -> "medications"
            "Journal Entry" -> "journal_entries"
            "Note" -> "notes"
            else -> "scanned_content" // fallback collection
        }

        // Save to Firestore
        db.collection(collectionName)
            .add(contentData as Map<String, Any?>)
            .addOnSuccessListener { documentReference ->
                Log.d("FirestoreService", "Scanned content saved with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error saving scanned content", exception)
                onFailure(exception)
            }
    }

    //Extract medication information from scanned text
    private fun extractMedicationInfo(content: String): Map<String, Any> {
        val medicationInfo = mutableMapOf<String, Any>()

        // Simple pattern matching for common medication information
        // We can expand these patterns based on common prescription formats

        // Look for dosage patterns (e.g., "5mg", "10 mg", "2.5mg")
        val dosagePattern = Regex("""(\d+(?:\.\d+)?)\s*mg""", RegexOption.IGNORE_CASE)
        val dosageMatch = dosagePattern.find(content)
        if (dosageMatch != null) {
            medicationInfo["dosage"] = dosageMatch.value
            medicationInfo["dosageAmount"] = dosageMatch.groupValues[1].toDoubleOrNull() ?: 0.0
        }

        // Look for frequency patterns (e.g., "once daily", "twice a day", "every 8 hours")
        val frequencyPatterns = listOf(
            Regex("""once\s+daily""", RegexOption.IGNORE_CASE),
            Regex("""twice\s+(a\s+)?daily?""", RegexOption.IGNORE_CASE),
            Regex("""(\d+)\s+times?\s+(a\s+)?day""", RegexOption.IGNORE_CASE),
            Regex("""every\s+(\d+)\s+hours?""", RegexOption.IGNORE_CASE),
            Regex("""as\s+needed""", RegexOption.IGNORE_CASE)
        )

        for (pattern in frequencyPatterns) {
            val match = pattern.find(content)
            if (match != null) {
                medicationInfo["frequency"] = match.value
                break
            }
        }

        // Look for common medication instructions
        val instructionPatterns = mapOf(
            "with_food" to Regex("""(with|after)\s+food""", RegexOption.IGNORE_CASE),
            "before_food" to Regex("""before\s+(food|meals?)""", RegexOption.IGNORE_CASE),
            "at_bedtime" to Regex("""(at\s+)?bedtime""", RegexOption.IGNORE_CASE),
            "in_morning" to Regex("""in\s+the\s+morning""", RegexOption.IGNORE_CASE)
        )

        for ((key, pattern) in instructionPatterns) {
            if (pattern.find(content) != null) {
                medicationInfo[key] = true
            }
        }

        return medicationInfo
    }

    //Fetch content by type for displaying in UI
    fun fetchContentByType(
        userId: String,
        type: String,
        onResult: (List<Map<String, Any>>) -> Unit
    ) {
        val collectionName = when (type) {
            "Medication" -> "medications"
            "Journal Entry" -> "journal_entries"
            "Note" -> "notes"
            else -> "scanned_content"
        }

        db.collection(collectionName)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val contentList = documents.map { it.data }
                Log.d("FirestoreService", "Fetched ${contentList.size} items of type $type")
                onResult(contentList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreService", "Error fetching content by type", exception)
                onResult(emptyList())
            }
    }

    // Fetch all scanned content for Files screen
    fun fetchAllScannedContent(
        userId: String,
        onResult: (List<Map<String, Any>>) -> Unit
    ) {
        val allContent = mutableListOf<Map<String, Any>>()
        val collections = listOf("medications", "journal_entries", "notes")
        var completedCollections = 0

        collections.forEach { collectionName ->
            db.collection(collectionName)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    allContent.addAll(documents.map { it.data })
                    completedCollections++

                    // When all collections are fetched, return the combined result
                    if (completedCollections == collections.size) {
                        // Sort by creation date (most recent first)
                        val sortedContent = allContent.sortedByDescending { document ->
                            val createdAt = document["createdAt"]
                            when (createdAt) {
                                is com.google.firebase.Timestamp -> createdAt.toDate().time
                                is Date -> createdAt.time
                                else -> 0L
                            }
                        }
                        Log.d("FirestoreService", "Fetched ${sortedContent.size} total scanned items")
                        onResult(sortedContent)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreService", "Error fetching from $collectionName", exception)
                    completedCollections++

                    // Still return results even if one collection fails
                    if (completedCollections == collections.size) {
                        val sortedContent = allContent.sortedByDescending { document ->
                            val createdAt = document["createdAt"]
                            when (createdAt) {
                                is com.google.firebase.Timestamp -> createdAt.toDate().time
                                is Date -> createdAt.time
                                else -> 0L
                            }
                        }
                        onResult(sortedContent)
                    }
                }
        }
    }
}