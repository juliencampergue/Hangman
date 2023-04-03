package com.hangman.android.ui.screens.history

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.IGameContentScreenViewModel
import com.hangman.android.bouding.IGameDetail
import com.hangman.android.ui.screens.components.ErrorDialog
import com.hangman.android.ui.screens.components.ScreenLoading
import com.hangman.android.ui.screens.components.Screens
import com.hangman.android.ui.screens.components.rememberErrorDialogState

/**
 * The composable to use to display the details screen alone.
 *
 * @param viewModel The [IGameContentScreenViewModel] used by this screen to get all necessary data
 * and perform all necessary actions.
 * @param onBack The action to perform when a back is pressed.
 * @param modifier The modifier to apply to this screen.
 */
@Composable
fun DetailScreen(
    viewModel: IGameContentScreenViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // We will catch the default back press (when pressing the phone's back button) because we
    // don't want the default behavior to occur. When displaying this screen, we are still in the
    // "History and Details" top level destination. If we let system handle the back, we will
    // navigate back from this destination instead of staying in the current destination and
    // go back to history screen by clearing the selected item, which would be the wanted behavior.
    BackHandler(onBack = onBack)

    // Get the game content loading state and compute (or remember) the detail screen state with it.
    val gameContentState by viewModel.gameContentState.observeAsState()
    val detailScreenState = rememberDetailScreenState(detailState = gameContentState)

    when(detailScreenState) {
        is DetailScreenState.LOADING -> {
            // If we should be loading, then just display a loading screen as content.
            ScreenLoading(
                loadingText = stringResource(R.string.detail_screen_loading),
                onBack = onBack,
            )
        }
        is DetailScreenState.ERROR -> {
            // If there was an error while loading the game details, display an error popup. If
            // user wants to retry, we will try fetching the same game detail again.
            ErrorDialog(
                onConfirm = viewModel::retry,
                onDismiss = onBack,
                state = rememberErrorDialogState(
                    displayedInScreen = Screens.DETAILS,
                    error = detailScreenState.error,
                )
            )
        }
        is DetailScreenState.EMPTY -> {
            // Details Screen should never be empty, as it is displayed alone. It should go back to
            // History screen instead.
            // TODO : Display error? Do back?
        }
        is DetailScreenState.DISPLAY_DETAILS<*> -> {
            // If we successfuly fetched the game details, just display the associated details
            // content
            DetailScreenContent(
                data = detailScreenState.data as IGameDetail,
                modifier = modifier,
                onBack = onBack,
            )
        }
    }
}