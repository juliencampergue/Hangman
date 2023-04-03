package com.hangman.android.ui.nav

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hangman.android.ui.screens.GameScreen
import com.hangman.android.viewmodels.impl.GameScreenViewModel
import com.hangman.android.viewmodels.impl.SettingsScreenViewModel

/**
 * The actual navigation route to access the game in progress destination.
 */
const val GAME_SCREEN_ROUTE = "Game_Screen"

/**
 * The actual composable describing the game in progress screen.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * game in progress composables. ie : a GameScreenViewModel and a SettingsScreenViewModel.
 * @param onNavigateToSplashScreen a  function describing the actions to perform when the game
 * screen needs to display the splash screen.
 * @param onWordCantBeFetched a function describing the actions to perform if the word of the day
 * could not be fetched and that retry is no longer a possibility.
 * @param modifier A modifier to apply to the game in progress destination
 */
@Composable
fun GameScreenRoute(
    viewModelProvider: ViewModelProvider,
    onNavigateToSplashScreen: () -> Unit,
    onWordCantBeFetched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // We shouldn't have to remember those vals as they are retrieved from a stateful
    // class outside of Composition.
    val viewModel = viewModelProvider.get(GameScreenViewModel::class.java)
    val settingsViewModel = viewModelProvider.get(SettingsScreenViewModel::class.java)
    val loggedInState by viewModel.isLoggedIn.observeAsState()

    // The game in progress screen is the home screen of this application. As such, before
    // displaying anything, we should check if user is currently logged in to either display the
    // home screen (ie : game in progress screen) or the SplashScreen we will display while login
    // process is running.
    // We will wait for loggedInState to have an actual value before proceeding either way.
    if (loggedInState != null) {
        if (loggedInState == false) {
            /*
             * If we are not currently logged in, then we need to navigate to the splash screen.
             * Call the given function and let the caller handle the navigation part.
             *
             * INFO
             * Navigation functions need to be called from callbacks/coroutines to
             * prevent infinite recomposition.
             * In this case, we use a LaunchedEffect bound to the current Composable
             * Lifecycle (passing Unit as key). The call is made in another coroutine
             * when needed as long as the LaunchedEffect is in the composition.
             * if the LaunchedEffect leaves the composition (when we are displaying the splash
             * screen or when login is working for exemple), then existing coroutines will be
             * cancelled and no new coroutine should be ran.
             */
            LaunchedEffect(Unit) {
                onNavigateToSplashScreen()
            }
        } else {
            // If currently logged in, then just display the home screen.
            GameScreen(viewModel, settingsViewModel, onWordCantBeFetched, modifier)
        }
    }
}

/**
 * Describes the composable that will be called and used by the NavGraphBuilder as the composable
 * entry point to call when navigating to the game in progress destination.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * game in progress composables.
 * @param onNavigateToSplashScreen a function describing the actions to perform when the game
 * screen needs to display the splash screen.
 * @param onWordCantBeFetched a function describing the actions to perform if the word of the day
 * could not be fetched and that retry is no longer a possibility.
 * @param modifier A modifier to apply to the game in progress destination
 */
fun NavGraphBuilder.gameScreen(
    viewModelProvider: ViewModelProvider,
    onNavigateToSplashScreen: () -> Unit,
    onWordCantBeFetched: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = GAME_SCREEN_ROUTE) {
        GameScreenRoute(viewModelProvider, onNavigateToSplashScreen, onWordCantBeFetched, modifier)
    }
}

/**
 * A Helper function to manually navigate to the game in progress destination from a NavController
 * instance.
 */
fun NavController.navigateToGameScreen() {
    this.navigate(GAME_SCREEN_ROUTE)
}
