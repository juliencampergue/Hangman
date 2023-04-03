package com.hangman.android.ui.screens.gamescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.*
import com.hangman.android.ui.previewutils.DayNightModePreviews
import com.hangman.android.ui.screens.components.*
import com.hangman.android.ui.screens.shareableuicomponents.HangmanDrawing
import com.hangman.android.ui.screens.shareableuicomponents.rememberHangmanDrawingState
import com.hangman.android.ui.theme.*

/**
 * Convertion constant. How much milliseconds in a second.
 */
const val SECONDS_IN_MILLIS = 1000

/**
 * Convertion constant. How much milliseconds in a minute.
 */
const val MIN_IN_MILLIS = 60 * SECONDS_IN_MILLIS

/**
 * Convertion constant. How much milliseconds in an hour.
 */
const val HOUR_IN_MILLIS = 60 * MIN_IN_MILLIS

/**
 * The component representing a finished game.
 *
 * @param game The details of the game to display
 * @param shareGame The action to perform is the "share" button is pressed. If null, the share
 * button will not be displayed.
 * @param modifier The modifier to apply to this composable.
 * @param onBack The action to perform if a back is pressed. If non-null, a back button will be
 * displayed in the component's topbar. If null, the back button will not be displayed.
 */
@Composable
fun GameOverScreen(
    game: IGameDetail,
    shareGame: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    // Get and remember the state of the hangman's drawing.
    // TODO Save score into game detail instead of re extracting it every time.
    // Another TODO : Save the maxScore in gameDetails as well!
    val hangmanDrawingState = rememberHangmanDrawingState(
        score = game.playedLetters.filter { !it.goodLetter }.size,
        maxScore = 0,
        displayPlaceHolder = true
    )

    // We use a scaffold because there is a topbar as well as a bottom bar. Both bars should always
    // be displayed. The content will match the remaining size.
    Scaffold(
        modifier = modifier,
        topBar = {
            GameOverTopBar(
                word = game.wordOfTheDay.word,
                result = game.result,
                playTime = game.gamePlayTime,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack,
            )
        },
        bottomBar = {
            GameOverBottomBar(
                game.playedLetters,
                shareGame,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = smallSpacing),
            )
        }
    ) {
        // Use the box to add the padding around the content. The padding correspond to the size
        // taken by both the topbar and the bottombar. The content will then take as much place as
        // possible.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HangmanDrawing(
                state = hangmanDrawingState,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * The component to use to display the "Share" button.
 *
 * @param onClick the action to perform when this button is clicked
 * @param modifier the modifier to apply to this button.
 */
@Composable
fun ShareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The share button is basically a filled button containing the share text and the share icon.
    HangmanFilledButton(
        onClick = onClick,
        text = {
            Text(text = stringResource(id = R.string.game_over_share_button))
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_share_24),
                contentDescription = null
            )
        },
        modifier = modifier,
    )
}

/**
 * The component to use to display the topbar back button.
 *
 * @param onClick The action to perform when this button is clicked.
 * @param modifier the modifier to apply to this button.
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The back button is basically a button displaying a back arrow.
    IconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = null)
    }
}

/**
 * The component to use as the topbar for the gameover screen.
 *
 * @param word The played word
 * @param playTime The raw time taken to play the game. In milliseconds.
 * @param result was the game won or lost
 * @param modifier The modifier to apply to this topbar.
 * @param onBack The action to perform when the back button is pressed. If null, the back button
 * will not be displayed.
 */
@Composable
fun GameOverTopBar(
    word: String,
    playTime: Long,
    result: Boolean,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)?,
) {
    // We use a resultTopBar that will display differently depending on the given result.
    HangmanResultTopBar(
        result,
        modifier = modifier
    ) {
        // Use a box to allow more fine grained alignments.
        Box(modifier = Modifier.fillMaxSize()) {
            if (onBack != null) {
                // If onBack function is not null, add the back button to the composition.
                BackButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            // Display the game's word
            HangmanWordText(
                text = word,
                modifier = Modifier.align(Alignment.Center)
            )
            // Display the play time. Settings are not taken into account here because the game is
            // over. The settings is for displaying the time when the game is being played.
            HangmanPlayedTime(
                time = playTime,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

/**
 * The component that will display elements that need to stay at the bottom of the game over screen.
 *
 * @param playedLetter The list of letters that were played during this game along with their status
 * (good or bad letter).
 * @param onShare The action to perform when the user clicks on the "share" button. If null, the
 * share button will not be displayed.
 * @param modifier The modifier to apply to this bottom bar.
 */
@Composable
fun GameOverBottomBar(
    playedLetter: List<ILetter>,
    onShare: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    // Share button and played letters grid are stacked up vertically.
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HangmanSectionTitle(text = stringResource(R.string.game_over_played_letters))
        PlayedLetters(playedLetters = playedLetter)
        if (onShare != null) {
            ShareButton(
                onClick = onShare,
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
@DayNightModePreviews
@Composable
fun previewGameOverSuccess() {
    HangmanTheme() {
        GameOverScreen(
            game = GameDetail(
                id = 1,
                date = 1,
                wordOfTheDay = Word("word", 1234, "hello"),
                played = true,
                result = true,
                playedLetters = listOf(
                    Letter('H', true),
                    Letter('X', false),
                    Letter('Y', false),
                    Letter('E', true),
                    Letter('L', true),
                    Letter('O', true),
                ),
                gamePlayTime = 17000
            ),
            shareGame = {},
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGameOverFailure() {
    HangmanTheme() {
        GameOverScreen(
            game = GameDetail(
                id = 1,
                date = 1,
                wordOfTheDay = Word("word", 1234, "hello"),
                played = true,
                result = false,
                playedLetters = listOf(
                    Letter('H', true),
                    Letter('X', false),
                    Letter('Y', false),
                    Letter('E', true),
                    Letter('L', true),
                    Letter('B', false),
                    Letter('C', false),
                    Letter('D', false),
                    Letter('F', false),
                    Letter('G', false),
                    Letter('I', false),
                    Letter('J', false),
                    Letter('K', false),
                    Letter('M', false),
                ),
                gamePlayTime = 170000
            ),
            shareGame = {},
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGameOverFailureWithBackButtonAndNotShareable() {
    HangmanTheme() {
        GameOverScreen(
            game = GameDetail(
                id = 1,
                date = 1,
                wordOfTheDay = Word("word", 1234, "hello"),
                played = true,
                result = false,
                playedLetters = listOf(
                    Letter('H', true),
                    Letter('X', false),
                    Letter('Y', false),
                    Letter('E', true),
                    Letter('L', true),
                    Letter('B', false),
                    Letter('C', false),
                    Letter('D', false),
                    Letter('F', false),
                    Letter('G', false),
                    Letter('I', false),
                    Letter('J', false),
                    Letter('K', false),
                    Letter('M', false),
                ),
                gamePlayTime = 170000
            ),
            shareGame = null,
            onBack = {},
        )
    }
}
//endregion Previews
