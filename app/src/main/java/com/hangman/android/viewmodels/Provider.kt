package com.hangman.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hangman.android.bouding.*
import com.hangman.android.viewmodels.impl.GameContentScreenViewModel
import com.hangman.android.viewmodels.impl.GameScreenViewModel
import com.hangman.android.viewmodels.impl.HistoryScreenViewModel
import com.hangman.android.viewmodels.impl.SettingsScreenViewModel
import java.lang.IllegalArgumentException

/**
 * The provider for the ViewModels. This class is responsible for properly instanciating the
 * differents ViewModels that might be needed during the life of the application.
 *
 * @param dependencies The interface describing the dependencies needed to instanciate
 * the instances provided by this package.
 */
class UiViewModelProvider(val dependencies: IViewModelDependencies) {
    private val defaultFactory = UiViewModelFactory(dependencies.core, -1)

    /**
     * Get a factory object depending on the given contentId.
     * As a matter of fact, factory object is not dependent on the contentId anymore, and you
     * should avoid passing any gameContentId
     *
     * @param gameContentId Not used anymore.
     */
    fun getFactoryFor(gameContentId: Int = -1): ViewModelProvider.Factory {
        if (gameContentId < 0) {
            return defaultFactory
        }
        return UiViewModelFactory(dependencies.core, gameContentId)
    }
}

/**
 * The ViewModelFactory responsible for instantiating the proper ViewModel class depending on the
 * needed ViewModel class.
 *
 * @param core The actual Core implementation that the ViewModels will access.
 * @param gameContentId Not used anymore.
 */
private class UiViewModelFactory(val core: ICore, val gameContentId: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
            return SplashScreenViewModel(core) as T
        } else if (modelClass.isAssignableFrom(SettingsScreenViewModel::class.java)) {
            return SettingsScreenViewModel(core) as T
        } else if (modelClass.isAssignableFrom(HistoryScreenViewModel::class.java)) {
            return HistoryScreenViewModel(core) as T
        } else if (modelClass.isAssignableFrom(GameScreenViewModel::class.java)) {
            return GameScreenViewModel(core) as T
        } else if (modelClass.isAssignableFrom(GameContentScreenViewModel::class.java)) {
            return GameContentScreenViewModel(core, gameContentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}