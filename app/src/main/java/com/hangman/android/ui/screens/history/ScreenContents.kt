@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.hangman.android.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.GameHistoryItem
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.IGameDetail
import com.hangman.android.bouding.IGameHistoryItem
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import com.hangman.android.ui.previewutils.LandscapeOnlyPreview
import com.hangman.android.ui.screens.components.*
import com.hangman.android.ui.screens.gamescreen.GameOverScreen
import com.hangman.android.ui.theme.*
import com.hangman.android.ui.utils.LOAD_MORE_HISTORY_THRESHOLD
import java.text.SimpleDateFormat
import java.util.Date

/**
 * The composable to use as the actual content for the history screen. Whether it is displayed
 * alone or not.
 *
 * @param data list of [IGameHistoryItem] to display.
 * @param isLastGameReached boolean indicating if the last game has been reached or not. If null,
 * acts like false.
 * @param selectedGameId The ID of the currently selected game, if any.
 * @param onSelectGame The action to perform when a game is selected.
 * @param onLoadMore The action to perform when more history data should be loaded.
 * @param loadMoreState The [RequestState] of the load more request.
 * @param pullToRefreshState The state to use for the pull to refresh feature. Hoisting the
 * pullToRefreshState allows us to be able to pull to refresh from different screens using this
 * same composable.
 * @param modifier The modifier to apply to this content.
 */
@Composable
fun HistoryScreenContent(
    data: List<IGameHistoryItem>,
    isLastGameReached: Boolean?,
    selectedGameId: Int? = null,
    onSelectGame: (gameId: Int) -> Unit,
    onLoadMore: () -> Unit,
    loadMoreState: RequestState?,
    pullToRefreshState: PullRefreshState,
    modifier: Modifier = Modifier,
) {
    // The LazyListState is used to keep track of the scroll position of the list.
    val lazyListState = rememberLazyListState()
    // The listSize is used when we want to determine if we need to load more data.
    val listSize = data.size

    // The derivedStateOf function is used to compute the shouldLoadMoreData boolean based on the
    // visible items in the list and the LOAD_MORE_HISTORY_THRESHOLD constant.
    // As the visibleItemsInfo can often change during a scroll, but we are only interested to know
    // when a certain threshold has been reached, we use the derivedStateOf to only be notified
    // when this threshold is reached.
    val shouldLoadMoreData by remember(listSize){
        derivedStateOf {
            if (lazyListState.layoutInfo.visibleItemsInfo.isEmpty()) {
                // If nothing is displayed yet, don't try to load more.
                false
            } else {
                // Else, load more data if user scrolled enough and we are reaching the last
                // "LOAD_MORE_HISTORY_THRESHOLD" items of the list.
                lazyListState.layoutInfo.visibleItemsInfo.last().index > listSize - LOAD_MORE_HISTORY_THRESHOLD
            }
        }
    }
    val datePattern = stringResource(id = R.string.history_screen_date_pattern)

    if (data.isEmpty()) {
        // If there is no saved games yet, dispay the "empty" screen.
        Surface(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = stringResource(id = R.string.history_screen_empty),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    } else {
        // This Box will catch the scroll movement when the below LazyColumn will be at the top
        // and thus not consuming scroll movements. It will then update the pullToRefreshState
        // with all needed values for us to use after for the pullToRefresh progress indicator.
        Box(
            modifier
                .fillMaxSize()
                .pullRefresh(pullToRefreshState),
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(data) { item ->
                    // Only compute formatted date if not computed before. Remember it for next use.
                    val date = remember(item.date, datePattern) {
                        val d = Date(item.date)
                        val formatter = SimpleDateFormat(datePattern)
                        formatter.format(d)
                    }
                    HangmanHistoryItem(
                        onClick = { onSelectGame(item.id) },
                        selected = (selectedGameId != null && item.id == selectedGameId),
                        result = item.result,
                        word = { HangmanWordText(text = item.word) },
                        date = { Text(date) }
                    )
                }

                // If the last game is not reached yet, we will add at the end of the list, as the
                // last item, the "load more" progress indicator, or an "error" item allowing the
                // use to retry the load more if an error occured. If the last item has been reached
                // though, we will remove any of it from the composition.
                if (isLastGameReached == null || !isLastGameReached!!) {
                    when (loadMoreState) {
                        is RequestState.IDLE,
                        is RequestState.RUNNING,
                        is RequestState.SUCCESS -> {
                            item {
                                HangmanHistoryLoadingItem()
                                if (shouldLoadMoreData) {
                                    LaunchedEffect(Unit) {
                                        onLoadMore()
                                    }
                                }
                            }
                        }
                        else -> { // is ERROR
                            item {
                                HangmanHistoryErrorItem(onRetry = onLoadMore)
                            }
                        }
                    }
                }
            }
            // After displaying the list, we will display the pullToRefresh progress indicator.
            // We will use the pullRefreshIndicatorTransform modifier to let Android handle the
            // indicator scaling and translation into position. The change of color will be handled
            // by the HangmanPullRefreshProgressIndicator in itself.
            // The threshold is deemed reached when the pull to refresh progress is > 1.0, which is
            // the threshold used by the pull to refresh itself to trigger the refresh action when
            // user removes his finger from the screen.
            HangmanPullRefreshProgressIndicator(
                thresholdReached = pullToRefreshState.progress >= 1.0f,
                modifier = Modifier.pullRefreshIndicatorTransform(
                    state = pullToRefreshState,
                    scale = true
                )
            )
        }
    }
}

