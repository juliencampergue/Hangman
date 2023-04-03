package com.hangman.android.core.bounding

import com.hangman.android.bouding.IGame
import com.hangman.android.bouding.IWord

/**
 * Describes the game engine methods we will need to handle the games in progress.
 */
interface IGameEngine {
    /**
     * Get the game associated to the given word, or create one if none exist.
     * @param word The word we want to get the game for.
     * @return the Game object corresponding to the given word.
     */
    fun getGameForWord(word: IWord): IGame
}