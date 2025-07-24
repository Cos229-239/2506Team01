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


data class DreamEntry(
    val title: String,
    val date: String,
    val description: String = ""
)


// Dreams screen implementation using the back navigation wrapper
@Composable
fun DreamsScreen(navController: NavController, viewModel: DreamsViewModel = viewModel()) {
    val dreams = viewModel.dreams
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadDreams(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    type = NavigationBarType.DreamsHistory,
                    navController = navController,
                    useIconHeader = true,
                    onSearchClick = { /* TODO */ }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    type = NavigationBarType.Dreams,
                    onCompose = { navController.navigate(Routes.DREAMS) }
                )
            },
            containerColor = Color.LightGray
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(dreams) { dream ->
                    DreamCard(dream)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Custom Compose Button manually overlaid
        Icon(
            painter = painterResource(R.drawable.dreams_compose_icon),
            contentDescription = "New Dream",
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-32).dp) // This cuts into the BottomNavigationBar
                .clickable {
                    navController.navigate(Routes.DREAMS_TEMPLATE)
                },
            tint = Color.Unspecified // preserves original icon coloring
        )
    }
}



@Composable
fun DreamCard(dream: DreamEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(dream.title, style = MaterialTheme.typography.titleMedium)
            Text(dream.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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

class DreamsViewModel : ViewModel() {
    private val _dreams = mutableStateListOf<DreamEntry>()
    val dreams: List<DreamEntry> get() = _dreams

    fun loadDreams(userId: String) {
        FirestoreService.db.collection("users").document(userId)
            .collection("dreams")
            .get()
            .addOnSuccessListener { result ->
                _dreams.clear()
                for (document in result.documents) {
                    val data = document.data ?: continue
                    val title = data["title"] as? String ?: "Untitled"
                    val description = data["content"] as? String ?: ""
                    val date = data["createdAt"]?.let { ts ->
                        if (ts is Timestamp)
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(ts.toDate())
                        else "Unknown Date"
                    } ?: "Unknown Date"

                    _dreams.add(DreamEntry(title, date, description))
                }
            }
            .addOnFailureListener { e ->
                Log.e("DreamsViewModel", "Failed to fetch dream entries", e)
            }
    }
}

