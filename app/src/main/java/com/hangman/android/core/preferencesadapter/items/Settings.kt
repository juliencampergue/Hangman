package com.hangman.android.core.preferencesadapter.items

import com.hangman.android.bouding.ISettings

/**
 * The preferences implementation of the ISettings model.
 */
class Settings(val _displayTimer: Boolean): ISettings {
    override val displayTimer: Boolean
        get() = _displayTimer
}