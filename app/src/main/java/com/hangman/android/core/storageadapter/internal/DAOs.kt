package com.hangman.android.core.storageadapter.internal

import androidx.room.*

/**
 * The class describing how to access the Words from the database
 */
@Dao
abstract class WordDao {
    /**
     * Create a new Word entry in the Database.
     * If another word with the same id already exists, then this insert is ignored.
     *
     * @param word The Word we want to insert into the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(word: RoomWord)

    /**
     * Update an existing Word entry in the Database.
     * This operation is ignored is case of conflict with the database content.
     *
     * @param word The up to date word we want to save in the database. Its id must match the id of
     * an existing Word entry.
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(word: RoomWord)

    /**
     * Remove a Word entry from the database.
     *
     * @param word The word to remove. Its id must match the id of the word we want to delete. No
     * other property will be checked for Word recognition.
     */
    @Delete
    abstract suspend fun delete(word: RoomWord)

    /**
     * Remove a Word entry from the database.
     *
     * @param wordId The id of the word we want to remove.
     */
    @Transaction
    open suspend fun delete(wordId: String) {
        delete(RoomWord(id = wordId, date = 0, word = ""))
    }

    /**
     * Insert a new Word into the database if it doesn't exist yet.
     * Update the actual Word entry if a corresponding Word already exists into the database.
     *
     * @param word The word we want to either insert or update.
     */
    @Transaction
    open suspend fun upsert(word: RoomWord) {
        insert(word)
        update(word)
    }

    /**
     * Retrieve a Word by date from the database if it exists.
     *
     * @param date The date timestamp of the Word to fetch.
     * @return The corresponding Word if an entry with the specified date exists in the database,
     * or null otherwise.
     */
    @Query("SELECT * FROM word_of_the_day_table WHERE date = :date")
    abstract suspend fun getWordByDate(date: Long): RoomWord?

    /**
     * Retrieve a Word by id from the database if it exists.
     *
     * @param id The id of the Word to fetch.
     * @return The corresponding Word if an entry exists in the database, or null otherwise
     */
    @Query("SELECT * FROM word_of_the_day_table WHERE id = :id")
    abstract suspend fun getWordById(id: String): RoomWord?
}

/**
 * The class describing how to access a Letter from the database
 * The letters are pre populated into the database and are used to store the letters played during
 * a specific game. So we will only need a few accession methods, but no update or insert methods
 * will be necessary.
 */
@Dao
interface LettersDao {
    /**
     * Fetch the entry ID in the database corresponding to the given parameters. The parameters are
     * the two parts of the unique key with which a Letter is identified in the database. This key
     * will be in the form (char, boolean), where the char is the letter itself, and the boolean
     * represents the fact that the letter is part of the corresponding played Word or not.
     *
     * @param letter The char we want to fetch ID from. This is the first part of the unique key
     * representing a Letter in the database.
     * @param goodLetter Is the char part of the played Word or not. This is the second part of the
     * unique key representing a Letter in the database.
     */
    @Query("SELECT id FROM played_letters_table WHERE letter = :letter AND goodLetter = :goodLetter")
    suspend fun getLetterId(letter: Char, goodLetter: Boolean): Int
}

/**
 * The class describing how to access the game history from the database.
 */
@Dao
abstract class GameHistoryDao {
    /**
     * Get the requested number of history items from the database. This method will return the
     * first entries, ordered from newest to oldest. ie: we will fetch the n newest entries.
     *
     * @param pageSize The number of items to fetch
     * @return A list containing the requested history items. It can be empty if no such item exists.
     */
    @Transaction
    @Query("SELECT * FROM game_details_table ORDER BY id DESC LIMIT :pageSize")
    abstract suspend fun _getFirstHistoryItems(pageSize: Int = 100): List<RoomGameHistoryItem>

    /**
     * Get the requested number of history items from the database. This method will return the
     * requested entries, ordered from newest to oldest, and will only fetch entries which have
     * been inserted before the given one. ie: we will fetch the n newest entries, which are older
     * then the entry with the given ID.
     *
     * @param from The ID of the entry from which we want to fetch. All entries created after the
     * one with the given ID will be ignored.
     * @param pageSize The number of items to fetch.
     * @return A list containing the requested history items. It can be empty if no such item exists.
     */
    @Transaction
    @Query("SELECT * FROM game_details_table WHERE id < :from ORDER BY id DESC LIMIT :pageSize")
    abstract suspend fun _getHistoryItemsFrom(from: Int = -1, pageSize: Int = 100): List<RoomGameHistoryItem>

    /**
     * Get the requested number of history items from the database. This method will return the
     * requested entries, ordered from newest to oldest, and will only fetch entries which have
     * been inserted before the given one. ie: we will fetch the n newest entries, which are older
     * then the entry with the given ID.
     *
     * @param from The ID of the entry from which we want to fetch. All entries created after the
     * one with the given ID will be ignored. If this param is equal to 0 or is negative, then the
     * returned items will simply be the newest ones.
     * @param pageSize The number of items to fetch.
     * @return A list containing the requested history items. It can be empty if no such item exists.
     */
    @Transaction
    open suspend fun getHistoryItems(from: Int = -1, pageSize: Int = 100): List<RoomGameHistoryItem> {
        if (from <= 0) {
            return _getFirstHistoryItems(pageSize)
        }
        return _getHistoryItemsFrom(from, pageSize)
    }
}

