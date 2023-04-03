package com.hangman.android.viewmodels.impl

import androidx.lifecycle.*
import com.hangman.android.bouding.*
import com.hangman.android.bounding.DataState
import com.hangman.android.bounding.RequestState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * The ViewModel associated with the Games and game screens. both idle games and games being played.
 * It will contain all the necessary actions to handle a game.
 *
 * @param core An ICore implementation on which to make the necessary requests.
 */
class GameScreenViewModel(val core: ICore): ViewModel(), IGameScreenViewModel {
    private val _wordOfTodayState = MutableLiveData<DataState>(DataState.FETCHING)
    private val _saveGameState = MutableLiveData<RequestState>(RequestState.IDLE)

    // GameScreen will be the "home" screen, as such, this ViewModel should host the current
    // login status in order to choose what to do at startup.
    // See https://developer.android.com/jetpack/compose/navigation
    // Arguably, the "home" screen could also use the login ViewModel. This choice was made instead
    // because the needed value was very small and not at all acted upon by this ViewModel itself,
    // and the weight of another ViewModel was deemed too much for such a small use.
    override val isLoggedIn: LiveData<Boolean> = core.isLoggedIn.asLiveData(viewModelScope.coroutineContext)
    override val wordOfTodayState: LiveData<DataState> = _wordOfTodayState
    override val currentGameState: LiveData<DataState> = core.currentGame
        .map {if (it == null) DataState.NOTAVAILABLE else DataState.DATA(it) }
        .onStart { emit(DataState.FETCHING) }
        .catch { emit(DataState.ERROR(it)) }
        .asLiveData(viewModelScope.coroutineContext)
    override val todaysGameContentState: LiveData<DataState> = core.todaysGameContent
        .map {if (it == null) DataState.NOTAVAILABLE else DataState.DATA(it)}
        .onStart { emit(DataState.FETCHING) }
        .catch { emit(DataState.ERROR(it)) }
        .asLiveData(viewModelScope.coroutineContext)
    override val saveGameState: LiveData<RequestState> = _saveGameState

    init {
        // Only when we are logged in will we try to fetch the word of today.
        // There is no logout process, so logout should not happen. In the off chance it did,
        // during a refresh of the current session or something equivalent for exemple,
        // we just won't do anything, as it does not necessarily invalidate the current word,
        // and user should not be able to play anyway.
        viewModelScope.launch {
            core.isLoggedIn.collect() {
                if (it) {
                    fetchWordOfToday()
                }
            }
        }
    }

    override fun retryGetWordOfToday() {
        // Check if we are already fetching before starting the request. If already fetching, just
        // do nothing and wait for the current request to finish.
        if (wordOfTodayState.value == DataState.FETCHING) {
            return
        }

        fetchWordOfToday()
    }

    override fun saveGame(game: IGameDetail) {
        if (saveGameState.value == RequestState.RUNNING) {
            // We are already saving, wait until it is over, one way or the other.
            return
        }

        // TODO : Check that the game is finished before saving it to storage.

        // Change state to running
        _saveGameState.value = RequestState.RUNNING

        // Then, do the save. All requests are launched in the ViewModel Scope
        viewModelScope.launch {
            try {
                core.saveGame(game)
                _saveGameState.value = RequestState.SUCCESS
            } catch (e: Throwable) {
                _saveGameState.value = RequestState.ERROR(e)
            }
        }
    }

    /**
     * Do the actual fetch of today's word.
     * Nothing is returned, as the fetch state will be update directly with the word attached into
     * the state object if applicable.
     */
    private fun fetchWordOfToday() {
        // First of all, update fetch state.
        _wordOfTodayState.value = DataState.FETCHING

        // Then, do the actual fetch. All requests will be run in the ViewModel scope.
        viewModelScope.launch {
            try {
                _wordOfTodayState.value = DataState.DATA(core.getWordOfTheDay())
            } catch (e: Throwable) {
                _wordOfTodayState.value = DataState.ERROR(e)
            }
        }
    }
}