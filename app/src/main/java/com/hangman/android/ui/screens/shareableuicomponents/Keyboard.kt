package com.hangman.android.ui.screens.shareableuicomponents

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.hangman.android.bouding.ILetter
import com.hangman.android.bouding.Letter
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import com.hangman.android.ui.theme.HangmanLetterButton
import com.hangman.android.ui.theme.HangmanTheme
import com.hangman.android.ui.theme.HangmanVerticalGrid

/**
 * The keyboard that the player will use to play a specific letter.
 * Unplayed letters will be displayed with a default background. Won letter will be displayed with
 * a 'win' background, and lost letters will be displayed with a 'lost' background.
 *
 * @param playedLetters The letters that have been played. This is a list of ILetter because we
 * need to know whether the letters were good or not to know how to display them.
 * @param playLetter The action to perform when a user clicks on a letter on the keyboard.
 * @param modifier The modifier to apply to this keyboard.
 */
@Composable
fun HangmanKeyboardComposable(
    playedLetters: List<ILetter>,
    playLetter: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    // remember the good letters and only recreate the list when the list of played letters changes.
    // If there is a recomposition for another reason, no need to recreate this list.
    val validLetters = rememberSaveable(playedLetters) {
        playedLetters
            .filter { it.goodLetter }
            .map { it.letter }
    }
    // remember the bad letters and only recreate the list when the list of played letters changes.
    // If there is a recomposition for another reason, no need to recreate this list.
    val invalidLetters = rememberSaveable(playedLetters) {
        playedLetters
            .filter { !it.goodLetter }
            .map { it.letter }
    }
    // Create the keys of the keyboard. There will be one for each letter of the alphabet.
    val alphabet = rememberSaveable { listOf('A'..'Z').flatten() }

    // The keyboard will be a simple grid containing each possible letter.
    // Each button will be either playable, or already played, in which case it will be either
    // good (the letter was part of the word) or bad.
    HangmanVerticalGrid(modifier = modifier) {
        items(alphabet) {
            // Remember letter's validity of each letter if the lists of played letters did not
            // change.
            val valid: Boolean? = remember(validLetters, invalidLetters) {
                when {
                    it in validLetters -> true
                    it in invalidLetters -> false
                    else -> null
                }
            }
            HangmanLetterButton(
                // When user clicks on this key, we play the associated letter.
                onClick = {playLetter(it)},
                valid = valid,
                letter = { Text(text = it.toString()) },
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
fun previewsHangmanKeyboardComposable() {
    HangmanTheme() {
        HangmanKeyboardComposable(
            playedLetters = listOf(
                Letter('H', true),
                Letter('X', false),
                Letter('Y', false),
                Letter('E', true),
                Letter('L', true),
                Letter('O', true),
            ),
            playLetter = {}
        )
    }
}
//endregion