package com.teamjg.dreamsanddoses.uis

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamEntry
import com.teamjg.dreamsanddoses.uis.journalUI.Line
import java.text.SimpleDateFormat
import java.util.*


object FirestoreService {

    @Suppress("StaticFieldLeak")
    val db = FirebaseFirestore.getInstance()

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

    fun saveDreamEntry(
        userId: String,
        entryId: String = UUID.randomUUID().toString(),
        title: String,
        content: String,
        location: String?,
        time: String?,
        people: String?,
        mood: String?,
        colorHex: String?,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val dreamData = hashMapOf(
            "title" to title,
            "content" to content,
            "location" to location,
            "time" to time,
            "people" to people,
            "mood" to mood,
            "color" to colorHex,
            "createdAt" to Timestamp.now(),
            "lastEdited" to Timestamp.now()
        )

        db.collection("users").document(userId)
            .collection("dreams").document(entryId)
            .set(dreamData)
            .addOnSuccessListener {
                Log.d("FirestoreService", "Dream entry saved.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Failed to save dream entry", e)
                onFailure(e)
            }
    }


    fun fetchRecentDreams(
        userId: String,
        limit: Long = 5,
        onResult: (List<DreamEntry>) -> Unit
    ) {
        db.collection("users").document(userId)
            .collection("dreams")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { snapshot ->
                val dreams = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val title = data["title"] as? String ?: "Untitled"
                    val description = data["content"] as? String ?: ""
                    val date = data["createdAt"]?.let {
                        if (it is Timestamp) SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.toDate())
                        else "Unknown Date"
                    } ?: "Unknown Date"
                    DreamEntry(title, date, description)
                }
                onResult(dreams)
            }
            .addOnFailureListener {
                onResult(emptyList())
                Log.e("FirestoreService", "Error fetching recent dreams", it)
            }
    }

    fun saveCanvas(
        lines: List<Line>,
        title: String,
        previewUrl: String? = null,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete(false)
        val db = FirebaseFirestore.getInstance()

        val lineData = lines.map { line ->
            mapOf("points" to line.points.map { pt -> mapOf("x" to pt.x, "y" to pt.y) })
        }

        val canvas = hashMapOf(
            "title" to title,
            "createdAt" to Timestamp.now(),
            "lines" to lineData,
            "previewUrl" to previewUrl
        )

        db.collection("users")
            .document(userId)
            .collection("canvases")
            .add(canvas)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    data class CanvasPreview(
        val id: String,
        val title: String,
        val previewUrl: String?,
        val createdAt: Timestamp,
        val tags: List<String> = emptyList()
    )

    fun fetchCanvasPreviews(
        onResult: (List<CanvasPreview>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .collection("canvases")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val previews = snapshot.documents.mapNotNull { doc ->
                    val title = doc.getString("title") ?: "Untitled"
                    val previewUrl = doc.getString("previewUrl")
                    val createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                    val tags = doc.get("tags") as? List<String> ?: emptyList()

                    CanvasPreview(
                        id = doc.id,
                        title = title,
                        previewUrl = previewUrl,
                        createdAt = createdAt,
                        tags = tags
                    )
                }
                onResult(previews)
            }
            .addOnFailureListener { error ->
                onError(error)
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
        val timestamp = Timestamp.now()
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
            .orderBy("createdAt", Query.Direction.DESCENDING)
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
                .orderBy("createdAt", Query.Direction.DESCENDING)
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
                                is Timestamp -> createdAt.toDate().time
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
                                is Timestamp -> createdAt.toDate().time
                                is Date -> createdAt.time
                                else -> 0L
                            }
                        }
                        onResult(sortedContent)
                    }
                }
        }
    }

    // Dante added for Reminders to be saved in FireStore
    fun saveReminder(userId: String, title: String, time: String, notes: String) {
        val reminder = hashMapOf(
            "title" to title,
            "time" to time,
            "notes" to notes,
            "userId" to userId
        )

        db.collection("users")
            .document(userId)
            .collection("reminders")
            .add(reminder)
            .addOnSuccessListener {
                Log.d("FirestoreService", "Reminder saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Error saving reminder", e)
            }
    }

    // Dante added for Reminders to be visible on the HomeScreen.kt
    fun fetchReminders(
        userId: String,
        onResult: (List<Triple<String, String, String>>) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("reminders")
            .get()
            .addOnSuccessListener { result ->
                val reminderList = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val time = doc.getString("time")
                    val notes = doc.getString("notes")
                    val id = doc.id
                    if (title != null && time != null && notes != null) {
                        Triple(id, title, "$time - $notes")
                    } else null
                }
                onResult(reminderList)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // Dante added for Reminders to have a delete option
    fun deleteReminder(userId: String, reminderId: String, onComplete: (Boolean) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("reminders")
            .document(reminderId)
            .delete()
            .addOnSuccessListener {
                onComplete(true) // Notify success
            }
            .addOnFailureListener {
                onComplete(false) // Notify failure
            }
    }
}