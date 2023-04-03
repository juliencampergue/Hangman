package com.hangman.android.core.repositories.impl

import com.hangman.android.bouding.ISettings
import com.hangman.android.core.bouding.IPreferencesAdapter
import com.hangman.android.core.bouding.ISettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * The repository that will handle all settings accesses
 *
 * @param preferencesAdapter The adapter to access the actual preferences mecanism.
 * @param dispatcher The coroutine dispatcher on which the preferences requests will run.
 */
class SettingsRepository(val preferencesAdapter: IPreferencesAdapter, val dispatcher: CoroutineDispatcher = Dispatchers.Default): ISettingsRepository {
    override suspend fun getSettings(): ISettings {
        // preferencesAdapter.settings is a flow, not a suspend function. So, Use flowOn(dispatcher)
        // instead of withContext(dispatcher).
        return preferencesAdapter.settings.flowOn(dispatcher).first()
    }

    override suspend fun saveSettings(settings: ISettings) {
        return withContext(dispatcher) {
            preferencesAdapter.saveSettings(settings)
        }
    }
}