package com.hangman.android.core

import android.content.Context
import com.hangman.android.bouding.ICore
import com.hangman.android.core.backendadapter.BackendProvider
import com.hangman.android.core.bouding.IBackendAdapter
import com.hangman.android.core.bouding.IPreferencesAdapter
import com.hangman.android.core.bouding.IStorageAdapter
import com.hangman.android.core.engines.GameEngineProvider
import com.hangman.android.core.impl.Core
import com.hangman.android.core.impl.DATASTORE_FILE_NAME
import com.hangman.android.core.impl.GAMES_DATABASE_FILE_NAME
import com.hangman.android.core.impl.IImplHandler
import com.hangman.android.core.preferencesadapter.di.IPreferencesAdapterDependencies
import com.hangman.android.core.preferencesadapter.di.PreferencesProvider
import com.hangman.android.core.repositories.IRepositoryDependencies
import com.hangman.android.core.repositories.RepositoryProvider
import com.hangman.android.core.storageadapter.IStorageAdapterDependencies
import com.hangman.android.core.storageadapter.StorageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * The provider for the Core. This class is responsible for properly instanciating the
 * Core of the application.
 *
 * @param dependencies The interface describing the dependencies needed to instanciate
 * the instances provided by this package.
 */
class CoreProvider(val dependencies: ICoreDependencies) {
    private val job = Job()
    private val coreScope = CoroutineScope(job)

    val core: ICore = Core(ImplHandler(dependencies.appContext, coreScope))
}

/**
 * The actual implementation of the IImplHandler that the Core needs to be instantiated.
 * It will basically handle all the component implementations and be responsible for providing
 * them to the Core.
 *
 * @param applicationContext An Android Application Context.
 * @param scope A coroutine scope in which to run all the core requests.
 */
private class ImplHandler(override val applicationContext: Context, scope: CoroutineScope): IImplHandler,
    IStorageAdapterDependencies, IPreferencesAdapterDependencies, IRepositoryDependencies {

    // All components and providers are declared lazy, which allows the creations to occur in
    // the right order (the order in which they are needed). If lazy were not a possibility, we
    // then would have to manually instantiate each component/provider in the right order. No
    // circular dependency is then possible because we would need each component to be instantiated
    // to be able to instantiate the other.
    private val backendProvider by lazy {BackendProvider()}
    private val storageProvider by lazy {StorageProvider(this)}
    private val preferencesProvider by lazy {PreferencesProvider(this)}
    private val repositoriesProvider by lazy {RepositoryProvider(this)}
    private val gameEngineProvider by lazy {GameEngineProvider()}

    override val gamesDatabaseName: String = GAMES_DATABASE_FILE_NAME
    override val dataStoreFileName: String = DATASTORE_FILE_NAME
    override val preferencesScope: CoroutineScope = scope
    override val backendAdapter: IBackendAdapter by lazy { backendProvider.backendAdapter }
    override val storageAdapter: IStorageAdapter by lazy { storageProvider.storageAdapter }
    override val preferencesAdapter: IPreferencesAdapter by lazy { preferencesProvider.preferencesAdapter }
    override val authRepository by lazy { repositoriesProvider.authRepository }
    override val wordOfTheDayRepository by lazy { repositoriesProvider.wordOfTheDayRepository }
    override val settingsRepository by lazy { repositoriesProvider.settingsRepository }
    override val gameRepository by lazy { repositoriesProvider.gameRepository }
    override val gameEngine by lazy { gameEngineProvider.gameEngine }
}