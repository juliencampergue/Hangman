package com.hangman.android.ui.screens.states

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bounding.DataState

/**
 * Helper function to remember the [GameScreenState] computed from the given data states.
 *
 * @param wordState The [DataState] representing the state of the word of the day fetch.
 * @param playedGameState The [DataState] representing the game details of today's game if it has
 * already been finished
 * @param currentGameState The [DataState] representing the game of today if it has not been
 * finished yet.
 * @param settings The [DataState] representing the state of the settings load.
 */
@Composable
fun rememberGameScreenState(
    wordState: DataState?,
    playedGameState: DataState?,
    currentGameState: DataState?,
    settings: DataState?,
): GameScreenState {
    return remember(wordState, playedGameState, currentGameState, settings) {
        getGameScreenState(wordState, playedGameState, currentGameState, settings)
    }
}

/**
 * Describe the different possible states for the game screen
 * Display Played game and Display game in progress are different states because we won't display
 * the same information in both cases.
 */
sealed interface GameScreenState {
    /**
     * The game screen is currently loading
     */
    object LOADING: GameScreenState

    /**
     * There was an error at some point during the game or while fetching some information.
     *
     * @param error The actual error that happened
     */
    data class ERROR(val error: Throwable): GameScreenState

    /**
     * The game of the day was already played and its details were successfuly retrieved. So display
     * those details
     *
     * @param data The actual game detail of today's game to display.
     */
    data class DISPLAY_PLAYED_GAME<T>(val data: T): GameScreenState

    /**
     * The game of the day is not finished yet. So display the game in progress screen.
     * Not that the game not started screen is part of the game in progress screen.
     *
     * @param data The game to play itself
     * @param settings The current settings to apply to the game.
     */
    data class DISPLAY_GAME_IN_PROGRESS<T, S>(val data: T, val settings: S): GameScreenState
}

/**
 * Use all the given [DataState] to compute the actual [GameScreenState]
 *
 * @param wordState The [DataState] representing the state of the word of the day fetch.
 * @param playedGameState The [DataState] representing the game details of today's game if it has
 * already been finished
 * @param currentGameState The [DataState] representing the game of today if it has not been
 * finished yet.
 * @param settings The [DataState] representing the state of the settings load.

 */
private fun getGameScreenState(
    wordState: DataState?,
    playedGameState: DataState?,
    currentGameState: DataState?,
    settingsState: DataState?
): GameScreenState {
    return when {
        // First, check if any state is null. In that case, we consider we are starting and we should
        // be loading. This should never happen really as all state should be at least FETCHING...
        wordState == null
                || playedGameState == null
                || currentGameState == null
                || settingsState == null -> GameScreenState.LOADING

        // Then, check if we are on an error state.
        wordState is DataState.ERROR -> GameScreenState.ERROR(wordState.error)
        playedGameState is DataState.ERROR -> GameScreenState.ERROR(playedGameState.error)
        currentGameState is DataState.ERROR -> GameScreenState.ERROR(currentGameState.error)
        settingsState is DataState.ERROR -> GameScreenState.ERROR(settingsState.error)
        wordState is DataState.NOTAVAILABLE -> GameScreenState.ERROR(HangmanError.WordFetchingError("word of the day could not be fetched"))
        wordState is DataState.DATA<*>
                && playedGameState is DataState.NOTAVAILABLE
                && currentGameState is DataState.NOTAVAILABLE -> {
            GameScreenState.ERROR(HangmanError.UnknownError("We did not get anything, something went wrong"))
        }
        wordState is DataState.DATA<*>
                && playedGameState is DataState.NOTAVAILABLE
                && currentGameState is DataState.DATA<*>
                && settingsState is DataState.NOTAVAILABLE -> {
                    // We should be displaying the current game, but settings could not be fetched
                    // for some reason. So we will display an error
            GameScreenState.ERROR(HangmanError.UnknownError("We could not fetch the settings while needing it."))
        }

        // We ruled out errors, let's see the rest now.
        // If we are still fetching word of today OR played game details, then we are still loading.
        wordState is DataState.FETCHING || playedGameState is DataState.FETCHING -> GameScreenState.LOADING
        // Only if we have fetched the word and the played game is empty will we check the current
        // game and the settings.
        wordState is DataState.DATA<*>
                && playedGameState is DataState.NOTAVAILABLE
                && (currentGameState is DataState.FETCHING
                    || settingsState is DataState.FETCHING) -> GameScreenState.LOADING

        // We also ruled out loading states, now we decide when we will display game screens and
        // which.
        // If the played game is not empty, then display the finished game details.
        wordState is DataState.DATA<*> && playedGameState is DataState.DATA<*> -> GameScreenState.DISPLAY_PLAYED_GAME(playedGameState.data)
        // If the played game is empty, then there should be a game in progress, in which case,
        // display the game in progress screen.
        wordState is DataState.DATA<*>
                && playedGameState is DataState.NOTAVAILABLE
                && currentGameState is DataState.DATA<*>
                && settingsState is DataState.DATA<*> -> GameScreenState.DISPLAY_GAME_IN_PROGRESS(currentGameState.data, settingsState.data)

        // We are in an unknown state.
        else -> GameScreenState.ERROR(HangmanError.UnknownError("We are in an unknwon state, something went wrong"))
    }
}