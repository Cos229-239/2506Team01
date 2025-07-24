package com.teamjg.dreamsanddoses.uis

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

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
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}