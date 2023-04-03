package com.hangman.android.bouding

import kotlinx.coroutines.flow.StateFlow

// region interfaces
/**
 * Describes what a Word is inside the Application. Each word can be played once and can have an
 * associated game.
 * The word itself may not be unique throughout all the words played, but its ID cannot.
 * The date in itself is here to indicate when the word should be played. As such, it should be
 * unique as well.
 */
interface IWord {
    /**
     * The ID of this word
     */
    val id: String

    /**
     * The date at which this word should/can be played.
     */
    val date: Long

    /**
     * The word in itself
     */
    val word: String
}

/**
 * Describes a letter than the user played during a game. It will be associated to said game.
 * It is basically a pair of information. The letter in itself, and whether it is a good letter or
 * not. ie : Is the letter part of the word associated to the game in which the letter was played.
 */
interface ILetter {
    /**
     * The letter in itself
     * It should only be an unaccentuated, uppercased alphabetical letter. ie : {A-Z}
     */
    val letter: Char

    /**
     * True if this letter is part of the word associated to the game it was played on.
     */
    val goodLetter: Boolean
}

/**
 * Represents an actual game, including the states and necessary methods to play and extract details.
 */
interface IGame {
    /**
     * The different possible states of a game.
     */
    enum class GameState {
        /**
         * The current game has not started yet
         */
        NOT_STARTED,

        /**
         * The current game is not over yet
         */
        PLAYING,

        /**
         * The current game is over and the player won
         */
        OVER_SUCCESS,

        /**
         * The current game is over and the player lost
         */
        OVER_FAILURE
    }

    /**
     * The minimum score will correspond to the score at the beginning of the game. (ie : 0 by default)
     * The score in itself correspond to the number of "wrong" letters that were played.
     */
    val minScore: Int

    /**
     * The maximum score will correspond to the score that means game over if the user reach it.
     * The score in itself correspond to the number of "wrong" letters that were played.
     */
    val maxScore: Int

    /**
     * The current score of the user, ie: The number of times the user gave a wrong answer
     */
    val currentScore: StateFlow<Int>

    /**
     * The word the user is trying to find
     */
    val currentWord: IWord

    /**
     * Start the game (and thus, the timer).
     * That game needs to be started before playing. If a letter is played before the game is started,
     * then an error will be returned.
     * @return true if game started, false if it was already started
     */
    fun start(): Boolean

    /**
     * The current state of the game
     */
    val gameState: StateFlow<GameState>

    /**
     * Call this function to play a letter
     * @param letter The letter to play.
     * @return true if the letter has been taken into account. It will not be taken into account if
     * either it is not a valid letter, or it has already been played
     * @throws GameNotStartedError if game was not started before playing the letter
     * @throws GameAlreadyEnded if game was already ended before playing the letter
     */
    fun playLetter(letter: Char): Boolean

    /**
     * The list of currently played letters, whether they were good or bad.
     */
    val playedLetters: StateFlow<List<ILetter>>

    /**
     * Get the details of the current game
     * @return The details of the current game
     */
    fun getGameDetail(): IGameDetail

    /**
     * Query the current game time
     * @return The current play time of this game.
     */
    fun getPlayTime(): Long
}

/**
 * A lighter Game Detail that will be used in the history list. It will be lighter because the
 * history list potentially needs to display a lot of items, and thus informations that should not
 * be used in the list has been scraped for the list items.
 */
interface IGameHistoryItem {
    /**
     * The id of this history item. It will correspond to the id of the game detail and can be
     * used to fetch the whole game detail afterwards.
     */
    val id: Int

    /**
     * The date this game was played
     */
    val date: Long

    /**
     * The id of the played word
     */
    val wordId: String

    /**
     * The played word in itself
     */
    val word: String

    /**
     * The result of the game, true if success, false if failure
     */
    val result: Boolean

    /**
     * true if the game is already over, false otherwise
     */
    val played: Boolean
}