/**
 * The Composable to use when displaying the details of a game. Whether alone or alongside the
 * history screen.
 *
 * @param data the [IGameDetail] of the game to display.
 * @param modifier The modifier to apply to this content.
 * @param onBack The action to perform when a back action is fired.
 */
@Composable
fun DetailScreenContent(
    data: IGameDetail,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    // Details screen is just a GameOver screen but with the sharing option unavailable.
    GameOverScreen(
        game = data,
        shareGame = null,
        modifier = modifier,
        onBack = onBack,
    )
}

/**
 * The actual content composable of the screen when both history list and selected game details are
 * alongside each other.
 *
 * @param historyScreenState The actual [HistoryScreenState] of the history part of the screen.
 * @param isLastGameReached boolean indicating if the last game has been reached or not. If null,
 * acts like false.
 * @param detailScreenState The actual [DetailScreenState] of the game details part of the screen.
 * @param onSelectGame The action to perform when a game is selected on the history list.
 * @param onLoadMore The action to perform when scrolling the history list reached a predefined
 * threshold.
 * @param loadMoreState The [RequestState] of the loadMore request
 * @param historyPullToRefreshState The state to use for the pull to refresh feature of this history
 * list. Hoisting this pullToRefreshState allows us to be able to pull to refresh from different
 * screens using the same composable.
 * @param modifier the modifier to apply to this content.
 */
