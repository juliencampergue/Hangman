package com.hangman.android.ui

import androidx.lifecycle.ViewModelProvider

/**
 * Describe the dependencies needed by the UI to be able to build the requested UI elements.
 */
interface IUiDependencies{
    /**
     * Get an actual implementation of the ViewModel Factory, depending on the currently selected
     * game
     *
     * @param gameId The ID of the currently selected game
     */
    fun getFactoryFor(gameId: Int = -1): ViewModelProvider.Factory
}