package com.hangman.android.ui.screens.shareableuicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hangman.android.ui.theme.HangmanWordText

/**
 * The composable to use when displaying the word of the day
 *
 * @param word The word to display
 * @param playedLetters The set of letters that were played for this word.
 * @param modifier The modifier to apply to this composable
 */
@Composable
fun HangmanWordComposable(
    word: String,
    playedLetters: Set<Char>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // For each letter of the word, we will display either the letter if it has already been
        // found, or an underscore if the letter has not been found yet.
        for (i in 0..word.length - 1) {
            var text = "_ "
            val letter = word.get(i)
            if (letter in playedLetters) {
                text = "${letter} "
            }
            HangmanWordText(text = text)
        }
    }
}