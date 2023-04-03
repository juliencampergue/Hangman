package com.hangman.android.core.backendadapter.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hangman.android.bouding.HangmanError
import com.hangman.android.bouding.IWord
import com.hangman.android.core.backendadapter.internal.FirebaseWord
import com.hangman.android.core.bouding.IBackendAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * The name of the database we want access from the Firebase Realtime database.
 */
const val WORDS_DATABASE_NAME = "words"

/**
 * This class will define a "Firebase" version of a BackendAdapter. The BackendAdapter contract is
 * defined in the IBackendAdapter interface. This implementation allows us to implement a
 * BackendAdapter that will have access to the requested data through the Firebase services.
 *
 * @param firebaseAuth The FirebaseAuth instance to use to access Firebase Auth service.
 * @param firebaseDatabase The Firebase Realtime Database instance that we will use to access the
 * data from the Firebase RealtimeDatabase service
 */
class BackendAdapter(private val firebaseAuth: FirebaseAuth, private val firebaseDatabase: FirebaseDatabase): IBackendAdapter {
    private val wordsDatabase = firebaseDatabase.getReference(WORDS_DATABASE_NAME)

    private val _isLoggedIn = MutableStateFlow(isFirebaseLoggedIn())
    override val isLoggedIn = _isLoggedIn.asStateFlow()
    private val authStateListener = AuthStateListener { _isLoggedIn.value = isFirebaseLoggedIn() }

    init {
        // We start by registering the Auth listener in order to have updates on the login status
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun isFirebaseLoggedIn(): Boolean = firebaseAuth.currentUser != null

    /*
     * INFO :
     *
     * This function needs to be a suspendCancellableCoroutine (and not simply a suspendCoroutine)
     * in order for the coroutine's "withTimeout" clause to work. Otherwise, it just doesn't do
     * anything as this coroutine is not cancellable.
     * When using the withTimeout clause, the coroutine in itself will be cancelled upon timeout,
     * but there is no way for us to cancel the signin task in itself. So we need to check whether
     * the task is still active or not into the onCompleteListener.
     * The only way(s) to cancel the task would be to use the Tasks methods (such as
     * Tasks.await(task) or Tasks.withTimeout(task) to emulate the same behavior.
     * But in this case, the timeout would be "decided" here instead of further down the line
     * (where the "original" suspend function will be called). Where the timeout should be set
     * is indeed arguable and could easily be setup in here, but this is not the behavior chosen
     * for this application.
     * Nonetheless, we won't use either the Tasks.await, Tasks.withTimeout for timeout, nor will we
     * use the continuation.invokeOnCancellation method because we simply cannot cancel the signin
     * process ourselves this way.
     */
    override suspend fun logIn(): Boolean = suspendCancellableCoroutine { continuation ->
        firebaseAuth
            .signInAnonymously()
            .addOnCompleteListener { task ->
                if (continuation.isActive) {
                    // If the task has been successful, we will simply update the isLoggedIn StateFlow.
                    // Whether the resulting Boolean will be true or false, it should fire up notification
                    // to the observers anyway, which is the wanted behavior.
                    if (task.isSuccessful) {
                        continuation.resume(firebaseAuth.currentUser != null)
                    } else {
                        // If the task was not successful at that point, we just act as if it was a network
                        // error.
                        continuation.resumeWithException(HangmanError.NetworkError(cause = task.exception))
                    }
                }
            }
    }

    override suspend fun getWordOfToday(): IWord = suspendCancellableCoroutine { continuation ->
        // Prepare the query
        val date = SimpleDateFormat("yyyy-DDD").format(Date())
        val query = wordsDatabase.child(date)

        // Prepare the listener for when we get the query's result
        val getWordListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Extract the retrieved value from snapshot
                val snapshotWord = snapshot.getValue(FirebaseWord::class.java)
                // Transform it to be usable in the rest of the app
                val word = snapshotWord?.asModel()
                // Check validity then return the actual result.
                if (word !=  null) {
                    continuation.resume(word)
                } else {
                    continuation.resumeWithException(HangmanError.InvalidFetchedWordError(msg = "Word fetched from Firebase was invalid : $snapshotWord"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(HangmanError.WordFetchingError(msg = "Word could not be fetched from Firebase", error.toException()))
            }
        }

        // Run the actual query
        query.addListenerForSingleValueEvent(getWordListener)

        // What to do if coroutine gets cancelled
        continuation.invokeOnCancellation { query.removeEventListener(getWordListener) }
    }
}