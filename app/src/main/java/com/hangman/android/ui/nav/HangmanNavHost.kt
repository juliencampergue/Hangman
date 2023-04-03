package com.hangman.android.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

/**
 * The NavHost that will display the top level navigation navigation destinations.
 *
 * @param navController The navController that must be associated with this navHost.
 * @param viewModelFactory a ViewModelFactory able to instantiate every needed ViewModel for
 * all the destinations that can be displayed by this NavHost.
 * @param isExpandedScreen is the screen currently considered wide or expanded, or is it considered
 * narrow or small?
 * @param modifier The modifier that will be applied to the content of this NavHost.
 * @param startDestination The destination at which to navigate first when this NavHost comes into
 * the composition.
 */
@Composable
fun HangmanNavHost(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = GAME_SCREEN_ROUTE,
) {
    // We want the ViewModels to be shareable between screens, so we will get the viewModelProvider from
    // here, and then pass it down to every route.
    // Note : We might also pass the factory as parameter and pass the storeOwner as a
    // CompositionLocalProvider as routes can also call LocalViewModelStoreOwner.current if needed.
    // But there is no need for each route to reinstantiate a ViewModelProvider when sharing one.
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)
    val viewModelProvider = ViewModelProvider(viewModelStoreOwner, viewModelFactory)

    // This composable is basically only a NavHost describing all the destinations it can display.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        splashScreen(
            viewModelProvider = viewModelProvider,
            onUserLoggedIn = navController::popBackStack,
            onQuitTrying = {System.exit(0)}
        )
        gameScreen(
            viewModelProvider = viewModelProvider,
            onNavigateToSplashScreen = navController::navigateToSplashScreen,
            onWordCantBeFetched = {System.exit(0)}
        )
        settingsScreen(
            viewModelProvider = viewModelProvider,
            onBack = navController::popBackStack
        )
        historyAndDetailsScreen(
            viewModelProvider = viewModelProvider,
            onBack = navController::popBackStack,
            showBoth = isExpandedScreen,
        )
    }
}