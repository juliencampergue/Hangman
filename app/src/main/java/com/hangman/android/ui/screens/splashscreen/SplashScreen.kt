package com.hangman.android.ui.screens.splashscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hangman.android.bouding.ISplashScreenViewModel
import com.hangman.android.bounding.RequestState
import com.hangman.android.ui.screens.components.ScreenLoading
import com.hangman.android.R
import com.hangman.android.ui.screens.components.ErrorDialog
import com.hangman.android.ui.screens.components.Screens
import com.hangman.android.ui.screens.components.rememberErrorDialogState

/**
 * The Splash screen will be used to display a waiting screen to the user while we do everything
 * that needs to be done before the app can start. Mainly connect to the server and do the anonymous
 * login process.
 *
 * @param viewModel the [ISplashScreenViewModel] that will be used to access all the necessary core
 * data and actions
 * @param onUserLoggedIn the action to perform we user has successfuly logged in.
 * @param onQuitTrying the action to perform want we could not perform the necessary startup actions
 * and user don't want to retry anymore.
 * @param modifier the modifier to apply to this screen.
 */
@Composable
fun SplashScreen(
    viewModel: ISplashScreenViewModel,
    onUserLoggedIn: () -> Unit,
    onQuitTrying: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Observe the login state to know what to do
    val loginState = viewModel.loginState.observeAsState()
    // Extract the login state value to be able to properly use into the when() clause.
    val currentLoginState = loginState.value
    when(currentLoginState) {
        is RequestState.IDLE,
        is RequestState.RUNNING -> {
            // When starting or when trying to connect/login, we just display the loading progress bar.
            ScreenLoading(
                loadingText = stringResource(id = R.string.splash_loading),
                onBack = onQuitTrying
            )
        }
        is RequestState.SUCCESS -> {
            // If login is successful, we call the "onUserLoggedIn" callback. We will use a
            // LaunchedEffect to call it in order not to create infinite recompositions.
            LaunchedEffect(Unit) {
                onUserLoggedIn()
            }
        }
        is RequestState.ERROR -> {
            // If login failed, we will display the associated error message. If user retries, then
            // we will just retry the login process. If user dismisses the popup, then we just
            // quit trying.
            ErrorDialog(
                onConfirm = {viewModel.retry()},
                onDismiss = onQuitTrying,
                state = rememberErrorDialogState(Screens.SPLASH, currentLoginState.error)
            )
        }
        else -> {
            // We should never have this case. But in the off chance this ever happens, just display
            // an Unknown Error
            ErrorDialog(
                onConfirm = {viewModel.retry()},
                onDismiss = onQuitTrying,
                state = rememberErrorDialogState(Screens.SPLASH)
            )
        }
    }
}