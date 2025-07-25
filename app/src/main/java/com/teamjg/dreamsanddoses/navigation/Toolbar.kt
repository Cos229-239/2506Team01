package com.teamjg.dreamsanddoses.navigation

// UI framework imports
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.teamjg.dreamsanddoses.R

/**
 * Enum-style sealed class to define and distinguish the screen type
 * Used to tailor navigation bars to the context of the current screen
 */
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
    object DreamsTemplate : NavigationBarType()
}

/**
 * Top app bar component that displays a back button, header icon, and optional action buttons.
 *
 * @param type Specifies which screen type this bar is for (affects icons and actions)
 * @param navController Optional NavController to enable back navigation
 * @param useIconHeader If true, shows an icon in the center instead of a title
 * @param onSearchClick Optional handler for search icon clicks
 * @param onArchiveClick Optional handler for archive icon clicks (only used on Files screen)
 */
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

    // Select icon from built-in vectors for center header
    val headerIconVector: ImageVector? = when (type) {
        NavigationBarType.Calendar -> Icons.Default.DateRange
        NavigationBarType.JournalHome -> Icons.Default.Edit
        NavigationBarType.Settings -> Icons.Default.Settings
        NavigationBarType.Home -> Icons.Default.Home
        else -> null
    }

    // Select icon from local drawable resources
    val headerIconPainter: Painter? = when (type) {
        NavigationBarType.Dreams -> painterResource(R.drawable.ic_dreams_icon)
        NavigationBarType.Files -> painterResource(R.drawable.ic_files_icon)
        NavigationBarType.Pills -> painterResource(R.drawable.ic_prescription_dosage_assistant)
        NavigationBarType.Journal -> painterResource(R.drawable.ic_journal_icon)
        NavigationBarType.Canvas -> painterResource(R.drawable.ic_main_logo_icon)
        NavigationBarType.DreamsHistory -> painterResource(R.drawable.ic_dreams_icon)
        NavigationBarType.DreamsTemplate -> painterResource(R.drawable.ic_dreams_icon)
        else -> null
    }

    // Determine which actions should be shown
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
            // Left: Back button
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                navController?.let {
                    IconButton(
                        onClick = {
                            // Custom back navigation per screen
                            when (type) {
                                NavigationBarType.Journal, NavigationBarType.Notes -> {
                                    navController.navigate(Routes.JOURNAL_HOME) {
                                        launchSingleTop = true
                                        popUpTo(Routes.HOME)
                                    }
                                }
                                NavigationBarType.DreamsHistory -> {
                                    navController.navigate(Routes.DREAMS_HOME) {
                                        launchSingleTop = true
                                        popUpTo(Routes.DREAMS) { inclusive = true }
                                    }
                                }
                                NavigationBarType.DreamsHome -> {
                                    navController.navigate(Routes.DREAMS) {
                                        launchSingleTop = true
                                        popUpTo(Routes.HOME)
                                    }
                                }
                                NavigationBarType.DreamsTemplate -> {
                                    navController.navigate(Routes.DREAMS_HOME) {
                                        launchSingleTop = true
                                        popUpTo(Routes.DREAMS_HOME) { inclusive = true }
                                    }
                                }
                                else -> navController.popBackStack()
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

            // Center: Icon header
            if (useIconHeader) {
                headerIconVector?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                } ?: headerIconPainter?.let {
                    Icon(it, contentDescription = "Header Icon", modifier = Modifier.size(75.dp))
                }
            }

            // Right: Archive / Search icons
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

/**
 * Bottom navigation bar with center FAB (or Scanner shortcut) and contextual tabs.
 *
 * @param navController Navigation controller to drive destination changes
 * @param type Current screen type (affects icons and compose logic)
 * @param onCompose Optional override for compose button logic
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    type: NavigationBarType,
    onCompose: (() -> Unit)? = null
) {
    val bgColor = Color.LightGray
    var showComposePicker by remember { mutableStateOf(false) }

    // Display the compose picker modal when triggered
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
                    "dreams" -> navController.navigate(Routes.DREAMS_TEMPLATE)
                }
            }
        )
    }

    Column {
        // Top shadow gradient separator above the nav bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.1f), Color.LightGray)
                    )
                )
        )

        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = bgColor,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Toggle between Home and Settings
                IconButton(onClick = {
                    val destination = if (type is NavigationBarType.Home)
                        Routes.SETTINGS else Routes.HOME
                    navController.navigate(destination) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME)
                    }
                }) {
                    Icon(
                        imageVector = if (type is NavigationBarType.Home)
                            Icons.Default.Settings else Icons.Default.Home,
                        contentDescription = "Home/Settings",
                        tint = Color.Black
                    )
                }

                // Pills Tab
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

                // Center: Compose FAB (Scanner or Picker)
                if (type != NavigationBarType.Dreams) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                onCompose?.invoke() ?: run {
                                    when (type) {
                                        NavigationBarType.Files -> {
                                            try {
                                                navController.navigate(Routes.SCANNER) {
                                                    launchSingleTop = true
                                                }
                                            } catch (e: Exception) {
                                                println("Navigation to scanner failed: ${e.message}")
                                            }
                                        }

                                        NavigationBarType.Home,
                                        NavigationBarType.DreamsHome,
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
                    // Spacer if Dreams screen disables the FAB
                    Box(modifier = Modifier.size(72.dp))
                }

                // Journal Tab
                IconButton(onClick = {
                    navController.navigate(Routes.JOURNAL_HOME) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME)
                    }
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Journal", tint = Color.Black)
                }

                // Calendar Tab
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
}
