package com.hangman.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.hangman.android.ui.nav.HangmanBottomBar
import com.hangman.android.ui.nav.HangmanNavHost
import com.hangman.android.ui.theme.HangmanTheme

/**
 * The Single Activity that will handle all the user interactions.
 */
class StartActivity: ComponentActivity() {
    /**
     * Optin to the window size class API to access the width size class. (wide screen, etc...)
     */
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the compose content
        setContent {
            // Use custom theme
            HangmanTheme() {
                // Compose entry point, the HangmanApp will display all necessary elements depending
                // on the current state.
                HangmanApp(
                    viewModelFactory = (application as IUiDependencies).getFactoryFor(-1),
                    widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                )
            }
        }
    }
}

/**
 * The main compose entry point. It describes all the screens, navigations, etc... But not the Theme,
 * so a custom theme might be applied before calling this function.
 *
 * @param viewModelFactory The factory used to instantiate the needed ViewModels
 * @param widthSizeClass The current width size class, describing how we are currently displayed
 * in order to maybe display some things differently.
 */
@Composable
fun HangmanApp(
    viewModelFactory: ViewModelProvider.Factory,
    widthSizeClass: WindowWidthSizeClass
) {
    val navigationController = rememberNavController()
    val scaffoldState = rememberScaffoldState()

    // The first level scaffold. It will hold the navigation buttons into the bottom bar in order
    // for them to be accessible from anywhere into the application.
    // The content itself will simply be the associated NavHost.
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {HangmanBottomBar(navigationController)},
    ) { innerPadding ->
        HangmanNavHost(
            navController = navigationController,
            viewModelFactory = viewModelFactory,
            isExpandedScreen = widthSizeClass != WindowWidthSizeClass.Compact,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
