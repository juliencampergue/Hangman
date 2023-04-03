package com.hangman.android.ui.screens.history

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.hangman.android.bouding.IGameContentScreenViewModel
import com.hangman.android.bouding.IHistoryScreenViewModel
import com.hangman.android.ui.screens.components.ErrorDialog
import com.hangman.android.ui.screens.components.Screens
import com.hangman.android.ui.screens.components.rememberErrorDialogState

/**
 * Base entry point to display the history and/or game details screens.
 * We are using a list/detail flow, so we can have two different behaviors, as if we were using
 * fragments.
 * We can either display the history alone, and when a specific item is selected, the associated
 * detail will be displayed. A specific action will be displayed in the details "screen" to be able
 * to "unselected" the item and come back to the history list.
 * Or we can display both the history and the eventual selected item's details alongside each other.
 * The "back" action should not appear in the details "screen", and the case where no item is
 * selected will be taken into account in the details "screen" as well.
 *
 * This is a kind of shallow composable that sole purpose is to decide which composable to actually
 * display.
 *
 * @param historyViewModel The [IHistoryScreenViewModel] to use to access to history specific data
 * and actions.
 * @param detailsViewModel The [IGameContentScreenViewModel] to use to access the game details
 * specific data and actions.
 * @param onBack The action to perform when a back is pressed
 * @param showBoth True if we need to display both the history and the game details screens
 * alongside each other. False if we need to display only one at a time.
 * @param modifier The modifier to apply to the whole screen. Whether we [showBoth] or not.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryAndDetailsScreen(
    historyViewModel: IHistoryScreenViewModel,
    detailsViewModel: IGameContentScreenViewModel,
    onBack: () -> Unit,
    showBoth: Boolean,
    modifier: Modifier = Modifier,
) {
    if (showBoth) {
        // If we show both screens, we will need both states at the same time. Just observe
        // necessary states and compute associated screenStates for both history and details screens.
        val historyState by historyViewModel.gamesHistoryState.observeAsState()
        val historyScreenState = rememberHistoryScreenState(state = historyState)
        val detailState by detailsViewModel.gameContentState.observeAsState()
        val detailScreenState = rememberDetailScreenState(detailState = detailState)

        when {
            historyScreenState is HistoryScreenState.ERROR -> {
                // If there was an error while loading the history, then we won't display anything
                // other than the error dialog. It will take up the whole screen and none of the
                // history or details screens will be displayed.
                ErrorDialog(
                    onConfirm = historyViewModel::refresh,
                    onDismiss = onBack,
                    state = rememberErrorDialogState(
                        displayedInScreen = Screens.HISTORY_AND_DETAILS,
                        error = historyScreenState.error,
                    )
                )
            }
            detailScreenState is DetailScreenState.ERROR -> {
                // If there was an error while loading a specific game's details, then we won't
                // display anything other than the error dialog either. It will take up the whole
                // screen.
                // If user retries, it will try fetching the details once again. If the user
                // dismisses, we will just unselect the selected history item.
                // TODO onConfirm and onDismiss.
                ErrorDialog(
                    onConfirm = {},
                    onDismiss = {},
                    state = rememberErrorDialogState(
                        displayedInScreen = Screens.HISTORY_AND_DETAILS,
                        error = detailScreenState.error,
                    )
                )
            }
            else -> {
                // If there was no error, we start by observing some needed values. The viewModel
                // will not be passed down from here.
                val isLastGameReached by historyViewModel.isLastGameReached.observeAsState()
                val loadMoreState by historyViewModel.loadMoreState.observeAsState()
                // Then we will display the actual dual screen in the current state.
                HistoryAndDetailScreenContent(
                    historyScreenState = historyScreenState,
                    isLastGameReached = isLastGameReached,
                    detailScreenState = detailScreenState,
                    onSelectGame = detailsViewModel::onChangeGameContent,
                    onLoadMore = historyViewModel::loadMore,
                    loadMoreState = loadMoreState,
                    historyPullToRefreshState = rememberPullRefreshState(
                        refreshing = false,
                        onRefresh = historyViewModel::refresh
                    ),
                    modifier = modifier
                )
            }
        }
    } else {
        // If we only display one screen at a time, we just need to know whether an game has been
        // selected or not to know what screen to display.
        val displayDetailState = detailsViewModel.validDetailId.observeAsState()
        val displayDetail = displayDetailState.value
        if (displayDetail != null && displayDetail) {
            // If a game has been selected, then we display the associated details screen
            DetailScreen(
                viewModel = detailsViewModel,
                onBack = detailsViewModel::onClear,
                modifier = modifier,
            )
        } else {
            // Else, if nothing was selected, we will display the history screen.
            HistoryScreen(
                viewModel = historyViewModel,
                onSelectGame = detailsViewModel::onChangeGameContent,
                onLoadMore = historyViewModel::loadMore,
                onBack = onBack,
                modifier = modifier,
            )
        }
    }
}