/**
 * The class describing how to access the details of a specific game from the database.
 */
@Dao
abstract class GameDetailDao {
    /**
     * Create a new Game Detail entry in the database.
     * In case of conflict, this insertion will be ignored.
     *
     * @param gameDetail The gameDetail object we want to save in the database.
     * /!\ Note that id will be autogenerated upon insertion to ensure database coherence, so the ID
     * of the given gameDetail should always be 0
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(gameDetail: RoomGameDetail): Long

    /**
     * Insert cross reference entries between a game and its played letters. This is to ensure the
     * many to many relationships between both tables.
     * This operation is ignored in case of conflict.
     *
     * @param crossRefs The list of cross references, ie: the pairs of (gameDetailId, letterId)
     * representing the relationships between a game played and the associated played letters.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertGameDetailLetterCrossRefs(crossRefs: List<GameDetailsLettersCrossRef>)

    /**
     * Update an existing game details entry in the Database.
     * This operation is ignored in case of conflict.
     *
     * @param gameDetail The up to date game detail we want to save in the Database. Its ID must
     * match the ID of an existing gameDetail entry.
     *
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun update(gameDetail: RoomGameDetail)

    /**
     * Insert a game detail entry in the database if none exist with the same id, otherwise, update
     * it.
     *
     * @param gameDetail the gameDetail we want to either insert or update.
     */
    @Transaction
    open suspend fun upsert(gameDetail: RoomGameDetail): Long {
        // TODO : Temporary fix. Check (test and fix if necessary) that update does not re-erase
        // TODO : ID with the wrong value from the gameDetail. Or just don't allow upsert at all.
        // TODO : as it does not seel to have any use in our case
        val actualId = insert(gameDetail)
        update(gameDetail)
        return actualId
    }

    /**
     * Remove a game detail entry from the database.
     *
     * @param gameDetail the game detail we want to remove from the database. Only the ID will
     * be needed and must match the id of an existing game detail in the database.
     */
    @Delete
    abstract suspend fun delete(gameDetail: RoomGameDetail)

    /**
     * Remove a game detail entry from the database using its ID.
     *
     * @param gameId the ID of the game we want to remove.
     */
    @Transaction
    open suspend fun deleteFromId(gameId: Int) {
        delete(RoomGameDetail(gameId, -1, false, false, -1, ""))
    }

    // Internal functions needed to fully populate a Game Detail.
    // They should not be used outside of this class, but as they are abstract, they
    // cannot be private.
    @Query("SELECT * FROM game_details_table WHERE id = :id")
    abstract suspend fun _getGameDetails(id: Int): RoomGameDetail?
    @Query("SELECT * FROM game_details_table WHERE wordId = :wordId")
    abstract suspend fun _getGameDetails(wordId: String): RoomGameDetail?
    @Query("SELECT * FROM word_of_the_day_table WHERE id = :id")
    abstract suspend fun _getWordOfTheDay(id: String): RoomWord?
    @Query("SELECT * FROM game_details_letters_cross_ref WHERE gameDetailsId = :id")
    abstract suspend fun _getLettersCrossRef(id: Int): List<GameDetailsLettersCrossRef>
    @Query("SELECT * FROM played_letters_table WHERE id = :id")
    abstract suspend fun _getLetter(id: Int): RoomLetter?

    /**
     * Retrieve a fully populated game details from the Database.
     *
     * @param id The id of the game of which we want to retrieve the details.
     * @return A fully populated game details, or null if the game was not found in database.
     */
    @Transaction
    open suspend fun getPopulatedGameDetail(id: Int): PopulatedRoomGameDetail? {
        val gameDetail = _getGameDetails(id)
        if (gameDetail == null) {
            return null
        }

        return _getPopulatedGameDetail(gameDetail)
    }

    /**
     * Retrieve a fully populated game details from the word that was being played.
     *
     * @param wordId The id of the word that was being played during the wanted game.
     * @return A fully populated game details, or null if no game was found for this word.
     */
    @Transaction
    open suspend fun getPopulatedGameDetailFromWordId(wordId: String): PopulatedRoomGameDetail? {
        val gameDetail = _getGameDetails(wordId)
        if (gameDetail == null) {
            return null
        }
        return _getPopulatedGameDetail(gameDetail)
    }

    /**
     * Fully populate the given game details.
     *
     * @param gameDetail The Game Detail to populate.
     * @return The populated Game Detail, or null if something went wrong (for exemple, the
     * associated word wasn't found in the database).
     */
    private suspend fun _getPopulatedGameDetail(gameDetail: RoomGameDetail): PopulatedRoomGameDetail? {
        val word = _getWordOfTheDay(gameDetail.wordId)
        if (word == null) {
            return null
        }
        val gameDetailsLettersCrossRef = _getLettersCrossRef(gameDetail.id)
        val letters = arrayListOf<RoomLetter>()
        gameDetailsLettersCrossRef.sortedBy { it.order }.forEach {
            val l = _getLetter(it.letterId)
            if (l != null) {
                letters.add(l)
            }
        }
        return PopulatedRoomGameDetail(
            gameDetail = gameDetail,
            word = word,
            playedLetters = letters,
        )
    }
}