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
    fun saveReminder(
        userId: String,
        title: String,
        time: String,
        notes: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val reminderData = hashMapOf(
            "title" to title,
            "time" to time,
            "notes" to notes
        )

        db.collection("users")
            .document(userId)
            .collection("reminders")
            .add(reminderData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}