package com.hangman.android.core.repositories

import com.hangman.android.core.bouding.IBackendAdapter
import com.hangman.android.core.bouding.IPreferencesAdapter
import com.hangman.android.core.bouding.IStorageAdapter

/**
 * Describe the dependencies needed by the repositories provider to be able to build the
 * requested repositories.
 */
interface IRepositoryDependencies {
    /**
     * An actual implementation of the BackendAdapter
     */
    val backendAdapter: IBackendAdapter

    /**
     * An actual implementation of the StorageAdapter
     */
    val storageAdapter: IStorageAdapter

    /**
     * An actual implementation of the PreferencesAdapter.
     */
    val preferencesAdapter: IPreferencesAdapter
}