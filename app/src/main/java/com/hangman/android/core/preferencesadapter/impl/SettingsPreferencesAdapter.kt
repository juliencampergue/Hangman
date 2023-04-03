package com.hangman.android.core.preferencesadapter.impl

import androidx.datastore.core.DataStore
import com.hangman.android.bouding.ISettings
import com.hangman.android.bouding.Settings
import com.hangman.android.core.bouding.IPreferencesAdapter
import com.hangman.android.core.preferencesadapter.items.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The actual implementation of the preferences adapter. This allow us to access the preferences
 * via the Android DataStore.
 */
class SettingsPreferencesAdapter(val datastore: DataStore<SettingsPreferences>): IPreferencesAdapter {
    override val settings: Flow<ISettings>
        get() = datastore.data.map { settingsPrefs ->
            Settings(settingsPrefs.displayTimer)
        }

    override suspend fun saveSettings(settings: ISettings) {
        datastore.updateData { settingsPrefs ->
            settingsPrefs.toBuilder()
                .setDisplayTimer(settings.displayTimer)
                .build()
        }
    }
}