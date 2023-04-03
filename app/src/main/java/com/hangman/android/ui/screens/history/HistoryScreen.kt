package com.hangman.android.ui.screens.history

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.IGameHistoryItem
import com.hangman.android.bouding.IHistoryScreenViewModel
import com.hangman.android.ui.screens.components.ErrorDialog
import com.hangman.android.ui.screens.components.ScreenLoading
import com.hangman.android.ui.screens.components.Screens
import com.hangman.android.ui.screens.components.rememberErrorDialogState

/**
 * Entry point to display the history screen. This will display the history screen alone
 *
 * This is a kind of shallow composable that sole purpose is to decide which composable to actually
 * display.
 *
 * @param viewModel The [IHistoryScreenViewModel] to use to access to history specific data
 * and actions.
 * @param onSelectGame The action to perform when a game (a history item) has been selected.
 * @param onLoadMore The action to perform when we need to load more history items than what is
 * already loaded.
 * @param onBack The action to perform when a back is pressed
 * @param modifier The modifier to apply to the screen.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryScreen(
    viewModel: IHistoryScreenViewModel,
    onSelectGame: (gameId: Int) -> Unit,
    onLoadMore: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // observe both the load states and compute the screen state with it. history state will be the
    // state of first load or of a refresh, and loadmore state will be the state when loading more
    // data. Both are treated differently because we will behave differently in both cases.
    val historyState by viewModel.gamesHistoryState.observeAsState()
    val loadMoreState by viewModel.loadMoreState.observeAsState()
    val historyScreenState = rememberHistoryScreenState(state = historyState)

    when(historyScreenState) {
        is HistoryScreenState.LOADING -> {
            // If we are loading the first batch of items (or refreshing), then only display a
            // progress bar and nothing else.
            ScreenLoading(
                loadingText = stringResource(R.string.history_screen_loading),
                onBack = onBack,
            )
        }
        is HistoryScreenState.ERROR -> {
            // If there was any kind of error, then we will display the error instead of the history
            // list. If user retries, then we will refresh the list. If he dismisses, then we go
            // back to the previous screen.
            ErrorDialog(
                onConfirm = viewModel::refresh,
                onDismiss = onBack,
                state = rememberErrorDialogState(
                    displayedInScreen = Screens.HISTORY,
                    error = historyScreenState.error,
                )
            )
        }
        is HistoryScreenState.DISPLAY_HISTORY<*> -> {
            // Else, we are able to display the history. Just fetch the last game status because
            // the viewModel won't be passed down from here. Data to display will be stored into
            // the screen state.
            val isLastGameReached by viewModel.isLastGameReached.observeAsState()

            HistoryScreenContent(
                data = historyScreenState.data as List<IGameHistoryItem>,
                isLastGameReached = isLastGameReached,
                onSelectGame = onSelectGame,
                onLoadMore = onLoadMore,
                loadMoreState = loadMoreState,
                pullToRefreshState = rememberPullRefreshState(
                    refreshing = false,
                    onRefresh = viewModel::refresh
                ),
                modifier = modifier,
            )
        }
    }
}