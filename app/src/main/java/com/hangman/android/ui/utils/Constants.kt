package com.hangman.android.ui.utils

/**
 * The timeout value for the login process.
 */
const val LOGIN_TIMEOUT_MILLIS: Long = 4000 // 4s

/**
 * The threshold under which we will load more history if possible. IE : When there is less items
 * than this threshold left to display at the end of the history, then more history items should
 * be fetched to ensure a smooth scrolling for the user.
 */
const val LOAD_MORE_HISTORY_THRESHOLD: Int = 10