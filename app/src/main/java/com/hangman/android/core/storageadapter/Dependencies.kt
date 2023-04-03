package com.hangman.android.core.storageadapter

import android.content.Context

/**
 * Describe the dependencies required by the Room provider to be able to build the necessary
 * Storage Adapter.
 */
interface IStorageAdapterDependencies {
    /**
     * The current application's Context
     */
    val applicationContext: Context

    /**
     * The name of the file the database will be stored to.
     */
    val gamesDatabaseName: String
}