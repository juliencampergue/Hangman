package com.hangman.android.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hangman.android.ui.screens.splashscreen.SplashScreen
import com.hangman.android.viewmodels.SplashScreenViewModel

/**
 * The actual navigation route to access the splash screen destination.
 */
const val SPLASH_SCREEN_ROUTE = "Splash_Screen"

/**
 * The actual composable describing the splash screen.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * splash screen composables. ie : a SplashScreenViewModel.
 * @param onUserLoggedIn The function that will be called when the login process will have
 * succeeded and the user will be effectively logged in.
 * @param onQuitTrying This function will be called if the login process failed and retry is no
 * longer an option.
 * @param modifier A modifier to apply to the splash screen destination
 */
@Composable
fun SplashScreenRoute(
    viewModelProvider: ViewModelProvider,
    onUserLoggedIn: () -> Unit,
    onQuitTrying: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // We shouldn't have to remember those vals as they are retrieved from a stateful
    // class outside of Composition.
    val viewModel = viewModelProvider.get(SplashScreenViewModel::class.java)
    // Nothing fancy here, just call the splash screen composable.
    SplashScreen(viewModel, onUserLoggedIn, onQuitTrying, modifier)
}

/**
 * Describes the composable that will be called and used by the NavGraphBuilder as the composable
 * entry point to call when navigating to the splash screen destination.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * splash screen composables.
 * @param onUserLoggedIn The function that will be called when the login process will have
 * succeeded and the user will be effectively logged in.
 * @param onQuitTrying This function will be called if the login process failed and retry is no
 * longer an option.
 * @param modifier A modifier to apply to the splash screen destination
 */
fun NavGraphBuilder.splashScreen(
    viewModelProvider: ViewModelProvider,
    onUserLoggedIn: () -> Unit,
    onQuitTrying: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(SPLASH_SCREEN_ROUTE) {
        SplashScreenRoute(viewModelProvider, onUserLoggedIn, onQuitTrying, modifier)
    }
}

/**
 * A Helper function to manually navigate to the splash screen destination from a NavController instance.
 */
fun NavController.navigateToSplashScreen() {
    this.navigate(SPLASH_SCREEN_ROUTE)
}