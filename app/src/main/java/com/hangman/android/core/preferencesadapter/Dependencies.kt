package com.hangman.android.core.preferencesadapter.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope

/**
 * Describe the dependencies needed by the preferences provider to be able to build the
 * requested preferences adapter.
 */
interface IPreferencesAdapterDependencies {
    /**
     * The Android application Context.
     */
    val applicationContext: Context

    /**
     * The Name of the file that will store the preferences. This should be the name only, and
     * not a full path to the File.
     */
    val dataStoreFileName: String

    /**
     * A coroutine scope in which the preferences requests will be ran.
     */
    val preferencesScope: CoroutineScope
}