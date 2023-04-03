package com.hangman.android.core.bouding

import com.hangman.android.bouding.IGameDetail
import com.hangman.android.bouding.IGamesHistoryList
import com.hangman.android.bouding.ISettings
import com.hangman.android.bouding.IWord
import kotlinx.coroutines.flow.StateFlow

/**
 * Describes the methods to handle the Authentication to the server.
 */
interface IAuthRepository {
    /**
     * Is user already logged in
     * @return A StateFlow indicating the current login status.
     * Value will be true if user is already logged in, false otherwise
     */
    val isLoggedIn: StateFlow<Boolean>

    /**
     * Request for the user to anonymously log in.
     * @return true if login succeeded, false otherwise
     */
    suspend fun login(): Boolean
}

/**
 * Describes the methods needed to access and handle the word of the day.
 */
interface IWordOfTheDayRepository {
    /**
     * Fetch the word of the day
     * @return The word of the day fetched.
     * @throws Error if fetch failed
     */
    suspend fun getWordOfToday(): IWord
}

/**
 * Describes the methods needed to access and handle finished games.
 */
interface IGameRepository {
    /**
     * fetch a subset of the player's already played games.
     * Note that the returned subset, though it will never be null, might not contain {@param size}
     * items, or might even be empty.
     * This might happen when reaching the end of the player's game list.
     * @param from The id of the last fetched game.
     * @param size The size of the subset we want to fetch.
     * @return an IGamesHistoryList containing the subset of IGameHistory corresponding to the
     * requested subset.
     */
    suspend fun getPlayedGames(from: Int = -1, size: Int = -1): IGamesHistoryList

    /**
     * Request the saving of a specific Game. It will make a persistant save of the content and
     * details of the game.
     * This is a persistant save of the game and should not be used to temporarily save the current
     * game, for exemple for an Activity configuration change.
     * @param game The game to save.
     * @return The id of the saved game. By default, games have an invalid ID. A valid ID is given
     * upon saving a game, and will be returned after saving for further use.
     * @throws UnavailableGameDetailError if saved game could not be found after saving
     */
    suspend fun saveGame(game: IGameDetail): Int

    /**
     * Get the game associated to a specific word if it has already been saved.
     * The returned game may be null in case the given word has no associated game (the user did not
     * play it yet) or that game hasn't been saved yet.
     * @param wordId The id of the word for which we want the associated game.
     * @return The content of the game if it has already been save, null otherwise
     */
    suspend fun getGameContent(wordId: String): IGameDetail?

    /**
     * Request the content of a specific game. The returned game will not be null as the given ID
     * should always correspond to an existing game and should always be fetchable.
     * If for some reason this was not the case, an error would be thrown.
     * @param gameId The id of the game we would like to get the content of.
     * @return The requested game content
     * @throws UnavailableGameDetailError if requested game not found
     */
    suspend fun getGameContent(gameId: Int): IGameDetail
}

/**
 * The access point for the settings.
 */
interface ISettingsRepository {
    /**
     * Get the current settings
     * @return the settings as they exist right now
     */
    suspend fun getSettings(): ISettings

    /**
     * Save the given settings
     * @param settings the settings to save.
     */
    suspend fun saveSettings(settings: ISettings)
}