package com.teamjg.dreamsanddoses.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.teamjg.dreamsanddoses.uis.journalUI.JournalEntry
import java.util.Date


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

    fun fetchJournalEntries(
        userId: String,
        onResult: (List<JournalEntry>) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("journals")
            .orderBy("createdOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val entries = result.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val content = doc.getString("content") ?: ""
                    val timestamp = doc.getTimestamp("createdOn")?.toDate() ?: Date()
                    JournalEntry(title, content, timestamp)
                }
                onResult(entries)
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting journal entries", it)
                onResult(emptyList())
            }
    }

    fun addNote(userId: String, title: String, tag: String, content: String) {
        val noteData = hashMapOf(
            "title" to title,
            "tag" to tag,
            "content" to content,
            "createdOn" to Timestamp.now(),
            "updatedOn" to Timestamp.now()
        )

        db.collection("users")
            .document(userId)
            .collection("notes")
            .add(noteData)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "Note entry added with ID ${docRef.id} for user $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding note entry", e)
            }
    }

    fun fetchNotesEntries(
        userId: String,
        onResult: (List<JournalEntry>) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("notes")
            .orderBy("createdOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val entries = result.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val content = doc.getString("content") ?: ""
                    val tag = doc.getString("tag") ?: ""
                    val timestamp = doc.getTimestamp("createdOn")?.toDate() ?: Date()
                    JournalEntry(title, content, timestamp)
                }
                onResult(entries)
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting notes entries", it)
                onResult(emptyList())
            }
    }

    fun addList(
        title: String,
        tag: String,
        checklistItems: MutableList<com.teamjg.dreamsanddoses.uis.journalUI.ChecklistItem>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onFailure(Exception("User not authenticated"))
            return
        }

        val checklistData = checklistItems.map {
            mapOf("text" to it.text, "isChecked" to it.isChecked)
        }

        val listData = hashMapOf(
            "title" to title,
            "tag" to tag,
            "checklistItems" to checklistData,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("lists")
            .add(listData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun fetchListEntries(userId: String, onResult: (List<ListEntry>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("lists")
            .get()
            .addOnSuccessListener { result ->
                val entries = result.map { doc ->
                    ListEntry(
                        title = doc.getString("title") ?: "",
                        tag = doc.getString("tag") ?: "",
                        checklistItems = (doc["checklistItems"] as? List<Map<String, Any>>)?.map {
                            ChecklistItem(
                                text = it["text"] as? String ?: "",
                                isChecked = it["isChecked"] as? Boolean ?: false
                            )
                        } ?: emptyList()
                    )
                }
                onResult(entries)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}