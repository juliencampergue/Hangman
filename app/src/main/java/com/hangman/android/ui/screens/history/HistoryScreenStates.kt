package com.hangman.android.ui.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bounding.DataState

/**
 * Helper function to remember the [HistoryScreenState] computed from the given data state.
 *
 * @param state The [DataState] representing the state of the history load.
 *
 * @return The [HistoryScreenState] computed or remembered from the given action/data states.
 */
@Composable
fun rememberHistoryScreenState(state: DataState?): HistoryScreenState {
    // No need to rememberSavable. Screen State is "read only" and created from
    // a state presumably coming from a viewModel (anyway, it should).
    return remember(state) {
        getHistoryScreenState(state)
    }
}

/**
 * Describes the different possible states of the HistoryScreen.
 */
sealed interface HistoryScreenState {
    /**
     * The history is currently being loaded.
     */
    object LOADING: HistoryScreenState

    /**
     * There was an error while fetching the played games history
     *
     * @param error The actual error that occured during the fetch of the history
     */
    data class ERROR(val error: Throwable): HistoryScreenState

    /**
     * History loading ended successfuly and history list can be displayed.
     *
     * @param data the actual history items retrieved from the load.
     */
    data class DISPLAY_HISTORY<T>(val data: T): HistoryScreenState
}

/**
 * Use the load data state to compute the actual history screen state.
 *
 * @param fromState The [DataState] representing the state of the history items load.
 *
 * @return The [HistoryScreenState] computed from the given data state.
 */
private fun getHistoryScreenState(fromState: DataState?): HistoryScreenState {
    return when(fromState) {
        // First, check if state is null. In that case, we consider we are starting and we should
        // be loading. This should never happen really as state should be at least FETCHING...
        null -> HistoryScreenState.LOADING
        // Then, check if we are on an error state. As history items should never be "NOT AVAILABLE",
        // we act as if it were an error as well in that case.
        is DataState.ERROR -> HistoryScreenState.ERROR(fromState.error)
        is DataState.NOTAVAILABLE -> HistoryScreenState.ERROR(HangmanError.HistoryFetchingError("Could not fetch games history"))
        // We ruled out errors, let's see the rest now.
        // If we are still fetching, we will be loading.
        is DataState.FETCHING -> HistoryScreenState.LOADING
        // If ok, just display the retrieved history
        is DataState.DATA<*> -> HistoryScreenState.DISPLAY_HISTORY(fromState.data)
    }
}