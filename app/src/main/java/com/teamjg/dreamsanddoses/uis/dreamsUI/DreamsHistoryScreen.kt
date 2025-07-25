package com.teamjg.dreamsanddoses.uis.dreamsUI

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.teamjg.dreamsanddoses.navigation.NavigationBarType
import com.teamjg.dreamsanddoses.navigation.TopNavigationBar
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.navigation.BottomNavigationBar
import com.teamjg.dreamsanddoses.navigation.Routes
import com.teamjg.dreamsanddoses.uis.FirestoreService
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Data class representing a dream entry.
 * @param title Title of the dream.
 * @param date String representing the date the dream was created or recorded.
 * @param description Optional description or content of the dream.
 */
data class DreamEntry(
    val title: String,
    val date: String,
    val description: String = ""
)

/**
 * Main Dreams screen composable showing a list of saved dreams.
 * Displays a top app bar, bottom navigation, and a floating "new dream" button.
 * Loads the user's dreams from Firestore and displays them in a LazyColumn.
 *
 * @param navController Navigation controller to handle screen transitions.
 * @param viewModel ViewModel holding the dreams state and data loading logic.
 */
@Composable
fun DreamsScreen(navController: NavController, viewModel: DreamsViewModel = viewModel()) {
    // Access the current list of dreams from the ViewModel's state
    val dreams = viewModel.dreams

    // Get current logged-in user's UID (null if not signed in)
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // On userId change or initial composition, load dreams for that user
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadDreams(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // Top bar showing Dreams History navigation
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.DreamsHistory,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* TODO: implement search */ }
                )
            },
            // Bottom bar navigation with Dreams tab selected
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    type = NavigationBarType.Dreams,
                    onCompose = { navController.navigate(Routes.DREAMS) } // Navigate to new dream creation
                )
            },
            containerColor = Color.LightGray // Background color for the scaffold
        ) { innerPadding ->
            // LazyColumn for displaying list of dreams with padding and spacing
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Padding for system bars and scaffold content
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Inner padding for content edges
            ) {
                // For each dream in the list, show a DreamCard and add vertical spacing
                items(dreams) { dream ->
                    DreamCard(dream)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Floating Compose icon button manually overlaid above bottom navigation
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp) // Large tap target size
                .align(Alignment.BottomCenter) // Positioned at bottom center of the screen
                .offset(y = (-32).dp) // Slightly overlaps the bottom navigation bar
                .clickable {
                    // Navigate to the Dreams template screen for creating a new dream
                    navController.navigate(Routes.DREAMS_TEMPLATE)
                },
            tint = Color.Unspecified // Preserve the icon's original colors
        )
    }
}

/**
 * Composable that displays a card with dream details.
 * Shows title, date, and optionally the description if available.
 *
 * @param dream DreamEntry instance containing data to display.
 */
@Composable
fun DreamCard(dream: DreamEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // Make card take full horizontal width
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)), // Drop shadow for elevation
        colors = CardDefaults.cardColors(containerColor = Color.White), // White card background
        shape = RoundedCornerShape(12.dp) // Rounded corners for card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title text styled with Material theme typography
            Text(dream.title, style = MaterialTheme.typography.titleMedium)

            // Date text styled smaller and gray to denote metadata
            Text(dream.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            // If description is present, add spacing and show description text limited to 3 lines
            if (dream.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    dream.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )
            }
        }
    }
}

/**
 * ViewModel to manage dreams state and Firestore data loading.
 * Holds a mutable list of DreamEntry objects observed by Compose.
 */
class DreamsViewModel : ViewModel() {
    // Backing mutable list of dreams wrapped in a Compose state list
    private val _dreams = mutableStateListOf<DreamEntry>()

    // Public read-only access to dreams list
    val dreams: List<DreamEntry> get() = _dreams

    /**
     * Loads dream entries from Firestore for the given user ID.
     * Clears previous dreams and fetches documents from "users/{userId}/dreams" collection.
     * Parses fields and converts Firestore Timestamp to formatted date string.
     *
     * @param userId Firebase UID of the current user.
     */
    fun loadDreams(userId: String) {
        FirestoreService.db.collection("users").document(userId)
            .collection("dreams")
            .get()
            .addOnSuccessListener { result ->
                _dreams.clear()
                for (document in result.documents) {
                    val data = document.data ?: continue
                    // Extract fields safely with default fallbacks
                    val title = data["title"] as? String ?: "Untitled"
                    val description = data["content"] as? String ?: ""

                    // Format the Firestore Timestamp to a human-readable date string
                    val date = data["createdAt"]?.let { ts ->
                        if (ts is Timestamp)
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(ts.toDate())
                        else "Unknown Date"
                    } ?: "Unknown Date"

                    // Add parsed dream entry to the state list
                    _dreams.add(DreamEntry(title, date, description))
                }
            }
            .addOnFailureListener { e ->
                // Log errors for troubleshooting Firestore fetch failures
                Log.e("DreamsViewModel", "Failed to fetch dream entries", e)
            }
    }
}
