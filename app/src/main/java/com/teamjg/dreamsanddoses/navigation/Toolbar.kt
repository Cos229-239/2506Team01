package com.teamjg.dreamsanddoses.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R


// Enum-like sealed class to identify each top bar variant by screen type
sealed class NavigationBarType {
    object Home : NavigationBarType()
    object Calendar : NavigationBarType()
    object Journal : NavigationBarType()
    object Pills : NavigationBarType()
    object Files : NavigationBarType()
    object Settings : NavigationBarType()
    object Dreams : NavigationBarType()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    type: NavigationBarType,                // Determines which icon to show
    navController: NavController? = null,   // NavController for back button
    useIconHeader: Boolean = false,         // Whether to show an icon/logo in the center
    onSearchClick: (() -> Unit)? = null,     // Lambda for search button click
    onArchiveClick: (() -> Unit)? = null
) {
    val color = Color.LightGray

    // Icon resource for standard vector icons
    val headerIconVector: ImageVector? = when (type) {
        is NavigationBarType.Calendar -> Icons.Default.DateRange
        is NavigationBarType.Journal -> Icons.Default.Edit
        is NavigationBarType.Settings -> Icons.Default.Settings
        is NavigationBarType.Home -> Icons.Default.Home
        else -> null
    }

    // Icon resource for custom image-based icons
    val headerIconPainter: Painter? = when (type) {
        is NavigationBarType.Dreams -> painterResource(id = R.drawable.ic_dreams_icon)
        is NavigationBarType.Files -> painterResource(id = R.drawable.ic_files_icon)
        is NavigationBarType.Pills -> painterResource(id = R.drawable.ic_prescription_dosage_assistant)
        else -> null
    }

    // Logic to conditionally show the search icon
    val showSearch = type is NavigationBarType.Settings ||
            type is NavigationBarType.Files ||
            type is NavigationBarType.Pills || type is NavigationBarType.Journal

    val showArchive = type is NavigationBarType.Files

    // Main top app bar container
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = color,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Back button positioned on the left
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navController?.let {
                    IconButton(
                        onClick = { it.popBackStack() },
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

            // Center icon, either vector or painter
            if (useIconHeader) {
                headerIconVector?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                } ?: headerIconPainter?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                }
            }

            // Search and Archive button positioned on the right
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterEnd), // keeps it pinned right
                horizontalArrangement = Arrangement.spacedBy(4.dp), // space between icons
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showArchive) {
                    IconButton(
                        onClick = onArchiveClick ?: {},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_archive_icon),
                            contentDescription = "Archive",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (showSearch && onSearchClick != null) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.size(48.dp)
                    ) {
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
fun BottomNavigationBar(navController: NavController, type: NavigationBarType) {
    // BottomAppBar provides the visual background and elevation
    BottomAppBar(
        tonalElevation = 4.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings screen button
            IconButton(onClick = {
                if (type is NavigationBarType.Settings || type is NavigationBarType.Journal) {
                    navController.navigate(Routes.HOME) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME)
                    }
                } else {
                    navController.navigate(Routes.SETTINGS) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME)
                    }
                }
            }) {
                Icon(imageVector = if (type is NavigationBarType.Settings || type is NavigationBarType.Journal) Icons.Default.Home else Icons.Default.Settings,
                    contentDescription = if (type is NavigationBarType.Settings || type is NavigationBarType.Journal) "Home" else "Settings",
                    tint = Color.Black
                )
            }

            // Pills screen button
            IconButton(onClick = {
                navController.navigate(Routes.PILLS) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prescription_dosage_assistant),
                    contentDescription = "Pills",
                    tint = Color.Black
                )
            }

            // Center action button
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable {
                        if (type is NavigationBarType.Files) {
                            // TODO: handle camera launch
                        } else {
                            // TODO: handle standard creation
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (type is NavigationBarType.Files) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera_icon),
                        contentDescription = "Open Camera",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create New",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }




            // Journal screen button
            IconButton(onClick = {
                navController.navigate(Routes.JOURNAL) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(Icons.Default.Edit,
                    contentDescription = "Journal",
                    tint = Color.Black)
            }

            // Calendar screen button
            IconButton(onClick = {
                navController.navigate(Routes.CALENDAR) {
                    launchSingleTop = true
                    popUpTo(Routes.HOME)
                }
            }) {
                Icon(Icons.Default.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.Black)
            }
        }
    }
}
