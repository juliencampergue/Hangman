package com.hangman.android.viewmodels.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hangman.android.bouding.ICore
import com.hangman.android.bouding.ISettings
import com.hangman.android.bouding.ISettingsScreenViewModel
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState
import kotlinx.coroutines.launch

/**
 * The ViewModel associated with the Settings Screen.
 * It will contain all the necessary actions to load and save the Settings properly.
 *
 * @param core An ICore implementation on which to make the necessary requests.
 */
class SettingsScreenViewModel(private val core: ICore): ViewModel(), ISettingsScreenViewModel {
    private val _settingsState = MutableLiveData<DataState>(DataState.FETCHING)
    private val _saveSettingsState = MutableLiveData<RequestState>(RequestState.IDLE)
    override val settingsState: LiveData<DataState> = _settingsState
    override val saveSettingsState: LiveData<RequestState> = _saveSettingsState

    init {
        // Load settings at least once at startup
        updateSettings()
    }

    override fun saveSettings(settings: ISettings) {
        // Check if we are either fetching or saving settings before running the request.
        // If something is already running, then don't do anything.
        if (_settingsState.value == DataState.FETCHING || _saveSettingsState.value == RequestState.RUNNING) {
            return
        }

        // Then, change state to "saving"
        _saveSettingsState.value = RequestState.RUNNING

        // We will run the requests in the viewModel scope
        viewModelScope.launch {
            try {
                // Save the settings
                core.saveSettings(settings)
                // Update the settings data shared with observers
                _settingsState.value = DataState.DATA(settings)
                // Update the state
                _saveSettingsState.value = RequestState.SUCCESS
            } catch (e: Error) {
                _saveSettingsState.value = RequestState.ERROR(e)
            }
        }
    }

    override fun retry() {
        // Check if we are already fetching the settings before doing another fetch.
        if (_settingsState.value == DataState.FETCHING) {
            return
        }

        updateSettings()
    }

    /**
     * Do the actual fetch of the settings
     */
    private fun updateSettings() {
        // First things first, change loading state to FETCHING.
        // The state should be checked before calling this method to prevent calling it if a fetch
        // is already in progress.
        _settingsState.value = DataState.FETCHING

        viewModelScope.launch {
            try {
                // Update the settings data shared with observers
                _settingsState.value = DataState.DATA(core.getSettings())
            } catch (e: Error) {
                _settingsState.value = DataState.ERROR(e)
            }
        }
    }
}