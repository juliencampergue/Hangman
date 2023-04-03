package com.hangman.android.core.backendadapter

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hangman.android.core.backendadapter.impl.BackendAdapter

/**
 * The Provider for the backend adapter. This class is responsible for properly instantiating
 * the backend adapter provided by this package.
 */
class BackendProvider() {

    /**
     * The backend adapter provided by this package.
     */
    val backendAdapter = BackendAdapter(Firebase.auth, Firebase.database)
}