package com.teamjg.dreamsanddoses.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore


object FireStoreService {

    @Suppress("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()

    fun addJournal(userId: String, title: String, content: String) {
        val journalData = hashMapOf(
            "title" to title,
            "content" to content,
            "createdOn" to Timestamp.now(),
            "updatedOn" to Timestamp.now()
        )

        db.collection("users")
            .document(userId)
            .collection("journals")
            .add(journalData)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "Journal entry added with ID ${docRef.id} for user $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding journal entry", e)
            }
    }
}