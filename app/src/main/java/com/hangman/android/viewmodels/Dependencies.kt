package com.hangman.android.viewmodels

import com.hangman.android.bouding.ICore

/**
 * Describe the dependencies needed by the ViewModelProvider to be able to build the
 * requested ViewModels.
 */
interface IViewModelDependencies {
    /**
     * An actual implementation of the Core as describing in the ICore interface.
     */
    val core: ICore
}