package com.teamjg.dreamsanddoses.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R
import com.teamjg.dreamsanddoses.uis.ComposePickerSheet


/** Represents the type of screen for configuring the navigation bars */
sealed class NavigationBarType {
    object Home : NavigationBarType()
    object Calendar : NavigationBarType()
    object JournalHome : NavigationBarType()
    object Journal : NavigationBarType()
    object Pills : NavigationBarType()
    object Files : NavigationBarType()
    object Settings : NavigationBarType()
    object Dreams : NavigationBarType()
    object DreamsHome : NavigationBarType()
    object Notes : NavigationBarType()
    object Canvas : NavigationBarType()
    object DreamsHistory : NavigationBarType()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    type: NavigationBarType,
    navController: NavController? = null,
    useIconHeader: Boolean = false,
    onSearchClick: (() -> Unit)? = null,
    onArchiveClick: (() -> Unit)? = null
) {
    val bgColor = Color.LightGray

    // Header icon logic based on screen type
    val headerIconVector: ImageVector? = when (type) {
        NavigationBarType.Calendar -> Icons.Default.DateRange
        NavigationBarType.JournalHome -> Icons.Default.Edit
        NavigationBarType.Settings -> Icons.Default.Settings
        NavigationBarType.Home -> Icons.Default.Home
        else -> null
    }

    val headerIconPainter: Painter? = when (type) {
        NavigationBarType.Dreams -> painterResource(R.drawable.ic_dreams_icon)
        NavigationBarType.Files -> painterResource(R.drawable.ic_files_icon)
        NavigationBarType.Pills -> painterResource(R.drawable.ic_prescription_dosage_assistant)
        NavigationBarType.Journal -> painterResource(R.drawable.ic_journal_icon)
        NavigationBarType.Canvas -> painterResource(R.drawable.ic_main_logo_icon)
        NavigationBarType.DreamsHistory -> painterResource(R.drawable.ic_dreams_icon)
        else -> null
    }

    val showSearch = type in listOf(
        NavigationBarType.Settings, NavigationBarType.Files,
        NavigationBarType.Pills, NavigationBarType.JournalHome,
        NavigationBarType.Journal, NavigationBarType.DreamsHistory
    )

    val showArchive = type == NavigationBarType.Files

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = bgColor,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Back button
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navController?.let {
                    IconButton(
                        onClick = {
                            navController.let { nav ->
                                when (type) {
                                    is NavigationBarType.Journal,
                                    is NavigationBarType.Notes -> {
                                        nav.navigate(Routes.JOURNAL_HOME) {
                                            launchSingleTop = true
                                            popUpTo(Routes.HOME) { inclusive = false }
                                        }
                                    }

                                    is NavigationBarType.DreamsHistory -> {
                                        nav.navigate(Routes.DREAMS_HOME) {
                                            launchSingleTop = true
                                            popUpTo(Routes.DREAMS) { inclusive = true }  // ensure previous "Dreams" flow is cleared
                                        }
                                    }

                                    is NavigationBarType.DreamsHome -> {
                                        nav.navigate(Routes.DREAMS) {
                                            launchSingleTop = true
                                            popUpTo(Routes.HOME) { inclusive = false }
                                        }
                                    }

                                    else -> {
                                        nav.popBackStack()
                                    }
                                }
                            }
                        },

                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Center icon
            if (useIconHeader) {
                headerIconVector?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                } ?: headerIconPainter?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                }
            }

            // Right-side action buttons
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showArchive) {
                    IconButton(onClick = onArchiveClick ?: {}, modifier = Modifier.size(48.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_archive_icon),
                            contentDescription = "Archive",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (showSearch && onSearchClick != null) {
                    IconButton(onClick = onSearchClick, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    type: NavigationBarType,
    onCompose: (() -> Unit)? = null,
    includeCenterFab: Boolean = true
) {
    var showComposePicker by remember { mutableStateOf(false) }

    if (showComposePicker) {
        ComposePickerSheet(
            onDismiss = { showComposePicker = false },
            onSelect = { selected ->
                showComposePicker = false
                when (selected) {
                    "reminder" -> navController.navigate("reminder/new")
                    "journal" -> navController.navigate(Routes.NEW_JOURNAL)
                    "notes" -> navController.navigate(Routes.NEW_NOTE)
                    "lists" -> navController.navigate(Routes.LISTS_EDITOR)
                    "canvas_editor" -> navController.navigate(Routes.CANVAS_EDITOR)
                    "dreams_home" -> navController.navigate(Routes.DREAMS_HOME)
                }
            }
        )
    }

    BottomAppBar(
        tonalElevation = 4.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Home or Settings
            IconButton(onClick = {
                val destination = if (type is NavigationBarType.Home)
                    Routes.SETTINGS else Routes.HOME
                navController.navigate(destination) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(
                    imageVector = if (type is NavigationBarType.Home) Icons.Default.Settings else Icons.Default.Home,
                    contentDescription = "Home/Settings",
                    tint = Color.Black
                )
            }

            // Pills
            IconButton(onClick = {
                navController.navigate(Routes.PILLS) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_prescription_dosage_assistant),
                    contentDescription = "Pills",
                    tint = Color.Black
                )
            }


            // Compose FAB
            if (type != NavigationBarType.Dreams) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable {
                            onCompose?.invoke() ?: run {
                                when (type) {
                                    NavigationBarType.Files -> { try {
                                        navController.navigate(Routes.SCANNER) {
                                            launchSingleTop = true
                                        }
                                    } catch (e: Exception) {
                                        println("Navigation to scanner failed: ${e.message}")
                                    }
                                    }
                                    NavigationBarType.Home,
                                    NavigationBarType.DreamsHome -> {
                                        showComposePicker = true
                                    }
                                    NavigationBarType.JournalHome,
                                    NavigationBarType.Journal,
                                    NavigationBarType.Notes -> {
                                        showComposePicker = true
                                    }
                                    else -> {}
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (type is NavigationBarType.Files) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera_icon),
                            contentDescription = "Open Camera Scanner",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Compose",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(72.dp))
            }


            // Journal
            IconButton(onClick = {
                navController.navigate(Routes.JOURNAL_HOME) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Journal", tint = Color.Black)
            }

            // Calendar
            IconButton(onClick = {
                navController.navigate(Routes.CALENDAR) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = Color.Black)
            }
        }
    }
}
