package com.hangman.android.ui.screens.components

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.hangman.android.bouding.ILetter
import com.hangman.android.bouding.Letter
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import com.hangman.android.ui.screens.gamescreen.HOUR_IN_MILLIS
import com.hangman.android.ui.screens.gamescreen.MIN_IN_MILLIS
import com.hangman.android.ui.screens.gamescreen.SECONDS_IN_MILLIS
import com.hangman.android.ui.theme.*

/**
 * The composable that displays the play time
 *
 * @param time The time to display, in milliseconds
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun HangmanPlayedTime(
    time: Long,
    modifier: Modifier = Modifier,
) {
    // Create and remember the time string to display
    val playedTimeString = remember(time) {getPlayTimeString(time)}

    HangmanTimeText(
        text = playedTimeString,
        modifier = modifier,
    )
}

/**
 * Transforms a raw time in milliseconds into a displayable time string.
 *
 * @param time the time to display, in milliseconds
 * @return The constructed String to display to user.
 */
private fun getPlayTimeString(time: Long): String {
    // Extract seconds, minutes and hours from the given time
    var remainingTime = time
    val hours = remainingTime / HOUR_IN_MILLIS
    remainingTime %= HOUR_IN_MILLIS
    val minutes = remainingTime / MIN_IN_MILLIS
    remainingTime %= MIN_IN_MILLIS
    val seconds = remainingTime / SECONDS_IN_MILLIS

    // Then build the time string.
    return buildString {
        if (hours > 0) {
            // If time >= 1 hour, only display hours
            append("$hours").append("h")
        } else {
            // Else display minutes and seconds in the mm:ss format.
            append(String.format("%02d", minutes))
            append(":")
            append(String.format("%02d", seconds))
        }
    }
}

/**
 * The composable to use to display the summary of the played letters in a finished game.
 * Display will be in the form of a grid with each cell containing a played letter.
 * Played letters will be displayed differently whether the letter was good or not.
 *
 * @param playedLetters The list of letters that were played.
 * @param modifier The modifier to apply to this composable.
 */
@Composable
fun PlayedLetters(
    playedLetters: List<ILetter>,
    modifier: Modifier = Modifier,
) {
    HangmanVerticalGrid(modifier = modifier) {
        items(playedLetters) {
            HangmanLetterButton(
                // This is a summary for a finished game. No action should be made when user clicks
                // on a letter.
                onClick = {},
                valid = it.goodLetter,
                letter = { Text(text = it.letter.toString()) },
            )
        }
    }
}

//region Previews
/*
 * --------
 * Previews
 * --------
 */
@DayNightModeWithBackgroundPreviews
@Composable
fun previewPlayedLetters() {
    HangmanTheme {
        PlayedLetters(
            playedLetters = listOf(
                Letter('H', true),
                Letter('X', false),
                Letter('Y', false),
                Letter('E', true),
                Letter('L', true),
                Letter('O', true),
            )
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewPlayTimeSeconds() {
    HangmanTheme {
        HangmanPlayedTime(
            time=53000
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewPlayTimeMinutes() {
    HangmanTheme {
        HangmanPlayedTime(
            time=153000
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewPlayTimeHours() {
    HangmanTheme {
        HangmanPlayedTime(
            time=15300000
        )
    }
}
//endregion