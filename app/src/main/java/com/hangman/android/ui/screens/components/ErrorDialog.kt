package com.hangman.android.ui.screens.components

import androidx.annotation.StringRes
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.HangmanError
import com.hangman.android.ui.previewutils.DayNightModePreviews
import com.hangman.android.ui.theme.*

/**
 * An interface describing the different available parts of any error popup.
 */
interface IErrorDialogState {
    /**
     * The String Resource ID of the error popup's title.
     */
    val titleId: Int

    /**
     * The String Resource ID of the error popup's description text.
     */
    val textId: Int

    /**
     * The String Resource ID of the error popup's confirm button's text.
     */
    val confirmTextId: Int

    /**
     * The String Resource ID of the error popup's dismiss button's text.
     */
    val dismissTextId: Int

    /**
     * Should this error be dismissable by the user?
     */
    val showDismissButton: Boolean
}

/**
 * The hoistable state of a Splash Screen error. It will allow the error popup to request all
 * necessary values to display on the associated error popup.
 */
class SplashScreenErrorDialogState(private val error: Throwable? = null): IErrorDialogState {
    override val titleId: Int
        @StringRes get() = when(error) {
            is HangmanError.NetworkError -> R.string.splash_dialogTitle_networkError
            is HangmanError.UnknownError -> R.string.splash_dialogTitle_unknownError
            else -> R.string.splash_dialogTitle_unknownError
        }

    override val textId: Int
        @StringRes get() = when (error) {
            is HangmanError.NetworkError -> R.string.splash_dialogDesc_networkError
            is HangmanError.UnknownError -> R.string.splash_dialogDesc_unknownError
            else -> R.string.splash_dialogDesc_unknownError
        }

    override val confirmTextId: Int
        @StringRes get() = R.string.splash_dialogConfirmButton

    override val dismissTextId: Int
        @StringRes get() = R.string.splash_dialogDismissButton

    override val showDismissButton: Boolean
        get() = when (error) {
            is HangmanError.NetworkError -> true
            is HangmanError.UnknownError -> false
            else -> false
        }
}

/**
 * The actual Error Dialog composable. It will uniformly display errors to the user throughout the
 * application.
 *
 * @param onConfirm The action to perform when user presses the confirm button.
 * @param onDismiss The action to perform when user presses the dismiss button.
 * @param state The error dialog state containing all the necessary elements to display.
 * @param modifier The modifier to apply to this error dialog.
 */
@Composable
fun ErrorDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    state: IErrorDialogState,
    modifier: Modifier = Modifier) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = state.titleId)) },
        text = { Text(text = stringResource(id = state.textId)) },
        confirmButton = {
            HangmanTextButton(
                onClick = onConfirm,
                text = {
                    Text(text = stringResource(id = state.confirmTextId))
                },
            )
        },
        dismissButton = {
            if (state.showDismissButton) {
                HangmanTextButton(
                    onClick = onDismiss,
                    text = {
                        Text(text = stringResource(id = state.dismissTextId))
                    },
                )
            }
        }
    )
}

/**
 * Helper function to compute or remember the error dialog state depending on which screen it will
 * be displayed in.
 *
 * @param displayedInScreen The [Screen] in which this error will be displayed.
 * @param error The actual error.
 */
@Composable
fun rememberErrorDialogState(displayedInScreen: Screens? = null, error: Throwable? = null): IErrorDialogState = remember(displayedInScreen, error) {
    when(displayedInScreen) {
        // TODO : Use different types of error where applicable.
        Screens.SPLASH -> SplashScreenErrorDialogState(error)
        else -> SplashScreenErrorDialogState(error)
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
fun previewSplashErrorDialog() {
    HangmanTheme() {
        ErrorDialog(
            onConfirm = {},
            onDismiss = {},
            state = SplashScreenErrorDialogState(HangmanError.NetworkError()),
        )
    }
}
//endregion Previews