@Composable
fun HistoryAndDetailScreenContent(
    historyScreenState: HistoryScreenState,
    isLastGameReached: Boolean?,
    detailScreenState: DetailScreenState,
    onSelectGame: (gameId: Int) -> Unit,
    onLoadMore: () -> Unit,
    loadMoreState: RequestState?,
    historyPullToRefreshState: PullRefreshState,
    modifier: Modifier = Modifier,
) {
    // We want to display both contents side by side, which is why we use a Row.
    Row(modifier = modifier) {
        // Set weight to fix the Box relative size into the Row.
        Box(
            Modifier
                .fillMaxHeight()
                .weight(1f)) {
            when(historyScreenState) {
                is HistoryScreenState.LOADING -> {
                    // If the history is still loading, we will display the loading progress bar
                    // in place of the history screen.
                    ScreenLoading(loadingText = stringResource(id = R.string.history_screen_loading))
                }
                is HistoryScreenState.ERROR -> {
                    // This case should already have been handled at screen level, so there shouldn't
                    // be anything to do here.
                    //TODO : Nothing to do really? This case should be handled by Screen Level and not by
                    //TODO : Content Level (here)
                }
                is HistoryScreenState.DISPLAY_HISTORY<*> -> {
                    // If we could fetch the history, then we simply display the history content in
                    // this part of the screen
                    HistoryScreenContent(
                        data = historyScreenState.data as List<IGameHistoryItem>,
                        isLastGameReached = isLastGameReached,
                        // Extract the selected game's ID from the detail screen state if applicable.
                        // We do not store it elsewhere as well to prevent synchronization errors
                        // between history and details.
                        // The detail screen state is the only source of truth for the selected game
                        // id.
                        selectedGameId = if (detailScreenState is DetailScreenState.DISPLAY_DETAILS<*>) (detailScreenState.data as IGameDetail).id else null,
                        onSelectGame = onSelectGame,
                        onLoadMore = onLoadMore,
                        loadMoreState = loadMoreState,
                        pullToRefreshState = historyPullToRefreshState,
                        modifier = modifier
                    )
                }
            }
        }
        // Set weight to fix the Box relative size into the Row.
        Box(
            Modifier
                .fillMaxHeight()
                .weight(2f)) {
            when(detailScreenState) {
                is DetailScreenState.LOADING -> {
                    // If the game detail is still loading, we will display the loading progress
                    // bar in the game detail part of the screen.
                    ScreenLoading(loadingText = stringResource(id = R.string.detail_screen_loading))
                }
                is DetailScreenState.ERROR -> {
                    // This case should already have been handled at screen level, so there shouldn't
                    // be anything to do here.
                    //TODO : Nothing to do? This case should be handled by Screen Level and not by
                    //TODO : Content Level (here)
                }
                is DetailScreenState.EMPTY -> {
                    // If no game is currently selected, the game detail part of the screen will
                    // still be displayed, contrary to when only one screen is displayed at the same
                    // time. So we handle this possibility by adding a simple text as empty screen.
                    HangmanSectionTitle(
                        text= stringResource(id = R.string.detail_screen_empty),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetailScreenState.DISPLAY_DETAILS<*> -> {
                    // If we could fetch the details, then we simply display the game detail content
                    // in this part of the screen.
                    DetailScreenContent(
                        data = detailScreenState.data as IGameDetail,
                        modifier = modifier
                    )
                }

            }
        }
    }
}

/**
 * The composable to use when displaying an history list item.
 *
 * @param modifier The modifier to apply to this item.
 * @param onClick The action to perform when user clicks on this item.
 * @param selected Is this item the currently selected one.
 * @param result Has this game been won or lost
 * @param word The word of this game to be displayed on the item. Can be any composable.
 * @param data The date of this game to be displayed on the item. Can be any composable.
 */
@Composable
fun HangmanHistoryItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    selected: Boolean,
    result: Boolean,
    word: @Composable () -> Unit,
    date: @Composable () -> Unit,
) {
    HangmanMovingListItem(
        modifier = modifier,
        backgroundColor = HistoryItems.getItemColor(selected = selected),
        onClick = onClick,
    ) {
        // The item is an evently spaced row. It will have three items, the result indicator, the
        // word itself, and the game's date. Spaced evently means that the first content item will
        // be at the beginning of the list item, the last content item will be at the end of the
        // list item, and the "middle" content item will be centered in the list item.
        Row(
            modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // The result indicator is a small vertical line of the result color (whether it's been
            // won or lost) that appears at the beginning of the item.
            Surface(
                modifier = Modifier
                    .height(HistoryItems.resultIndicatorHeight)
                    .width(HistoryItems.resultIndicatorWidth),
                color = HistoryItems.getResultItemColor(result = result),
            ){
                // INFO :
                // Surface needs to contain something, otherwise, it will not be put into the
                // composition
                Spacer(Modifier.fillMaxSize())
            }
            word()
            date()
        }
    }
}

/**
 * The composable to use to display the progression of the "loadMore" request into as an history
 * list item.
 *
 * @param modifier The modifier to apply to this item.
 */
@Composable
fun HangmanHistoryLoadingItem(
    modifier: Modifier = Modifier,
) {
    // We use a fixed list item because it should not be displayed as another item of the list.
    // This indicator is not a "classical" item of the list.
    HangmanFixedListItem(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * The composable to use to display an error in the loadMore request.
 *
 * @param onRetry The action to perform if user clicks on the "retry" button.
 * @param modifier The modifier to apply to this item.
 */
@Composable
fun HangmanHistoryErrorItem(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // We use a fixed list item because it should not be displayed as another item of the list.
    // This error is not a "classical" item of the list.
    HangmanFixedListItem(modifier = modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // This item it composed of a text displaying the error and a button allowing the user
            // to retry loading more content.
            HangmanItemTitle(text = stringResource(id = R.string.history_screen_loading_error))
            HangmanVerticalSpacer()
            HangmanFilledButton(
                onClick = onRetry,
                text = { Text(text = stringResource(id = R.string.history_screen_retry)) }
            )
        }
    }
}

/**
 * Stores some of the needed history items definitions
 * This is were default height, width, etc... Should be defined and fetched by history item
 * composables.
 */
private object HistoryItems {
    /**
     * The default height of the result indicator of an history item.
     */
    val resultIndicatorHeight = normalHeight

    /**
     * The default width of the result indicator of an history item.
     */
    val resultIndicatorWidth = largeBorder

    /**
     * Get the background color of an history item depending on whether it is the currenlty selected
     * item or not.
     *
     * @param selected Is this item the currently selected one
     * @return The color of the history item's background for the given selection state.
     */
    @Composable
    fun getItemColor(selected: Boolean): Color {
        return when(selected) {
            true -> MaterialTheme.colors.primary
            false -> MaterialTheme.colors.surface
        }
    }

    /**
     * Get the color for the result indicator depending on the result itself.
     *
     * @param result The result of the displayed game. If null, indicator will be transparent.
     * @return the color in which to display the indicator for the given result.
     */
    @Composable
    fun getResultItemColor(result: Boolean?): Color {
        return when (result) {
            true -> MaterialTheme.colors.winningColor
            false -> MaterialTheme.colors.losingColor
            else -> Color.Transparent
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
fun previewHangmanFailedHistoryItem() {
    HangmanTheme() {
        HangmanHistoryItem(
            onClick = {},
            result = false,
            selected = false,
            word = { HangmanWordText(text = "Lose")},
            date = {Text("31/01")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHangmanSuccessHistoryItem() {
    HangmanTheme() {
        HangmanHistoryItem(
            onClick = {},
            result = true,
            selected = true,
            word = { HangmanWordText(text = "Win")},
            date = {Text("31/01")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHistoryScreenContent() {
    HangmanTheme() {
        HistoryScreenContent(
            data = listOf(
                GameHistoryItem(
                    1,
                    123,
                    "hello",
                    "Hello",
                    true,
                    true
                ),
                GameHistoryItem(
                    2,
                    1234,
                    "test",
                    "Test",
                    false,
                    true
                ),
                GameHistoryItem(
                    3,
                    12345,
                    "third",
                    "Third",
                    true,
                    true
                ),
                GameHistoryItem(
                    4,
                    123456,
                    "fourth",
                    "Fourth",
                    true,
                    true
                )
            ),
            isLastGameReached = true,
            selectedGameId = null,
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.IDLE,
            pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHistoryScreenContentWithSelectedItem() {
    HangmanTheme() {
        HistoryScreenContent(
            data = listOf(
                GameHistoryItem(
                    1,
                    123,
                    "hello",
                    "Hello",
                    true,
                    true
                ),
                GameHistoryItem(
                    2,
                    1234,
                    "test",
                    "Test",
                    false,
                    true
                ),
                GameHistoryItem(
                    3,
                    12345,
                    "third",
                    "Third",
                    true,
                    true
                ),
                GameHistoryItem(
                    4,
                    123456,
                    "fourth",
                    "Fourth",
                    true,
                    true
                )
            ),
            isLastGameReached = true,
            selectedGameId = 2,
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.RUNNING,
            pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHistoryScreenContentLastGameNotReached() {
    HangmanTheme() {
        HistoryScreenContent(
            data = listOf(
                GameHistoryItem(
                    1,
                    123,
                    "hello",
                    "Hello",
                    true,
                    true
                ),
                GameHistoryItem(
                    2,
                    1234,
                    "test",
                    "Test",
                    false,
                    true
                ),
                GameHistoryItem(
                    3,
                    12345,
                    "third",
                    "Third",
                    true,
                    true
                ),
                GameHistoryItem(
                    4,
                    123456,
                    "fourth",
                    "Fourth",
                    true,
                    true
                )
            ),
            isLastGameReached = false,
            selectedGameId = null,
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.IDLE,
            pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHistoryScreenContentLoadMoreError() {
    HangmanTheme() {
        HistoryScreenContent(
            data = listOf(
                GameHistoryItem(
                    1,
                    123,
                    "hello",
                    "Hello",
                    true,
                    true
                ),
                GameHistoryItem(
                    2,
                    1234,
                    "test",
                    "Test",
                    false,
                    true
                ),
                GameHistoryItem(
                    3,
                    12345,
                    "third",
                    "Third",
                    true,
                    true
                ),
                GameHistoryItem(
                    4,
                    123456,
                    "fourth",
                    "Fourth",
                    true,
                    true
                )
            ),
            isLastGameReached = false,
            selectedGameId = null,
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.ERROR(HangmanError.UnknownError("unkwownError")),
            pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHistoryEmptyScreen() {
    HangmanTheme() {
        HistoryScreenContent(
            data = emptyList(),
            isLastGameReached = false,
            selectedGameId = null,
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.SUCCESS,
            pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}

@LandscapeOnlyPreview
@Composable
fun previewHistoryAndDetailsScreen() {
    HangmanTheme() {
        HistoryAndDetailScreenContent(
            historyScreenState = rememberHistoryScreenState(state = DataState.DATA<Any>(emptyList<Any>())),
            isLastGameReached = true,
            detailScreenState = rememberDetailScreenState(detailState = DataState.NOTAVAILABLE),
            onSelectGame = {},
            onLoadMore = {},
            loadMoreState = RequestState.IDLE,
            historyPullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {}),
            modifier = Modifier,
        )
    }
}
//endregion