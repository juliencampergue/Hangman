package com.hangman.android.viewmodels.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.ICore
import com.hangman.android.bouding.IGameHistoryItem
import com.hangman.android.bouding.IHistoryScreenViewModel
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState
import kotlinx.coroutines.launch

/**
 * The ViewModel associated with the History Screen.
 * It will contain all the necessary actions to access the game history properly.
 *
 * @param core An ICore implementation on which to make the necessary requests.
 */
class HistoryScreenViewModel(private val core: ICore): ViewModel(), IHistoryScreenViewModel {
    private val _gamesHistoryState = MutableLiveData<DataState>(DataState.FETCHING)
    private val _loadMoreState = MutableLiveData<RequestState>(RequestState.IDLE)
    private val _isLastGameReached = MutableLiveData<Boolean>(false)
    private var lastFetchedId: Int = -1
    private val fetchedGames = ArrayList<IGameHistoryItem>()
    private var currentRequestFromId: Int = -1

    override val gamesHistoryState: LiveData<DataState> = _gamesHistoryState
    override val loadMoreState: LiveData<RequestState> = _loadMoreState
    override val isLastGameReached: LiveData<Boolean> = _isLastGameReached


    init {
        // Load a first batch of items at startup to speed up first load when requested.
        loadItems()

        // We want to watch for todayGameContent changes for invalidation of current history list.
        // as if todayGameContent changes, it should mean that the current game has been saved.
        // As for every other requests, it will be launched in the viewModel scope to be cancelled
        // on viewModel stop.
        viewModelScope.launch {
            core.todaysGameContent.collect {
                // TODO Maybe a better way would just be to add to the list as first item if not already in the list.
                refresh()
            }
        }
    }

    override fun loadMore() {
        // First, check if we are not already loading something. In that case, just do nothing.
        if (gamesHistoryState.value == DataState.FETCHING || loadMoreState.value == RequestState.RUNNING) {
            return
        }

        loadItems(lastFetchedId)
    }

    override fun refresh() {
        // We can refresh even if we were requesting a load more. It is equivalent to doing a pull
        // to refresh when we were getting following n items to display. It should just run as
        // usually and not take the "load more" into account.
        if (gamesHistoryState.value == DataState.FETCHING) {
            return
        }

        loadItems()
    }

    /**
     * Do the actual load of items from the given ID.
     * @param from the ID of the item from which we request a load.
     * IDs > 0 should be valid/existing IDs.
     * IDs <= 0 will trigger a refresh and reload from first item.
     */
    private fun loadItems(from: Int = 0) {
        val loadMore = from > 0
        if (loadMore) {
            // We shouldn't be here if any load request is already running, whether refresh or load
            // more.
            _loadMoreState.value = RequestState.RUNNING
        } else {
            // We can be here if there was already a loadMore request running, but we shouldn't be
            // if another refresh request is running.
            _gamesHistoryState.value = DataState.FETCHING
        }

        // We will save the current "from Id" value to use inside the coroutine below to check
        // whether the request should register its result or not. There is 2 cases. We requested
        // a load (refresh or loadmore) and nothing else, in which case we should have
        // from == currentRequestFromId into coroutine after receiving results. Or we requested
        // a refresh while a loadmore was in progress. In which case the loadMore should have a
        // from != currentrequestFromId as currentRequestFromId will have been updated by the
        // following line, and the refresh should have from == currentRequestFromId as expected.
        // Firing a loadMore request while currently refreshing should not be possible due to
        // checks in corresponding methods, thus we should not have this case to take into account
        // here.
        currentRequestFromId = from

        // Launch every request in the ViewModel scope.
        viewModelScope.launch {
            try {
                val playedGames = core.getPlayedGames(from)

                if (from != currentRequestFromId) {
                    // We are currently receiving results from a request which isn't the last one
                    // fired. This might be normal if last fired request is refresh
                    if (currentRequestFromId == 0) {
                        // If we are currently receiving results of loadMore but were cancelled by a refresh request,
                        // then we just go into IDLE and don't do anything anymore. The refresh request
                        // result will take precedence.
                        _loadMoreState.value = RequestState.IDLE
                    } else {
                        // in the off chance that a loadMore request might have been fired when it
                        // should not have, just go into error state.
                        _loadMoreState.value = RequestState.ERROR(HangmanError.UnknownError("multiple load history requests were fired at the same time and got mixed up. This should never have happened. Something went wrong and synchronization might be in order to fix it..."))
                        _gamesHistoryState.value = DataState.ERROR(HangmanError.UnknownError("multiple load history requests were fired at the same time and got mixed up. This should never have happened. Something went wrong and synchronization might be in order to fix it..."))
                    }
                } else {
                    if (!playedGames.games.isEmpty()) {
                        lastFetchedId = playedGames.games.last().id
                    }
                    if (!loadMore) {
                        // If we refreshed, first clear the current list
                        fetchedGames.clear()
                    }
                    fetchedGames.addAll(playedGames.games)
                    _isLastGameReached.value = playedGames.isLastGameReached
                    // Set the gameHistoryState value whether we were refreshing or loading more.
                    // In both cases, the resulting data should be observed via this LiveData.
                    // Send a clone of the list to the observer. We don't want changes to occur
                    // without knowing what's happening.
                    _gamesHistoryState.value = DataState.DATA(fetchedGames.clone())
                    // Whether we are refreshing or loading more, there should be only one request
                    // running here. So we should be safe always setting loadMore state to success
                    // here.
                    _loadMoreState.value = RequestState.SUCCESS
                }
            } catch (e: Error) {
                if (loadMore) {
                    _loadMoreState.value = RequestState.ERROR(e)
                } else {
                    _gamesHistoryState.value = DataState.ERROR(e)
                }
            }
        }
    }
}