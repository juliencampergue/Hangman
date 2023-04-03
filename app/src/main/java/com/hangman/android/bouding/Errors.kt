package com.hangman.android.bouding

/**
 * A class representing all the possible error values of this application.
 *
 * @param msg a message describing the error that occured
 * @param cause The eventual error that was thrown prior to the creation of this one.
 */
sealed class HangmanError(msg: String?, cause: Throwable?): Throwable(msg, cause) {
    /**
     * An error occured but it has no specified origin
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class UnknownError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    // Backend Errors
    /**
     * An error that occured during a network request.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class NetworkError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    /**
     * Today's word was fetched but was invalid.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class InvalidFetchedWordError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    /**
     * Today's word could not be fetched, for exemple in case a timeout occured
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class WordFetchingError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    // Repositories Error
    /**
     * For some reason, the requested game content could not be found.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class UnavailableGameDetailError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    // Game Errors
    /**
     * A letter was played despite the fact that the game was not started yet.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class GameNotStartedError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    /**
     * A letter was played despite the fact that the game was already finished.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class GameAlreadyEndedError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    // Settings Errors
    /**
     * For some reason, the settings could not be fetched.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class SettingsFetchingError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    // History and Details Error
    /**
     * Something went wrong while fetching the game's history list.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class HistoryFetchingError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)

    /**
     * Something went wrong while fetching the details of a specific game.
     *
     * @param msg a message describing the error that occured
     * @param cause The eventual error that was thrown prior to the creation of this one.
     */
    class DetailFetchingError(msg: String?=null, cause: Throwable?=null): Error(msg, cause)
}
