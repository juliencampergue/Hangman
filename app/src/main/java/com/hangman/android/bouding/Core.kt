package com.hangman.android.bouding

import kotlinx.coroutines.flow.StateFlow

/**
 * The Core will be the part of the Application that will live throughout the whole application's
 * life. ie : As long as the Application class lives, so does the Core.
 * It will contain and handle all the components that needs not follow any of the multiple different
 * Android lifecycles, such as the repositories, the Game engine or the external Adaptors.
 */
interface ICore {
    // --- States ---

    /**
     * A StateFlow representing the current login status for the current anonymous user.
     */
    val isLoggedIn: StateFlow<Boolean>

    /**
     * Fetch the word of today.
     *
     * @return the word of today
     * @throws InvalidFetchedWordError if word was fetched but was invalid for some reason
     * @throws WordFetchingError if word could not be fetched (due, for exemple, to a timeout or any
     * other error occuring during the fetch itself).
     */
    suspend fun getWordOfTheDay(): IWord

    /**
     * get a list of games history starting at the given ID.
     *
     * @param from The id from which we want to fetch the history.
     * @return A GamesHistoryList containing a list of all requested items. Note that, even though
     * this list cannot be null, it can still be empty if no results were found.
     */
    suspend fun getPlayedGames(from: Int): IGamesHistoryList

    /**
     * A StateFlow representing the current Game.
     * The current game corresponds to a game either not started or currently being played.
     * It can be null if a save of today's game already exists in storage.
     * Note that both currentGame and todaysGameContent may be present at the same time. In that
     * case, todaysGameContent will take precedence and currentGame should not be used anymore.
     * Both should never be null at the same time.
     */
    val currentGame: StateFlow<IGame?>

    /**
     * A StateFlow representing the game content of today's game if it has already been saved.
     * ie : If today's game is already done.
     * Null otherwise.
     * Note that currentGame and todaysGameContent might be present at the same time. In that case,
     * todaysGameContent will take precedence and currentGame should not be used anymore.
     * Both should never be null at the same time.
     */
    val todaysGameContent: StateFlow<IGameDetail?>

    /**
     * Request the current settings
     *
     * @return the settings as saved when requested.
     */
    suspend fun getSettings(): ISettings

    // --- Actions ---

    /**
     * Request for the user to anonymously log in.
     * @return true if login succeeded, false otherwise
     */
    suspend fun login(): Boolean

    /**
     * Request the saving of a specific Game Content
     * @param game The game to save.
     */
    suspend fun saveGame(game: IGameDetail)

    /**
     * Request the content of a specific game
     * @param gameId The id of the game we would like to get the content of.
     * @return The requested game content
     * @throws UnavailableGameDetailError if requested game not found
     */
    suspend fun getGameContent(gameId: Int): IGameDetail?

    /**
     * Save the given settings
     * @param settings the settings to save.
     */
    suspend fun saveSettings(settings: ISettings)
}