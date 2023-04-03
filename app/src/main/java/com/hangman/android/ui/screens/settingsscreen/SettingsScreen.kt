package com.hangman.android.ui.screens.settingsscreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hangman.android.ui.screens.components.ScreenLoading
import com.hangman.android.R
import com.hangman.android.bouding.*
import com.hangman.android.ui.previewutils.DayNightModePreviews
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import com.hangman.android.ui.screens.components.ErrorDialog
import com.hangman.android.ui.screens.components.Screens
import com.hangman.android.ui.screens.components.rememberErrorDialogState
import com.hangman.android.ui.theme.*

/**
 * The composable that will be used to display the actual settings screen.
 *
 * @param viewModel The [ISettingsScreenViewModel] used by this screen to get all necessary data and
 * do all necessary actions.
 * @param onBack The action to perform when user wants to get back (using the "cancel" button for
 * instance).
 * @param modifier The modifier to apply to the Settings screen.
 */
@Composable
fun SettingsScreen(
    viewModel: ISettingsScreenViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use ViewModel states to decide of what we should display. Either a loading screen, or an
    // error, or the retrieved settings if possible.
    val fetchingState by viewModel.settingsState.observeAsState()
    val savingState by viewModel.saveSettingsState.observeAsState()
    val settingsScreenState = rememberSettingsScreenStates(
        fetchingState = fetchingState,
        savingState = savingState
    )

    // Extract settings from settingsScreenState if possible, else use empty settings, it means we
    // are in loading/error state.
    // Remembering the settingsState allows us to keep up to date settings.
    // Using retrieved data allows us to display accurate initial data.
    val data = if (settingsScreenState is SettingsScreenState.DISPLAY_SETTINGS<*>) settingsScreenState.data as ISettings else null

    // The settings will only be saved when the user clicks on the "save" button. But, even though
    // the states are remembered, the current settings, containing the eventual modifications made
    // by the user and not yet saved, are not remembered. They should be remembered and even more so
    // rememberSaveable to be able to be remember even after a configuration change (screen
    // rotation for instance). For that, we create this Saver runnable that will describe how to
    // save and restore the current settings.
    val settingsStateSaver = run {
        val settingsDisplayTimerKey = "settingsPlayedTime"
        // We need two things in the SettingsState. The actual data to keep, and to way to save them
        // permanently. As the method to save permanently is a viewModel method, we won't save it
        // in the mapSaver. First it cannot, and second it wouldn't make any sense as we have no way
        // of making sure that the new viewModel is the same as the old one (even though it should).
        // In addition to that, as the mapSaver restore function builds a SettingsState directly and
        // do not use the build set in the remember function, we need to be able to build a full
        // SettingsState at that point. So we need the saveSettings method here anyway.
        mapSaver(
            save = {mapOf(settingsDisplayTimerKey to it.displayTimer)},
            restore = {SettingsState(Settings(it[settingsDisplayTimerKey] as Boolean), viewModel::saveSettings)}
        )
    }
    // Create or remember the actual SettingsState
    val settingsState: SettingsState = rememberSaveable(data, saver = settingsStateSaver) {
        SettingsState(data, viewModel::saveSettings)
    }

    // We don't need a full scafold here
    Column(modifier = Modifier.fillMaxSize()) {
        // The settings title bar will will contain the "cancel" and "save" buttons. It will always
        // be displayed
        SettingsScreenTitleBar(
            settingsState = settingsState,
            settingsScreenState = settingsScreenState,
            onCancel = onBack
        )

        // Depending on the current screen state, we will do different things.
        when (settingsScreenState) {
            is SettingsScreenState.LOADING -> {
                // If currently fetching the settings, just display a loading screen.
                ScreenLoading(
                    loadingText = stringResource(id = R.string.settings_screen_loading),
                    onBack = onBack,
                )
            }
            is SettingsScreenState.SAVING -> {
                // If currently saving the settings, also display a loading screen, but do not allow
                // a back press to leave the screen just yet.
                ScreenLoading(loadingText = stringResource(id = R.string.settings_screen_saving))
            }
            is SettingsScreenState.FETCHING_ERROR -> {
                // In case of a loading error, display a popup error. If user quit retrying, then
                // we will leave the settings screen.
                ErrorDialog(
                    onConfirm = viewModel::retry,
                    onDismiss = onBack,
                    state = rememberErrorDialogState(
                        Screens.SETTINGS,
                        settingsScreenState.error
                    )
                )
            }
            is SettingsScreenState.SAVING_ERROR -> {
                // In case of a saving error, we notify the user. But we won't leave the screen if
                // the user dismisses the popup. He has been notify, he could retry on his own later
                // or whatever, we can still display the settings and perform the eventual actions
                // so no need to leave.
                ErrorDialog(
                    onConfirm = settingsState::save,
                    onDismiss = {},
                    state = rememberErrorDialogState(
                        Screens.SETTINGS,
                        settingsScreenState.error
                    )
                )
            }
            is SettingsScreenState.DISPLAY_SETTINGS<*> -> {
                // Finally, if everything went well, display the loaded settings.
                SettingsScreenContent(settingsState = settingsState)
            }
        }
    }
}

/**
 * The actual title bar composable for the settings screen.
 *
 * @param settingsState The current [SettingsState] describing the user modifications that have not
 * been saved yet.
 * @param settingsScreenState The current [SettingsScreenState] to be able to react to it accordingly.
 * @param onCancel The action to perform when the user presses the "cancel" button.
 */