/**
 * The Details of a game. It represents all the informations that is known about a specific game,
 * without all the means to play it.
 */
interface IGameDetail {
    /**
     * The id of this game
     */
    val id: Int

    /**
     * The date the game was played
     */
    val date: Long

    /**
     * The played word
     */
    val wordOfTheDay: IWord

    /**
     * The result of the game, true is success, false if failure
     */
    val result: Boolean

    /**
     * true if game has already been played, false otherwise
     */
    val played: Boolean

    /**
     * The letters the user played, in the order they were played
     */
    val playedLetters: List<ILetter>

    /**
     * How much time the user took to complete this game
     */
    val gamePlayTime: Long
}

/**
 * Represents a subset of Game History Items. This object, in addition to the item list, will
 * contain informations about the subset itself.
 */
interface IGamesHistoryList {
    /**
     * The actual list of history items. It can be empty if no game have been found or if the
     * end of the games list has been reached for instance.
     */
    val games: List<IGameHistoryItem>

    /**
     * True if the last item of the history has been reached.
     */
    val isLastGameReached: Boolean
}

/**
 * Represents the application settings that the user can modify.
 */
interface ISettings {
    /**
     * True if we should display the timer during the game.
     */
    val displayTimer: Boolean
}
// endregion

// region default implementations
/**
 * A default implementation of the IWord interface.
 * It is a data class implementing all the necessary members.
 */
data class Word(override val id: String, override val date: Long, override val word: String): IWord {
    /**
     * Builds a basic IWord implementation using an exist IWord instance.
     */
    constructor(word: IWord): this(word.id, word.date, word.word)
}

/**
 * A default implementation for the ILetter interface.
 * It is a data class implementing all the necessary members.
 */
data class Letter(override val letter: Char, override val goodLetter: Boolean): ILetter {
    /**
     * Builds a basic ILetter implementation using an existing ILetter instance.
     */
    constructor(letter: ILetter): this(letter.letter, letter.goodLetter)
}

/**
 * A default implementation for the IGameHistoryItem interface.
 * It is a data class implementing all the necessary members.
 */
data class GameHistoryItem(override val id: Int,
                           override val date: Long,
                           override val wordId: String,
                           override val word: String,
                           override val result: Boolean,
                           override val played: Boolean): IGameHistoryItem {
    /**
     * Builds a basic IGameHistoryItem implementation using an existing IGameHistoryItem instance.
     */
   constructor(item: IGameHistoryItem): this(item.id, item.date, item.wordId, item.word, item.result, item.played)
}

/**
 * A default implementation for the IGameDetail interface.
 * It is a data class implementing all the necessary members.
 */
data class GameDetail(override val id: Int,
                      override val date: Long,
                      override val wordOfTheDay: IWord,
                      override val result: Boolean,
                      override val played: Boolean,
                      override val playedLetters: List<ILetter>,
                      override val gamePlayTime: Long): IGameDetail {
    /**
     * Builds a basic IGameDetails implementation using an existing IGameDetail instance.
     */
    constructor(detail: IGameDetail): this(
        detail.id,
        detail.date,
        Word(detail.wordOfTheDay),
        detail.result,
        detail.played,
        detail.playedLetters.map {Letter(it)},
        detail.gamePlayTime
    )
}

/**
 * A default implementation for the IGameHistoryList interface.
 * It is a data class implementing all the necessary members.
 */
data class GamesHistoryList(override val games: List<IGameHistoryItem>,
                            override val isLastGameReached: Boolean): IGamesHistoryList {
    /**
     * Builds a basic IGameHistoryList implementation using an existing IGameHistoryList instance.
     */
    constructor(list: IGamesHistoryList): this(
        games = list.games.map {GameHistoryItem(it)},
        isLastGameReached = list.isLastGameReached
    )
}

/**
 * A default implementation for the ISettings interface.
 * It is a data class implementing all the necessary members.
 */
data class Settings(override val displayTimer: Boolean): ISettings
// endregion
