package com.teamjg.dreamsanddoses.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.delay

/**
 * Wrapper composable that adds animated entrance/exit to any screen content
 * @param navController The NavController used to handle navigation (e.g., popping the backstack)
 * @param autoPopOnExit Whether to automatically pop the backstack after exit animation
 * @param content The screen UI content to wrap with animation
 */

@Composable
fun AnimatedScreenWrapper(
    navController: NavController,
    modifier: Modifier = Modifier,
    autoPopOnExit: Boolean = true,
    content: @Composable () -> Unit
) {
    // State to control visibility of the screen's content
    var visible by remember { mutableStateOf(true) }

    // Launches when 'visible' changes. If becoming invisible and autoPop is enabled,
    // waits for the animation to finish before popping the backstack
    LaunchedEffect(visible) {
        if (!visible && autoPopOnExit) {
            delay(300) // Matches fadeOut tween duration
            navController.popBackStack()
        }
    }

    // Wraps content in enter/exit animations
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(), // Simple fade-in
        exit = slideOutHorizontally { fullWidth -> fullWidth } + fadeOut(tween(300)),
        modifier = modifier
    ) {
        content() // The actual screen content goes here
    }
}