@Composable
private fun SettingsScreenTitleBar(
    settingsState: SettingsState,
    settingsScreenState: SettingsScreenState,
    onCancel: () -> Unit
) {
    HangmanTopBar() {
        // Use a box to allow alignments.
        Box(modifier = Modifier.fillMaxWidth()) {
            // The cancel button. Always enabled.
            HangmanTextButton(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.CenterStart),
                text = {
                    Text(
                        text = stringResource(R.string.settings_screen_cancel)
                    )
                }
            )
            // The save button. Only enabled when we are displaying the settings. ie : We are not
            // on an error state, and we are neither fetching nor saving the settings.
            HangmanTextButton(
                onClick = settingsState::save,
                enabled = settingsScreenState is SettingsScreenState.DISPLAY_SETTINGS<*>,
                modifier = Modifier.align(Alignment.CenterEnd),
                text = {
                    Text(
                        text = stringResource(id = R.string.settings_screen_save)
                    )
                }
            )
        }
    }
}

/**
 * The composable describing the content of the settings screen. ie : The settings themselves.
 *
 * @param settingsState The actual [SettingsState] containing the current settings along with the
 * eventual changes that the user made that were not saved yet.
 */
@Composable
private fun SettingsScreenContent(settingsState: SettingsState) {
    // This is a fixed, non dynamic and small list. A column is enough until such time that the
    // settings are plenty
    Column(modifier = Modifier.fillMaxSize()) {
        // Each setting will be displayed by a HangmanSettingsItem.
        HangmanSettingsItem(
            modifier = Modifier.fillMaxWidth(),
            // When item is clicked, update the settingsState with the new value. This should
            // trigger a recomposition.
            onClick = {settingsState.updateDisplayTimer(!settingsState.displayTimer)},
            // The switch will act as the item. When clicked, it will update the settingsState with
            // the new value which will trigger a recomposition.
            switch = {
                Switch(
                    checked = settingsState.displayTimer,
                    onCheckedChange = settingsState::updateDisplayTimer
                )
            },
            // Displayed text will describe the current switch/setting value, and should change
            // base on the current setting value.
            text = {
                val textId = remember(settingsState.displayTimer){
                    if (settingsState.displayTimer) {
                        R.string.settings_playtime_active
                    } else {
                        R.string.settings_playtime_inactive
                    }
                }
                Text(text = stringResource(id = textId))
            },
            // The subtext will describe the settings itself and will not change when value changes.
            subText = {
                Text(text = stringResource(id = R.string.settings_playtime_desc))
            }
        )
    }
}

/**
 * Stores the current values for every settings that can be displayed to, or modified by the user.
 * This will be the main tool used by the settings screen to interact with the settings themselves.
 * If a setting has changed but has not been saved yet, the current value will be stored in here
 * until the save (or cancellation) is made.
 *
 * @param initialSettings The settings as they are saved in the permanent settings storage.
 * @param onSave The method to call when user wants to save the modified settings to permanent
 * storage.
 */
private class SettingsState(
    initialSettings: ISettings?,
    private val onSave: (ISettings) -> Unit,
): ISettings {
    // Each setting will be a mutableStateOf in order to trigger recomposition whenever the are
    // modified.
    private var _displayTimer by mutableStateOf( initialSettings?.displayTimer ?: false)

    override val displayTimer: Boolean
        get() = _displayTimer

    /**
     * Update the "display timer" setting value.
     * Note that, even though the new value will be displayed to the user, it is not yet saved
     * in permanent storage and will be lost if user leaves or cancels before saving.
     *
     * @param newValue the new value of this setting.
     */
    fun updateDisplayTimer(newValue: Boolean) {
        _displayTimer = newValue
    }

    /**
     * Call this method to actually save the settings as they are now to permanent storage.
     * As long as this method is not called, any modification made to the settings will be lost if
     * user leaves or cancels.
     */
    fun save() {
        onSave(this)
    }
}

/**
 * The composable representing an actual setting to be displayed.
 *
 * @param modifier the modifier to apply to this item.
 * @param onClick the action to perform when user clicks on this item.
 * @param switch the actual composable that will be used as the switch. Can be any composable and,
 * if not clickable, the item itself will handle clicks.
 * @param text the text to display that will describe the current setting state.
 * @param subText the text to display that will describe what this setting is for. Optional
 */
@Composable
fun HangmanSettingsItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    switch: @Composable () -> Unit,
    text: @Composable () -> Unit,
    subText: (@Composable () -> Unit)? = null,
) {
    // The item's behavior is common and is described in the HangmanFixedListItem.
    HangmanFixedListItem(
        onClick = onClick,
        modifier = modifier,
    ) {
        // The switch will be displayed at the begining of the item
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(end = HangmanListItems.horizontalSpacing)) {
                switch()
            }
            // Then, the text and subtext will be stacked up vertically and displayed alongside the
            // switch
            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                ApplyHangmanItemTitleStyle(text = text)
                if (subText != null) {
                    ApplyHangmanItemSubtitleStyle(text = subText)
                }
            }
        }
    }
}

//region Previews
/*
 * --------
 * Previews
 * --------
 */
@DayNightModeWithBackgroundPreviews
@Composable
fun previewHangmanSettingsItem() {
    HangmanTheme() {
        HangmanSettingsItem(
            onClick = {},
            switch = { Switch(checked = true, onCheckedChange = {}) },
            text = { Text("title") },
            subText = { Text("subtitle") }
        )
    }
}

@DayNightModePreviews
@Composable
fun previewSettingsSuccess() {
    HangmanTheme() {
        SettingsScreenContent(
            SettingsState(
                Settings(true),
                onSave = {},
            )
        )
    }
}
//endregion Previews