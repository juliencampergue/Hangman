package com.hangman.android.viewmodels

import androidx.lifecycle.*
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.ICore
import com.hangman.android.bouding.ISplashScreenViewModel
import com.hangman.android.bounding.RequestState
import com.hangman.android.ui.utils.LOGIN_TIMEOUT_MILLIS
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeoutException

/**
 * The ViewModel associated with the Splash Screen / Start of the application.
 * It will contain all the necessary actions to determinate if the application can be properly
 * started or should be stopped at startup.
 *
 * @param core An ICore implementation on which to make the necessary requests.
 */
class SplashScreenViewModel(val core: ICore): ViewModel(), ISplashScreenViewModel {
    private val _loginState = MutableLiveData<RequestState>(RequestState.RUNNING)
    override val loginState: LiveData<RequestState> = _loginState

    init {
        // Perform a login at startup
        login()
    }

    override fun retry() {
        // If a login process is already running, just do nothing and wait for it to finish.
        if (loginState.value == RequestState.RUNNING) {
            return
        }

        // Retry login
        login()
    }

    /**
     * Perform the actual login.
     */
    private fun login() {
        // First things first, change login state to RUNNING.
        // The state should be checked before calling this method to prevent calling it if a login
        // is already in progress.
        // TODO : The state check should be inside this method.
        _loginState.value = RequestState.RUNNING

        // The actual request will be run in the viewModel scope.
        viewModelScope.launch {
            try {
                var loginState: Boolean
                // Try login, but with a timeout.
                withTimeout(LOGIN_TIMEOUT_MILLIS) {
                    loginState = core.login()
                }
                // Then, update state
                if (loginState) {
                    _loginState.value = RequestState.SUCCESS
                } else {
                    // If not logged in but no error, it means login failed. Then we should inform
                    // The activity for it to inform the user and / or take proper action.
                    _loginState.value = RequestState.ERROR(UnknownError("login was not successful"))
                }
            } catch (timedOut: TimeoutException) {
                _loginState.value = RequestState.ERROR(HangmanError.NetworkError(cause = timedOut))
            } catch (timedOut: TimeoutCancellationException) {
                _loginState.value = RequestState.ERROR(HangmanError.NetworkError(cause = timedOut))
            } catch (e: Throwable) {
                _loginState.value = RequestState.ERROR(e)
            }
        }
    }
}