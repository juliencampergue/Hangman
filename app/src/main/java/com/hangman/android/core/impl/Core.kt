package com.hangman.android.core.impl

import com.hangman.android.bouding.*
import com.hangman.android.core.bouding.*
import com.hangman.android.core.bounding.IGameEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The name of the file we will you as a storage (datastore)
 */
const val DATASTORE_FILE_NAME: String = "settings.datastore"

/**
 * The name of the database that will be created to save the user's games.
 */
const val GAMES_DATABASE_FILE_NAME: String = "hangman.db"

/**
 * The number of games that can be retrieved when fetching games history.
 */
const val PLAYED_GAME_PAGE_SIZE: Int = 100

/**
 * The implementation of the Core of this project. This is the part of the application that will
 * live throughout the whole life of the application. ie : As long as the application itself is alive,
 * without caring for the activity/viewModels/etc... lifecycles.
 *
 * @param implHandler An instance that will properly define how to instanciate all the necessary
 * components needed by the Core implementation instance.
 */
class Core(val implHandler: IImplHandler): ICore{
    private var wordOfToday: IWord? = null
    private var settings: ISettings? = null
    private var _currentGame = MutableStateFlow<IGame?>(null)
    private var _todaysGameContent = MutableStateFlow<IGameDetail?>(null)

    override val isLoggedIn: StateFlow<Boolean> = implHandler.authRepository.isLoggedIn
    override val currentGame: StateFlow<IGame?> = _currentGame.asStateFlow()
    override val todaysGameContent: StateFlow<IGameDetail?> = _todaysGameContent.asStateFlow()

    // TODO : Have an invalidation process for the word of today, for exemple, when we changed day during app life.
    override suspend fun getWordOfTheDay(): IWord {
        // If the word of today has already been fetched, just return it directly.
        if (wordOfToday == null) {
            // Use the word repository to fetch the word
            wordOfToday = implHandler.wordOfTheDayRepository.getWordOfToday()
            // Fetch the associated game content from storage if any.
            val content = implHandler.gameRepository.getGameContent(wordOfToday!!.id)
            // Update the todays game content value with the retrieved data. It can be null if no
            // such game has been saved yet.
            _todaysGameContent.value = content
            if (content == null) {
                // In case we did not save any game yet, get a playable game from game engine.
                _currentGame.value = implHandler.gameEngine.getGameForWord(wordOfToday!!)
            } // else, the word of today has already been played and no new game needs to be created.
        }
        return wordOfToday!!
    }

    override suspend fun getPlayedGames(from: Int): IGamesHistoryList {
        return implHandler.gameRepository.getPlayedGames(from = from, size = PLAYED_GAME_PAGE_SIZE)
    }

    override suspend fun saveGame(game: IGameDetail) {
        val actualGameId = implHandler.gameRepository.saveGame(game)
        _todaysGameContent.value = implHandler.gameRepository.getGameContent(actualGameId)
    }

    override suspend fun login(): Boolean {
        return implHandler.authRepository.login()
    }

    override suspend fun getGameContent(gameId: Int): IGameDetail? {
        return implHandler.gameRepository.getGameContent(gameId)
    }

    override suspend fun saveSettings(settings: ISettings) {
        implHandler.settingsRepository.saveSettings(settings)
        // Update saved settings
        this.settings = implHandler.settingsRepository.getSettings()
    }

    override suspend fun getSettings(): ISettings {
        if (settings == null) {
            settings = implHandler.settingsRepository.getSettings()
        }
        return settings!!
    }
}

/**
 * This is an "internal" representation of the components of the Core. As we want the core to
 * be as much testable as possible, we want those dependencies to be defined outside of the Core
 * implementation itself. But as it is the Core's responsability to instanciate its necessary
 * parts as well, we will have an intermediate step represented by this interface.
 */
interface IImplHandler {
    /**
     * An actual implementation of the Auth Repository
     */
    val authRepository: IAuthRepository

    /**
     * An actual implementation of the Word Repository
     */
    val wordOfTheDayRepository: IWordOfTheDayRepository

    /**
     * An actual implementation of the Settings Repository
     */
    val settingsRepository: ISettingsRepository

    /**
     * An actual implementation of the Game Repository
     */
    val gameRepository: IGameRepository

    /**
     * An actual implementation of the Game Engine
     */
    val gameEngine: IGameEngine
}
