package com.hangman.android.ui.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hangman.android.bounding.DataState

/**
 * Helper function to remember the [DetailScreenState] computed from the given data state.
 *
 * @param detailState The [DataState] representing the state of the game detail load.
 *
 * @return The [DetailScreenState] computed or remembered from the given data state.
 */
@Composable
fun rememberDetailScreenState(detailState: DataState?): DetailScreenState {
    return remember(detailState) {
        getDetailScreenState(detailState)
    }
}

/**
 * Describes the different possible states of the DetailScreen.
 */
sealed interface DetailScreenState {
    /**
     * The game detail are currenlty being loaded.
     */
    object LOADING: DetailScreenState

    /**
     * There is no selected game, so we need to display an empty screen.
     */
    object EMPTY: DetailScreenState

    /**
     * There was an error while fetching the game details
     *
     * @param error The actual error that occured during the fetch or the game detail.
     */
    data class ERROR(val error: Throwable): DetailScreenState

    /**
     * Game detail loading ended successfuly, and can be displayed
     *
     * @param data The actual game detail retrieved from the load.
     */
    data class DISPLAY_DETAILS<T>(val data: T?): DetailScreenState
}

/**
 * Use the loading state to compute the actual state of the details screen state.
 *
 * @param fromState The [DataState] representing the state of the game details loading process.
 *
 * @return The [DetailScreenState] computed from the given data state.
 */
private fun getDetailScreenState(fromState: DataState?): DetailScreenState {
    return when(fromState) {
        // First, check if state is null. In that case, we consider we are starting and we should
        // be loading. This should never happen really as state should be at least FETCHING...
        null -> DetailScreenState.LOADING
        // Then, check if we are on an error state.
        is DataState.ERROR -> DetailScreenState.ERROR(fromState.error)
        // We ruled out errors, let's see the rest now.
        // If we are still fetching, we will be loading.
        is DataState.FETCHING -> DetailScreenState.LOADING
        // If not available, it means there is no game selected. So we display the empty screen.
        is DataState.NOTAVAILABLE -> DetailScreenState.EMPTY
        // Else if data has been successfuly retrieved, just display the associated game detail.
        is DataState.DATA<*> -> DetailScreenState.DISPLAY_DETAILS(fromState.data)
    }
}
