package com.hangman.android.core.engines.internal

import com.hangman.android.bouding.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The class describing the members and methods of a Game. This will be an implementation of the
 * IGame interface in order for the rest of the application to be able to use it safely.
 * As well as holding the state of a game, it will also handle the game's processes and
 * functionalities.
 *
 * @param minScore The minimum score for this game. ie : The score at the beginning of the game.
 * @param maxScore The maximum score for this game. ie : The score at which the game will be lost.
 * @param word The word that the user will try to find with the created game instance.
 */
class Game(override val minScore: Int, override val maxScore: Int, private val word: IWord): IGame {
    // Keep the game time
    private var startTime: Long = -1
    private var playTime: Long = -1
    private var date: Long = System.currentTimeMillis()
    private var _currentScore = MutableStateFlow(minScore)
    private var _gameState = MutableStateFlow(IGame.GameState.NOT_STARTED)
    private var _playedLetters = MutableStateFlow<List<ILetter>>(emptyList())
    // Keep a list of played letters outside of the StateFlow. As StateFlow won't be updated when
    // we add a value into the list, we will update it using clones of this list instead.
    private var playedLettersList = arrayListOf<ILetter>()
    // A list of the letters in the word. Letters' uppercased values are stored into a set to
    // ensure that each letter is represented only once, and only in one way (uppercase).
    private val validLetters = word.word.uppercase().toSet()

    // Keep a "basic" version into Game, in case we were given a huge class extending IWord
    override val currentWord: IWord = Word(word)
    override val currentScore: StateFlow<Int> = _currentScore.asStateFlow()
    override val gameState: StateFlow<IGame.GameState> = _gameState.asStateFlow()
    override val playedLetters: StateFlow<List<ILetter>> = _playedLetters.asStateFlow()

    override fun start(): Boolean {
        // Only start the game if it was not already started or finished.
        when (_gameState.value) {
            IGame.GameState.NOT_STARTED -> {
                startTime = System.currentTimeMillis()
                _gameState.value = IGame.GameState.PLAYING
                return true
            }
            else -> return false
        }
    }

    override fun playLetter(letter: Char): Boolean {
        // Only when the game is currently in playing state can a letter be played. Otherwise, we
        // will throw an error.
        return when(_gameState.value) {
            IGame.GameState.NOT_STARTED -> throw HangmanError.GameNotStartedError("You must call start() before calling playLetter()")
            IGame.GameState.PLAYING -> _playLetter(letter)
            else -> throw HangmanError.GameAlreadyEndedError("You cannot play letters when the game already ended")
        }
    }

    override fun getGameDetail(): IGameDetail {
        // TODO : Use a way to have a unique id from game creation. Might be better than waiting for the game to be saved.
        return GameDetail(
            id = 0, // There is no notion of id during playing. Put invalid id by default. Id will be defined when saving the game.
            date = date,
            wordOfTheDay = word,
            result = gameState.value == IGame.GameState.OVER_SUCCESS,
            played = gameState.value == IGame.GameState.OVER_SUCCESS || gameState.value == IGame.GameState.OVER_FAILURE,
            playedLetters = playedLetters.value,
            gamePlayTime = getPlayTime()
        )
    }

    override fun getPlayTime(): Long {
        return if (playTime > 0) {
            // If playTime is valid, it means the game is over and it can be returned directly.
            playTime
        } else if (startTime > 0) {
            // If playTime is invalid, but startTime is valid, it means that the game is running.
            // So we return current time.
            System.currentTimeMillis() - startTime
        } else {
            // Otherwise, the game is not started yet, just return an invalid time.
            -1
        }
    }

    /**
     * Do the actual playing of a letter in this game.
     *
     * @param originalLetter the played letter, as a char. This is the "original" one because
     * no transformation (such as uppercase) should have been done on it yet.
     * @return true if the letter has been properly played, false if the letter could not be played.
     * Several reasons are possible for a letter not to have been played. For exemple, the letter
     * might not be valid (punctuation, others signs, etc...), or the letter have already been
     * played, etc...
     */
    private fun _playLetter(originalLetter: Char): Boolean {
        // We only use uppercased chars in the game. Do it now to avoid problems later.
        val letter = originalLetter.uppercaseChar()

        if (letter < 'A' || letter > 'Z') {
            // Invalid letter, stop processing
            return false
        }

        if (letter in playedLetters.value.map{it.letter}) {
            // Already played letter, stop processing
            return false
        }

        // Check if letter is good or not
        val goodLetter = letter in validLetters

        // First Add the letter to the list of played letters
        playedLettersList.add(Letter(letter, goodLetter))
        // TODO : Find another way to do this (using SharedFlow?) because at that point, we will
        // TODO : create a new list each time a letter is played. This won't be overwhelming though,
        // TODO : there is only so many letters that can be played during a game...
        _playedLetters.value = playedLettersList.clone() as List<ILetter>

        if (goodLetter) {
            // If letter is good, we will only check if we won.
            if (playedLettersList.filter{it.goodLetter}.size >= validLetters.size) {
                endGame(true)
            }
        } else {
            // If letter is not good, update score then check if we lost
            _currentScore.value++
            if (currentScore.value >= maxScore) {
                endGame(false)
            }
        }

        return true
    }

    /**
     * End the game, successfully or not.
     *
     * @param success Pass true if the game was won, false if it was lost.
     */
    private fun endGame(success: Boolean) {
        _gameState.value = if (success) IGame.GameState.OVER_SUCCESS else IGame.GameState.OVER_FAILURE
        playTime = System.currentTimeMillis() - startTime
    }
}