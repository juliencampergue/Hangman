package com.hangman.android.core

import android.content.Context

/**
 * Describe the dependencies needed by the Core provider to be able to build the
 * requested Core.
 */
interface ICoreDependencies {
    /**
     * The Android application Context
     */
    val appContext: Context
}