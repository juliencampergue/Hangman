package com.hangman.android.core.backendadapter.internal

import com.google.firebase.database.IgnoreExtraProperties
import com.hangman.android.bouding.IWord
import com.hangman.android.bouding.Word

/**
 * A Firebase specific representation of a Word. It will contain all the necessary members to
 * be properly represented in the Firebase database.
 *
 * @param id The word ID. It is a String, but it does not necessarily contains the word in itself.
 * It should be unique.
 * @param date The date at which this word should be played
 * @param word the word in itself
 */
@IgnoreExtraProperties
data class FirebaseWord(val id: String? = null, val date: Long? = null, val word: String? = null) {
    /**
     * Secondary constructor for building a Firebase representation extracted from an existing
     * model instance.
     * @param word The IWord instance of which we would like to create a Firebase representation.
     */
    constructor(word: IWord): this(word.id, word.date, word.word)

    /**
     * true if current Word (id, date and content) is valid
     */
    private val isValid: Boolean
        get() = !id.isNullOrEmpty() && date != null && date > 0 && !word.isNullOrEmpty()

    /**
     * Return a model representation of the current Firebase Word. This will be used to transfer
     * a word outside of the Firebase "realm", as no other part of the application will/should have
     * any knowledge of Firebase objects.
     */
    fun asModel(): IWord? {
        return if (isValid) Word(id!!, date!!, word!!) else null
    }
}