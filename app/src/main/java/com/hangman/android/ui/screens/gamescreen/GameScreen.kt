package com.hangman.android.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.hangman.android.R
import com.hangman.android.bouding.*
import com.hangman.android.ui.screens.components.*
import com.hangman.android.ui.screens.gamescreen.GameOverScreen
import com.hangman.android.ui.screens.states.GameScreenState
import com.hangman.android.ui.screens.states.rememberGameScreenState

/**
 * The composable that acts as the entry point for the Game Screen itself.
 *
 * @param viewModel The [IGameScreenViewModel] used by this screen to get all necessary game data
 * and do all necessary actions.
 * @param settingsViewModel The [ISettingsScreenViewModel] used by this screen to apply settings
 * to the game in progress if applicable
 * @param onWordCantBeFetched The action to perform if we cannot fetch the word of today, and thus
 * we don't know what to do.
 * @param modifier The modifier to apply to this screen
 */
@Composable
fun GameScreen(viewModel: IGameScreenViewModel,
               settingsViewModel: ISettingsScreenViewModel,
               onWordCantBeFetched: () -> Unit,
               modifier: Modifier = Modifier) {
    val wordOfToday by viewModel.wordOfTodayState.observeAsState()
    val currentGame by viewModel.currentGameState.observeAsState()
    val playedGame by viewModel.todaysGameContentState.observeAsState()
    val settings by settingsViewModel.settingsState.observeAsState()

    // Compute or remember the current screen state to know what to display exactly
    val gameScreenState = rememberGameScreenState(
        wordState = wordOfToday,
        playedGameState = playedGame,
        currentGameState = currentGame,
        settings = settings,
    )

    // The Android context needed for Android specific actions such as sharing process.
    val context = LocalContext.current

    when(gameScreenState) {
        is GameScreenState.LOADING -> {
            // If we are still loading, display the loading screen
            ScreenLoading(
                loadingText = stringResource(id = R.string.game_screen_loading),
                onBack = onWordCantBeFetched
            )
        }
        is GameScreenState.ERROR -> {
            // If there was an error somewhere, display an error popup and let the user decide what
            // to do. If user decides to retry, we will fetch the word of today once again, which
            // will trigger any other operation that need it to be ran. If user dismisses this popup,
            // then the word is considered not fetchable.
            ErrorDialog(
                onConfirm = {viewModel.retryGetWordOfToday()},
                onDismiss = onWordCantBeFetched,
                state = rememberErrorDialogState(Screens.GAME, gameScreenState.error)
            )
        }
        is GameScreenState.DISPLAY_PLAYED_GAME<*> -> {
            // A played game is simply a GameOver screen on which the share option is available.
            val game = gameScreenState.data as IGameDetail
            GameOverScreen(
                game = game,
                shareGame = { shareGameResult(context, game) },
                modifier = modifier,
            )
        }
        is GameScreenState.DISPLAY_GAME_IN_PROGRESS<*, *> -> {
            // Display the game in progress screen if needed.
            GameInProgressScreen(
                game = gameScreenState.data as IGame,
                settings = gameScreenState.settings as ISettings,
                saveGame = viewModel::saveGame,
                modifier = modifier,
            )
        }
    }
}

/**
 * Do the actual sharing process for the given game detail.
 *
 * @param context The Android (Application) context needed to perform the sharing process.
 * @param game the [IGameDetail] of the game we want to share.
 */
private fun shareGameResult(context: Context, game: IGameDetail) {
    // Get the resources object to access the necessary resources (strings, images, etc...)
    val resources = context.resources

    // Get the number of errors that the user made during a game.
    val errorCount = game.playedLetters.filter { !it.goodLetter }.size

    // Get the character to represent a good letter
    val goodLetterChar = resources.getString(R.string.share_good_letter_char)

    // Get the character to represent a bad letter
    val badLetterChar = resources.getString(R.string.share_bad_letter_char)

    // Build the part of the string to share in place of the actual letters played. It will be a
    // string of good and bad letters chars with the same order as the good or bad letters played
    // during the game.
    val results = buildString {
        for (l in game.playedLetters) {
            append(if (l.goodLetter) goodLetterChar else badLetterChar)
        }
    }

    // Build the actual string to share. It will be composed the following way :
    // A first sentence describing if user won or lost the game
    // The list of good/bad letters chars, without the letter itself
    // The link to download the app
    val shareText = buildString {
        append(
            resources.getQuantityString(
                if (game.result) R.plurals.game_result_success_sharing else R.plurals.game_result_failure_sharing,
                errorCount,
                errorCount
            )
        )
        append(
            resources.getString(
                R.string.sharing_end_of_message,
                results,
                context.packageName
            )
        )
    }

    // Do the actual sharing
    val intent = Intent().apply{
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    ContextCompat.startActivity(context, Intent.createChooser(intent, "title"), null)
}
