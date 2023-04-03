package com.hangman.android.core.repositories.impl

import com.hangman.android.bouding.IWord
import com.hangman.android.core.bouding.IBackendAdapter
import com.hangman.android.core.bouding.IWordOfTheDayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The repository that will handle the needed Word related requests.
 *
 * @param backendAdapter The adapter to communicate with the backend (server).
 * @param dispatcher The coroutines dispatcher on which the needed requests will run.
 */
class WordOfTheDayRepository(private val backendAdapter: IBackendAdapter, private val dispatcher: CoroutineDispatcher = Dispatchers.IO): IWordOfTheDayRepository {
    override suspend fun getWordOfToday(): IWord {
        return withContext(dispatcher) {
            backendAdapter.getWordOfToday()
        }
    }
}