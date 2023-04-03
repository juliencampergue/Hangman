package com.hangman.android.ui.screens.shareableuicomponents

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.hangman.android.R
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import com.hangman.android.ui.theme.HangmanTheme
import com.hangman.android.ui.theme.HangmanTintedImage
import com.hangman.android.ui.theme.HangmanTintedPlaceholderImage

/**
 * The hangman's drawing itself
 *
 * @param state the [HangmanDrawingState] that will store the current drawing state.
 * The state will contain all necessary information to display the proper drawing for the current
 * score.
 * @param modifier the modifier to apply to this drawing.
 */
@Composable
fun HangmanDrawing(state: HangmanDrawingState, modifier: Modifier = Modifier) {
    // The "canvas" is squared
    Box(modifier = modifier.aspectRatio(1f)) {
        if (state.displayPlaceHolder) {
            // If the placeholder should be displayed, then display it. It will fill the whole
            // "canvas"
            HangmanTintedPlaceholderImage(
                drawableResId = R.drawable.game_image_placeholder,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (state.displayScore) {
            // If there is a non-null score, then display the correct hangman parts corresponding
            // to the current score.
            HangmanTintedImage(
                drawableResId = state.drawingResId,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Helper method to create and remember a drawing state.
 *
 * @param score the game's score
 * @param maxScore the score at which the game will be lost
 * @param displayPlaceHolder should the drawing display the placeholder or not.
 */
@Composable
fun rememberHangmanDrawingState(
    score: Int,
    maxScore: Int,
    displayPlaceHolder: Boolean
): HangmanDrawingState {
    return remember(score, maxScore, displayPlaceHolder) {
        HangmanDrawingState(score, maxScore, displayPlaceHolder)
    }
}

/**
 * Represents the state of a hangman's drawing. Can be hoisted.
 *
 * @param score the game's score
 * @param maxScore the score at which the game will be lost
 * @param displayPlaceHolder should the drawing display the placeholder or not.
 */
data class HangmanDrawingState(val score: Int, val maxScore: Int, val displayPlaceHolder: Boolean) {
    /**
     * should the score be displayed.
     * If no error was made by the player, no need to try to display an empty drawing.
     */
    val displayScore: Boolean
        get() = score > 0

    /**
     * The resource ID for the drawable representing the current [score].
     */
    val drawingResId: Int
        @DrawableRes get() {
            // For the moment, maxScore is unused.
            return when(score) {
                1 -> R.drawable.game_image_score1
                2 -> R.drawable.game_image_score2
                3 -> R.drawable.game_image_score3
                4 -> R.drawable.game_image_score4
                5 -> R.drawable.game_image_score5
                6 -> R.drawable.game_image_score6
                7 -> R.drawable.game_image_score7
                8 -> R.drawable.game_image_score8
                9 -> R.drawable.game_image_score9
                10 -> R.drawable.game_image_score10
                else -> R.drawable.game_image_score11
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
fun previewsHangmanDrawing() {
    HangmanTheme() {
        HangmanDrawing(state = HangmanDrawingState(0, 11, true))
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewsHangmanDrawingWithScore() {
    HangmanTheme() {
        HangmanDrawing(state = HangmanDrawingState(5, 11, true))
    }
}
//endregion