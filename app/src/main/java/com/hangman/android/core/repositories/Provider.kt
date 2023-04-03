package com.hangman.android.core.repositories

import com.hangman.android.core.repositories.impl.AuthRepository
import com.hangman.android.core.repositories.impl.GameRepository
import com.hangman.android.core.repositories.impl.SettingsRepository
import com.hangman.android.core.repositories.impl.WordOfTheDayRepository

/**
 * The provider for the different repositories. This class is responsible for properly
 * instanciating the repositories for this package.
 *
 * @param repositoryDependencies The interface describing the dependencies needed to instanciate
 * the instances provided by this package.
 */
class RepositoryProvider(repositoryDependencies: IRepositoryDependencies) {
    /**
     * The repository that will handle authentification on the server.
     */
    val authRepository = AuthRepository(repositoryDependencies.backendAdapter)

    /**
     * The repository that will handle all word related operations (mainly fetch)
     */
    val wordOfTheDayRepository = WordOfTheDayRepository(repositoryDependencies.backendAdapter)

    /**
     * The repository that will handle the games (Creations, saves, fetches, etc...)
     */
    val gameRepository = GameRepository(repositoryDependencies.storageAdapter)

    /**
     * The repository to use to access the settings.
     */
    val settingsRepository = SettingsRepository(repositoryDependencies.preferencesAdapter)
}