package com.hangman.android.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hangman.android.ui.screens.settingsscreen.SettingsScreen
import com.hangman.android.viewmodels.impl.SettingsScreenViewModel

/**
 * The actual navigation route to access the settings destination.
 */
const val SETTINGS_SCREEN_ROUTE = "Settings_Screen"

/**
 * The actual composable describing the settings screen.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * splash screen composables.
 * @param onBack This function will be called when a back navigation should be performed from the
 * settings destination.
 * @param modifier A modifier to apply to the settings destination
 */
@Composable
fun SettingsScreenRoute(
    viewModelProvider: ViewModelProvider,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
){
    // We shouldn't have to remember those vals as they are retrieved from a stateful
    // class outside of Composition.
    val viewModel = viewModelProvider.get(SettingsScreenViewModel::class.java)
    // Nothing fancy here, just call the settings screen composable.
    SettingsScreen(viewModel, onBack, modifier)
}

/**
 * Describes the composable that will be called and used by the NavGraphBuilder as the composable
 * entry point to call when navigating to the settings destination.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * splash screen composables.
 * @param onBack This function will be called when a back navigation should be performed from the
 * settings destination.
 * @param modifier A modifier to apply to the settings destination
 */
fun NavGraphBuilder.settingsScreen(
    viewModelProvider: ViewModelProvider,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(route = SETTINGS_SCREEN_ROUTE) {
        SettingsScreenRoute(viewModelProvider, onBack, modifier)
    }
}

/**
 * A Helper function to manually navigate to the settings destination from a NavController instance.
 */
fun NavController.navigateToSettingsScreen() {
    this.navigate(SETTINGS_SCREEN_ROUTE)
}
