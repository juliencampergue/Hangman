package com.hangman.android.core.storageadapter.internal

import androidx.room.RoomDatabase
import androidx.room.Database

/**
 * The current Room database that will store the player's games.
 *
 * Il will contain the played Words and the history of played games with associated game details,
 * played letters, etc...
 */
@Database(
    version = 1,
    entities = [RoomWord::class, RoomLetter::class, RoomGameDetail::class, GameDetailsLettersCrossRef::class]
)
abstract class GamesDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun gameHistoryDao(): GameHistoryDao
    abstract fun gameDetailDao(): GameDetailDao
    abstract fun lettersDao(): LettersDao
}