package com.hangman.android.core.preferencesadapter.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.hangman.android.core.bouding.IPreferencesAdapter
import com.hangman.android.core.preferencesadapter.impl.SettingsPreferencesAdapter
import com.hangman.android.core.preferencesadapter.items.SettingsPreferences
import com.hangman.android.core.preferencesadapter.items.SettingsPreferencesSerializer

/**
 * The Provider for the preferences adapter. This class is responsible for properly instantiating
 * the preferences adapter provided by this package.
 * @param dependencies The interface describing the needed dependencies necessary to instanciate
 * the instances provided by this package.
 */
class PreferencesProvider(val dependencies: IPreferencesAdapterDependencies) {
    /**
     * The Datastore that will be used to store the user's preferences.
     */
    private val dataStore: DataStore<SettingsPreferences> = DataStoreFactory.create(
        serializer = SettingsPreferencesSerializer(),
        corruptionHandler = null,
        migrations = listOf(),
        scope = dependencies.preferencesScope,
        produceFile = {dependencies.applicationContext.dataStoreFile(dependencies.dataStoreFileName)}
    )

    /**
     * The actual preference adapter provided by this package.
     */
    val preferencesAdapter: IPreferencesAdapter = SettingsPreferencesAdapter(dataStore)
}