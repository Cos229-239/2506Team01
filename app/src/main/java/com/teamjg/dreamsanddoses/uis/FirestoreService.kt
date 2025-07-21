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
}