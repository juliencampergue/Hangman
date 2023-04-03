package com.hangman.android.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hangman.android.ui.screens.history.HistoryAndDetailsScreen
import com.hangman.android.viewmodels.impl.GameContentScreenViewModel
import com.hangman.android.viewmodels.impl.HistoryScreenViewModel

/**
 * The actual navigation route to access the history and details destination.
 */
const val HISTORY_AND_DETAILS_SCREEN_ROUTE = "History_And_Details_Screen"

/**
 * The actual composable describing the history and details screen.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * history and details composables.
 * @param onBack This function will be called when a back navigation should be performed from the
 * history destination. ie : a HistoryScreenViewModel and a GameContentScreenViewModel
 * @param showBoth true if both history and details should be displayed on screen at the same time
 * or separately, as in any basic list/details setup.
 * @param modifier A modifier to apply to the history and game details destination.
 */
@Composable
fun HistoryAndDetailsRoute(
    viewModelProvider: ViewModelProvider,
    onBack: () -> Unit,
    showBoth: Boolean,
    modifier: Modifier = Modifier,
) {
    // We shouldn't have to remember those vals as they are retrieved from a stateful
    // class outside of Composition.
    val historyViewModel = viewModelProvider.get(HistoryScreenViewModel::class.java)
    val detailsViewModel = viewModelProvider.get(GameContentScreenViewModel::class.java)
    // Nothing fancy here, just call the history and details screen composable.
    HistoryAndDetailsScreen(
        historyViewModel,
        detailsViewModel,
        onBack,
        showBoth = showBoth,
        modifier,
    )
}

/**
 * Describes the composable that will be called and used by the NavGraphBuilder as the composable
 * entry point to call when navigating to the history (and game details) destination.
 *
 * @param viewModelProvider a ViewModelProvider able to provide necessary ViewModels for the
 * history and details composables.
 * @param onBack This function will be called when a back navigation should be performed from the
 * history destination.
 * @param showBoth true if both history and details should be displayed on screen at the same time
 * or separately, as in any basic list/details setup.
 * @param modifier A modifier to apply to the history and game details destination.
 */
fun NavGraphBuilder.historyAndDetailsScreen(
    viewModelProvider: ViewModelProvider,
    onBack: () -> Unit,
    showBoth: Boolean,
    modifier: Modifier = Modifier
) {
    composable(route = HISTORY_AND_DETAILS_SCREEN_ROUTE) {
        HistoryAndDetailsRoute(
            viewModelProvider = viewModelProvider,
            onBack = onBack,
            showBoth = showBoth,
            modifier = modifier,
        )
    }
}

/**
 * A Helper function to manually navigate to the history and game detail destination from a
 * NavController instance.
 */
fun NavController.navigateToHistoryAndDetailsScreen() {
    this.navigate(HISTORY_AND_DETAILS_SCREEN_ROUTE)
}