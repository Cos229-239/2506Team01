package com.teamjg.dreamsanddoses.uis

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

// This object acts as a helper service to talk to Firestore in a clean, centralized way.
object FirestoreService {

    @Suppress("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance() // The main connection point to Firestore

    // Save a test user
    fun saveTestUser(userId: String, name: String, email: String) {
        // Prepare the data we want to save as key-value pairs
        val userData = hashMapOf(
            "name" to name,
            "email" to email
        )

        // Save the user data to a document with the user's ID
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Log.d("FirestoreService", "User saved successfully.") // Log on success
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Error saving user", e) // Log on error
            }
    }

    // Test to read a user
    fun fetchUser(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        // Try to get the user document from Firestore
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                // If the document exists, send the data back through onResult
                if (document.exists()) {
                    onResult(document.data)
                } else {
                    onResult(null) // No user found
                }
            }
            .addOnFailureListener {
                onResult(null) // Something went wrong
            }
    }

    // Dante added for Reminders to be saved in FireStore
    fun saveReminder(userId: String, title: String, time: String, notes: String) {
        // Build the reminder object to store
        val reminder = hashMapOf(
            "title" to title,
            "time" to time,
            "notes" to notes,
            "userId" to userId
        )

        // Add the reminder to the "reminders" subcollection under the user
        db.collection("users")
            .document(userId)
            .collection("reminders")
            .add(reminder)
            .addOnSuccessListener {
                Log.d("FirestoreService", "Reminder saved successfully.") // Confirmation log
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreService", "Error saving reminder", e) // Error log
            }
    }

    // Dante added for Reminders to be visible on the HomeScreen.kt
    fun fetchReminders(
        userId: String,
        onResult: (List<Triple<String, String, String>>) -> Unit
    ) {
        // Get all reminder documents for the given user
        db.collection("users")
            .document(userId)
            .collection("reminders")
            .get()
            .addOnSuccessListener { result ->
                // Convert Firestore documents into a list of reminder triples
                val reminderList = result.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val time = doc.getString("time")
                    val notes = doc.getString("notes")
                    val id = doc.id

                    // Combine the data into a readable format for UI
                    if (title != null && time != null && notes != null) {
                        Triple(id, title, "$time - $notes")
                    } else null
                }

                onResult(reminderList) // Pass back the list
            }
            .addOnFailureListener {
                onResult(emptyList()) // In case of error, return an empty list
            }
    }

    // Dante added for Reminders to have a delete option
    fun deleteReminder(userId: String, reminderId: String, onComplete: (Boolean) -> Unit) {
        // Delete the specific reminder document under the user's reminders
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