package com.hangman.android.core.bouding

import com.hangman.android.bouding.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Describes all the needed methods to access the data from the backend
 */
interface IBackendAdapter {
    /**
     * Is the user logged in
     */
    val isLoggedIn: StateFlow<Boolean>

    /**
     * Start the anonymous login process
     * @return true if login succeeded, false otherwise
     */
    suspend fun logIn(): Boolean

    /**
     * Fetch the word of the day
     * @return The fetched word for today, as an IWord instance.
     * @throws InvalidFetchedWordError if word was fetched but was invalid for some reason
     * @throws WordFetchingError if word could not be fetched (due, for exemple, to a timeout or any
     * other error occuring during the fetch itself).
     */
    suspend fun getWordOfToday(): IWord
}

/**
 * Describes all the needed methods that we will need to save settings to the shared preferences.
 */
interface IPreferencesAdapter {
    /**
     * Fetch the settings
     * @return The fetched settings
     */
    val settings: Flow<ISettings>

    /**
     * Save a set of settings
     * @param settings the settings to save
     */
    suspend fun saveSettings(settings: ISettings)
}

/**
 * Describes all the necessary methods to access storage to save / access player's games.
 */
interface IStorageAdapter {
    /**
     * fetch a subset of the played games
     * @param from The id of the last fetched game to only fetch next games from this point on.
     * @param size The number of games to fetch for this subset. Give -1 to have unlimited size
     * (fetch every available history item)
     */
    suspend fun getPlayedGames(from: Int = -1, size: Int=-1): IGamesHistoryList

    /**
     * Save a game's details
     * @param game the game's details to save
     */
    suspend fun saveGame(game: IGameDetail): Int

    /**
     * Fetch a game's details corresponding to a specific word
     * @param wordId The id of the word for which we are fetching the game's details
     * @return The requested game's details', or null if not found
     */
    suspend fun getGameContent(wordId: String): IGameDetail?

    /**
     * Fetch a specific game's details
     * @param id The id of the game to fetch
     * @return The requested game's details', or null if not found
     */
    suspend fun getGameContent(id: Int): IGameDetail?
}
