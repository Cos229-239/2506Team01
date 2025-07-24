package com.teamjg.dreamsanddoses.uis

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.teamjg.dreamsanddoses.uis.dreamsUI.DreamEntry
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

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

}