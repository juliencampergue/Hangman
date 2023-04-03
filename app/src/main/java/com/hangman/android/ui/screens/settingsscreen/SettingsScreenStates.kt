package com.hangman.android.ui.screens.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState

/**
 * Helper function to remember the [SettingsScreenState] computed from the given action/data states.
 *
 * @param fetchingState The [DataState] representing the state of the settings load.
 * @param savingState The [RequestState] representing the state of the settings save.
 *
 * @return The [SettingsScreenState] computed or remembered from the given action/data states.
 */
@Composable
fun rememberSettingsScreenStates(fetchingState: DataState?, savingState: RequestState?): SettingsScreenState {
    return remember(fetchingState, savingState) {
        getSettingsScreenState(fetchingState, savingState)
    }
}

/**
 * Describe the different possible states for the Settings screen.
 * Load and save states are separated into different states because the UI might behave differently
 * when loading or saving the settings.
 */
sealed interface SettingsScreenState {
    /**
     * The Settings are currently loading.
     */
    object LOADING: SettingsScreenState

    /**
     * The settings are currently being saved.
     */
    object SAVING: SettingsScreenState

    /**
     * An error occured while loading the settings.
     *
     * @param error The actual error that occured
     */
    data class FETCHING_ERROR(val error: Throwable): SettingsScreenState

    /**
     * An error occured while saving the loading.
     *
     * @param error The actual error that occured
     */
    data class SAVING_ERROR(val error: Throwable): SettingsScreenState

    /**
     * Settings fetch finished successfuly and settings can be displayed.
     *
     * @param data The actual settings retrieved from the load.
     */
    data class DISPLAY_SETTINGS<T>(val data: T): SettingsScreenState
}

/**
 * Use the different action and data states to compute the actual settings screen state.
 *
 * @param fetchingState The [DataState] representing the state of the settings load.
 * @param savingState The [RequestState] representing the state of the settings save.
 *
 * @return The [SettingsScreenState] computed from the given action/data states.
 */
fun getSettingsScreenState(fetchingState: DataState?, savingState: RequestState?): SettingsScreenState {
    return when {
        // First, check if any state is null. In that case, we consider we are starting and we should
        // be loading. This should never happen really as all state should be at least FETCHING...
        fetchingState == null || savingState == null -> SettingsScreenState.LOADING

        // Then, check if we are on an error state. Note that, as fetched settings should never be
        // in the "NOT AVAILABLE" state, it is also considered to be an error state. Only we won't
        // have an associated error so we will build our own.
        fetchingState is DataState.ERROR -> SettingsScreenState.FETCHING_ERROR(fetchingState.error)
        savingState is RequestState.ERROR -> SettingsScreenState.SAVING_ERROR(savingState.error)
        fetchingState is DataState.NOTAVAILABLE -> SettingsScreenState.FETCHING_ERROR(HangmanError.SettingsFetchingError("settings could not be fetched"))

        // We ruled out errors, let's see the rest now.
        // If we are still fetching the settings, we will be in loading state.
        fetchingState is DataState.FETCHING -> SettingsScreenState.LOADING
        // Else, if we are saving the settings, we'll be in the saving state.
        fetchingState is DataState.DATA<*> && savingState is RequestState.RUNNING -> SettingsScreenState.SAVING

        // If ok, just display the settings
        fetchingState is DataState.DATA<*> -> SettingsScreenState.DISPLAY_SETTINGS(fetchingState.data)

        // We should not be in default case, just throw an error
        else -> SettingsScreenState.FETCHING_ERROR(HangmanError.UnknownError("We are in an unknown state, something went wrong"))
    }
}