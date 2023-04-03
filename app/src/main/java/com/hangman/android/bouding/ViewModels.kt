package com.hangman.android.bouding

import androidx.lifecycle.LiveData
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState

/**
 * The ViewModel associated with the Splash Screen / Start of the application.
 * It will contain all the necessary actions to determinate if the application can be properly
 * started or should be stopped at startup.
 */
interface ISplashScreenViewModel {
    /**
     * The initialization state for the splash screen.
     * We should be initialized when the authentication succeeded.
     */
    val loginState: LiveData<RequestState>

    /**
     * Retry the initialization if failed
     */
    fun retry()
}

/**
 * The ViewModel associated with the Games and game screens. both idle games and games being played.
 * It will contain all the necessary actions to handle a game.
 */
interface IGameScreenViewModel {
    /**
     * The current login status.
     * GameScreen will be the "home" screen, as such, this ViewModel should host the current
     * login status in order to choose what to do at startup.
     */
    val isLoggedIn: LiveData<Boolean>

    /**
     * The current state of the word of today's fetch.
     * Even if NOT_AVAILABLE might be a legit DataState value in some cases, it will never be
     * with the word of the day. NOT_AVAILABLE should not happen, and should be considered an error
     * in the off chance that it does.
     */
    val wordOfTodayState: LiveData<DataState>

    /**
     * If the fetch of today's word failed, you can restart the fetch process with this method.
     */
    fun retryGetWordOfToday()

    /**
     * A StateFlow representing the current Game.
     * The current game corresponds to a game either not started or currently being played.
     * It can be null if a save of today's game already exists in storage.
     * Note that both currentGame and todaysGameContent may be present at the same time. In that
     * case, todaysGameContent will take precedence and currentGame should not be used anymore.
     * Both should never be null at the same time.
     */
    val currentGameState: LiveData<DataState>

    /**
     * A StateFlow representing the game content of today's game if it has already been saved.
     * ie : If today's game is already done.
     * Null otherwise.
     * Note that currentGame and todaysGameContent might be present at the same time. In that case,
     * todaysGameContent will take precedence and currentGame should not be used anymore.
     * Both should never be null at the same time.
     */
    val todaysGameContentState: LiveData<DataState>

    /**
     * Save a gameDetails in the user's history.
     *
     * This should only be called when the game is finished. This is not a way to temporarily store
     * a game while playing for any reason.
     *
     * @param game The game to save in storage.
     */
    fun saveGame(game: IGameDetail)

    /**
     * The state of the saving of the current game.
     */
    val saveGameState: LiveData<RequestState>
}

/**
 * The ViewModel associated with the History Screen.
 * It will contain all the necessary actions to access the game history properly.
 */
interface IHistoryScreenViewModel {
    /**
     * The current state of the history being fetched.
     * It should never be "NOT_AVAILABLE".
     * It will always contain the full up to date fetched history, not only the last requested
     * subset.
     * It might be cleared and its content replaced in the case of a full refresh.
     * Let's see the following exemple showing requests and content of this data after each request.
     *
     * DATA: |__EMPTY__|
     *
     * REQUEST: REFRESH
     *
     * DATA: |__ID1__|
     *       |__...__|
     *       |__IDn__|
     *
     * REQUEST: LOAD_MORE()
     *
     * DATA: |__ID1__|
     *       |__...__|
     *       |__IDn__|
     *       |__IDn+1__|
     *       |__...__|
     *       |__IDn*2__|
     *
     * REQUEST: REFRESH
     *
     * DATA: |__ID1__|
     *       |__...__|
     *       |__IDn__|
     */
    val gamesHistoryState: LiveData<DataState>

    /**
     * The "load more" request state.
     * It will be distinct from gameHistoryState because UI might want to display both informations
     * in a different way.
     * This request will not hold any data, which is why this is a RequestState object. The actual
     * data will be stored in the gamesHistoryState state, which will be updated when load more
     * data will have been loaded.
     */
    val loadMoreState: LiveData<RequestState>

    /**
     * Refresh the list of history items.
     * Actually make a request for a subset of the most recent history items.
     * Note that the actual data stored in gamesHistoryState will be cleared and only the subset
     * of the most recent games will be available. If this is not the wanted behavior, see the
     * loadMore() method.
     */
    fun refresh()

    /**
     * Load more played games.
     * Actually make a request from a subset of the most recent history items, starting at the last
     * one that was reached during the previous request. The full list, containing the previously
     * fetched subset as well as the one retrieved with this method will be available. If this is
     * not the wanted behavior, see the refresh() method.
     */
    fun loadMore()

    /**
     * Has the last game of the history been fetched?
     * If true, loadMore method is still available, but should not add anything to the history list.
     */
    val isLastGameReached: LiveData<Boolean>
}

/**
 * The ViewModel associated with finished games.
 * It will contain all the necessary actions to display a game.
 *
 * It will associated to a specific ID and can only fetch the content for that specific game.
 * The id can be changed during the life of the ViewModel, meaning that a different content will
 * be fetched to be displayed.
 * An invalid ID can also be provided, meaning that the ViewModel will be "cleared" and should not
 * fetch or provide anymore game content for the time being.
 */
interface IGameContentScreenViewModel {
    /**
     * The content of the game to display.
     * NOT_AVAILABLE means that the ViewModel has been "cleared".
     * This is not an invalid value as invalid ids can be given to this viewModel to clear it and
     * indicate that no game content should be displayed.
     */
    val gameContentState: LiveData<DataState>

    /**
     * Is the given game ID valid?
     * a valid ID means that the game detail could be fetched.
     * An invalid ID means that the viewModel must have been cleared or that given game ID for
     * detail retrieval is not valid.
     */
    val validDetailId: LiveData<Boolean>

    /**
     * Change the game id of which we want to display the details.
     * Game Details will be fetched if gameId is valid.
     * If gameId is not valid, this basically clears the viewModel to indicate that no game content
     * should be displayed.
     *
     * @param gameId The id of the game of which we want to fetch the content. Any value <0 will
     * be considered invalid.
     */
    fun onChangeGameContent(gameId: Int)

    /**
     * Clear the viewModel of game ID to indicate that no game content should be displayed.
     */
    fun onClear()

    /**
     * Retry fetching the game detail of the current gameId
     */
    fun retry()
}

/**
 * The ViewModel associated with the Settings Screen.
 * It will contain all the necessary actions to load and save the Settings properly.
 */
interface ISettingsScreenViewModel {
    /**
     * The current settings.
     * It should never be NOT_AVAILABLE and this value should be considered as an ERROR.
     */
    val settingsState: LiveData<DataState>

    /**
     * Retry fetching the settings
     */
    fun retry()

    /**
     * Permanently save the settings
     *
     * @param settings The settings to save.
     */
    fun saveSettings(settings: ISettings)

    /**
     * The state of the saveSettings request.
     */
    val saveSettingsState: LiveData<RequestState>
}