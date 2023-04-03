package com.hangman.android.core.repositories.impl

import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.IGameDetail
import com.hangman.android.bouding.IGamesHistoryList
import com.hangman.android.core.bouding.IGameRepository
import com.hangman.android.core.bouding.IStorageAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The repository that will handle all the game related actions.
 *
 * @param storageAdapter the adapter that will give us access to the storage solution.
 * @param dispatcher The coroutine dispatcher on which will run the needed requests.
 */
class GameRepository(val storageAdapter: IStorageAdapter, val dispatcher: CoroutineDispatcher = Dispatchers.Default): IGameRepository {
    override suspend fun getPlayedGames(from: Int, size: Int): IGamesHistoryList {
        return withContext(dispatcher) {
            storageAdapter.getPlayedGames(from = from, size = size)
        }
    }

    override suspend fun saveGame(game: IGameDetail): Int {
        return withContext(dispatcher) {
            storageAdapter.saveGame(game)
        }
    }

    override suspend fun getGameContent(wordId: String): IGameDetail? {
        return withContext(dispatcher) {
            storageAdapter.getGameContent(wordId = wordId)
        }
    }

    override suspend fun getGameContent(gameId: Int): IGameDetail {
        return withContext(dispatcher) {
            storageAdapter.getGameContent(gameId) ?: throw HangmanError.UnavailableGameDetailError("game detail for game $gameId was not found in storage")
        }
    }
}