package com.hangman.android.viewmodels.impl

import androidx.lifecycle.*
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.ICore
import com.hangman.android.bouding.IGameContentScreenViewModel
import com.hangman.android.bounding.DataState
import kotlinx.coroutines.launch

/**
 * The ViewModel associated with finished games.
 * It will contain all the necessary actions to display a game.
 *
 * @param core An ICore implementation on which to make the necessary requests.
 * @param gameId Not used anymore.
 */
class GameContentScreenViewModel(private val core: ICore, private val gameId: Int): ViewModel(), IGameContentScreenViewModel {
    private val _gameContentState = MutableLiveData<DataState>(DataState.NOTAVAILABLE)
    override val gameContentState: LiveData<DataState> = _gameContentState.distinctUntilChanged()

    // The current detail id will be considered valid as long as it is >= 0.
    // We won't check the existance of the game corresponding to this ID in the database. If wanted
    // game does not exist, the request will return an error that we will simply transfert to the
    // caller.
    private var _currentGameId = MutableLiveData(-1)
    override val validDetailId: LiveData<Boolean> = _currentGameId.map {
        it >= 0
    }.distinctUntilChanged()

    /*
     * update gameId as described in
     * https://developer.android.com/guide/topics/large-screens/navigation-for-responsive-uis
     */
    override fun onChangeGameContent(gameId: Int) {
        if (gameId == _currentGameId.value) {
            // We are already handling this ID. So, nothing to do
            return
        }

        // Save the game ID before proceeding
        _currentGameId.value = gameId

        // Then, finally, get game content.
        getGameContent(gameId)
    }

    override fun onClear() {
        // To clear, we just set an invalid id as game content id.
        onChangeGameContent(-1)
    }

    override fun retry() {
        // Check if we are already fetching some game content before retrying. If so, don't do
        // anything and just wait for current fetch to finish.
        // Note that we do not check for game content id concordance as, as we are currently
        // retrying, we, by design, reuse the current game content id.
        if (_gameContentState.value == DataState.FETCHING) {
            return
        }

        getGameContent(_currentGameId.value!!)
    }

    /**
     * Do the actual fetching of the game content corresponding to the given game id.
     *
     * @param id The ID of the game from which we want to fetch the content.
     */
    private fun getGameContent(id: Int) {
        if (id < 0) {
            // If id is invalid, just set state to "not available". This is a valid value for us
            // and correspond to those times where we just have no game content to provide.
            _gameContentState.value = DataState.NOTAVAILABLE
        } else {
            // Else, do the request. But first, change state value to fetching.
            _gameContentState.value = DataState.FETCHING

            // All requests will be launched in the ViewModel scope.
            viewModelScope.launch {
                try {
                    val content = core.getGameContent(id)
                    // Now that we have the result, we will check that we have not been cancelled
                    // before updating the state. This might happen for exemple if a user pressed
                    // on another game to display before the end of the previous game content
                    // request.
                    // If we were cancelled, then we just won't do anything and wait for the
                    // up to date request to finish.
                    // If not, then we will update state and store proper values.
                    if (_currentGameId.value == id) {
                        if (content == null) {
                            _gameContentState.value = DataState.ERROR(HangmanError.DetailFetchingError("Could not retrieve data for this game : " + id))
                        } else {
                            _gameContentState.value = DataState.DATA(content)
                        }
                    }
                } catch (e: Error) {
                    _gameContentState.value = DataState.ERROR(e)
                }
            }
        }
    }
}