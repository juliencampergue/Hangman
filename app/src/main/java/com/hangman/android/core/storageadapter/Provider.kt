package com.hangman.android.core.storageadapter

import androidx.room.Room
import com.hangman.android.core.bouding.IStorageAdapter
import com.hangman.android.core.storageadapter.impl.StorageAdapter
import com.hangman.android.core.storageadapter.internal.GamesDatabase

/**
 * The provider for the Room Storage. This class is responsible for properly instanciating the
 * Storage Provider for this package.
 */
class StorageProvider(val dependencies: IStorageAdapterDependencies) {
    /**
     * The actual Room database that will be used to store the user's games.
     */
    private val gamesDatabase = Room.databaseBuilder(
            dependencies.applicationContext,
            GamesDatabase::class.java,
            dependencies.gamesDatabaseName)
        .createFromAsset("databases/games/prepopulate.db")
        .build()

    /**
     * The Storage Adapter provided by this package.
     */
    val storageAdapter: IStorageAdapter = StorageAdapter(gamesDatabase)
}