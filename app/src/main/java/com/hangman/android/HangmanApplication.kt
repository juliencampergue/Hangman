package com.hangman.android

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.hangman.android.bouding.ICore
import com.hangman.android.core.CoreProvider
import com.hangman.android.core.ICoreDependencies
import com.hangman.android.ui.IUiDependencies
import com.hangman.android.viewmodels.IViewModelDependencies
import com.hangman.android.viewmodels.UiViewModelProvider

/**
 * The actual application. Will act as the first level dependency injector.
 */
class HangmanApplication: Application(), ICoreDependencies, IViewModelDependencies, IUiDependencies {
    // Provides the core instance when needed. Declared lazy to be instantiated when needed only.
    private val coreProvider: CoreProvider by lazy {CoreProvider(this)}
    // Provides the viewModel factory when needed. Declared lazy to be instantiated only when needed.
    private val viewModelProvider: UiViewModelProvider by lazy {UiViewModelProvider(this)}

    override val appContext: Context
        get() = this

    override val core: ICore
        get() = coreProvider.core

    override fun getFactoryFor(gameId: Int): ViewModelProvider.Factory {
        return viewModelProvider.getFactoryFor(gameId)
    }
}