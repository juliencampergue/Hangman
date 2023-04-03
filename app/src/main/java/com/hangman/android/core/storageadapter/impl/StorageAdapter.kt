package com.hangman.android.core.storageadapter.impl

import com.hangman.android.bouding.*
import com.hangman.android.core.bouding.IStorageAdapter
import com.hangman.android.core.storageadapter.internal.GameDetailsLettersCrossRef
import com.hangman.android.core.storageadapter.internal.GamesDatabase
import com.hangman.android.core.storageadapter.internal.RoomGameDetail
import com.hangman.android.core.storageadapter.internal.RoomWord

/**
 * The actual implementation of the storage adapter. This allow us to store the played games into
 * a Room database, itself stored on the user's device.
 */
class StorageAdapter(val gamesDatabase: GamesDatabase): IStorageAdapter {
    override suspend fun getPlayedGames(from: Int, size: Int): IGamesHistoryList {
        if (from < 0 || size < 0) {
            throw IllegalArgumentException("neither from nor size argument can be < 0 when calling StorageAdapter.getPlayedGames(from, size)")
        }
        val historyItems = gamesDatabase.gameHistoryDao().getHistoryItems(from = from, pageSize = size).map { item ->
            item.asModel()
        }
        return GamesHistoryList(games = historyItems, isLastGameReached = historyItems.isEmpty())
    }

    override suspend fun getGameContent(wordId: String): IGameDetail? {
        return gamesDatabase.gameDetailDao().getPopulatedGameDetailFromWordId(wordId)?.asModel()
    }

    override suspend fun getGameContent(id: Int): IGameDetail? {
        return gamesDatabase.gameDetailDao().getPopulatedGameDetail(id)?.asModel()
    }

    override suspend fun saveGame(game: IGameDetail): Int {
        // First of all, add (or update) the corresponding Word.
        gamesDatabase.wordDao().upsert(RoomWord(game.wordOfTheDay))

        // Then, insert the game details, and get the actual ID it has in the database.
        val actualGameId = gamesDatabase.gameDetailDao().insert(RoomGameDetail(game))

        // Then, insert all the played letters into the junction table.
        val crossRefs = arrayListOf<GameDetailsLettersCrossRef>()
        game.playedLetters.forEach {
            val letterId = gamesDatabase.lettersDao().getLetterId(it.letter, it.goodLetter)
            crossRefs.add(GameDetailsLettersCrossRef(gameDetailsId = actualGameId.toInt(), letterId = letterId, order=game.playedLetters.indexOf(it)))
        }
        gamesDatabase.gameDetailDao().insertGameDetailLetterCrossRefs(crossRefs = crossRefs)

        // Finally, returned the newly generated ID for the saved game.
        return actualGameId.toInt()
    }
}