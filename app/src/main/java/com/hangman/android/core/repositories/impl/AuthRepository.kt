package com.hangman.android.core.repositories.impl

import com.hangman.android.core.bouding.IAuthRepository
import com.hangman.android.core.bouding.IBackendAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/**
 * The actual repository that will handle authentification process on the server
 *
 * @param backendAdapter The adapter that will allow us to access the backend (server) on which
 * to do the authentification
 * @param dispatcher The coroutine dispatcher on which the authentification process will run.
 */
class AuthRepository(private val backendAdapter: IBackendAdapter, private val dispatcher: CoroutineDispatcher = Dispatchers.IO): IAuthRepository {
    override val isLoggedIn: StateFlow<Boolean> = backendAdapter.isLoggedIn

    override suspend fun login(): Boolean {
        // Run the login request on the given dispatcher.
        return withContext(dispatcher) {
            backendAdapter.logIn()
        }
    }
}