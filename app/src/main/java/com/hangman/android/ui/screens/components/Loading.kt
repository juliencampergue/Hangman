package com.hangman.android.ui.screens.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * The actual loading in progress composable that will display a progress bar as well as a text
 * describing what is currently being loaded.
 *
 * @param loadingText The text to display.
 * @param onBack the action to perform when a back is pressed during loading. If null, the back
 * presses won't be catched and we will leave the system or the rest of the screen handle it
 * themselves.
 */
/*
 * INFO
 * When using Composables, it is a better practice to pass used variables and values as parameters.
 * This is both for Recomposition and for tests purposes. So in our exemple, pass the loading text
 * as a parameter of our LoadingScreen Composable.
 * see https://developer.android.com/jetpack/compose/state
 */
@Composable
fun ScreenLoading(loadingText: String, onBack: (() -> Unit)? = null) {
    if (onBack != null) {
        // If a back action is performed, then catch the system back press and perform the given
        // action.
        BackHandler(onBack = onBack)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(loadingText)
    }
}
