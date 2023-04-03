package com.hangman.android.ui.screens.components

/**
 * The list of possible screens in the application.
 */
enum class Screens {
    /**
     * The Splash screen
     */
    SPLASH,

    /**
     * The game of the day screen. This will be the application's home screen and can display games
     * that are started, finished, or even not started.
     */
    GAME,

    /**
     * The settings screen
     */
    SETTINGS,

    /**
     * The screen that will display the already played games only.
     */
    HISTORY,

    /**
     * The screen that will describe the details of a specific finished game only.
     */
    DETAILS,

    /**
     * The screen that will display for the list of already played games along with the details
     * of a specific selected game.
     */
    HISTORY_AND_DETAILS
}