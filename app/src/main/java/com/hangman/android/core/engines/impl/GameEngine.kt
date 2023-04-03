package com.hangman.android.core.engines.impl

import com.hangman.android.bouding.IGame
import com.hangman.android.bouding.IWord
import com.hangman.android.core.bounding.IGameEngine
import com.hangman.android.core.engines.internal.Game

/**
 * The default minimum score for a game. It correspond to the starting score for a game.
 */
const val MIN_SCORE = 0

/**
 * The default maximum score for a game. It corresponds to the score at which the game will be lost.
 */
const val MAX_SCORE = 11

/**
 * The actual implementation of the game engine. This allows us to handle the games creation and
 * retrieval if a game is already created.
 */
class GameEngine(): IGameEngine {
    private val createdGames = HashMap<String, IGame>()
    private val monitor = Object()

    override fun getGameForWord(word: IWord): IGame {
        // We will synchronize game creation in order to avoid creating multiple games on the
        // same word. Only one game per word is allowed.
        synchronized(monitor) {
            // Try retrieving the game for this word if it already exists.
            var game = createdGames[word.id]
            // If not, create and save a new game.
            if (game == null) {
                game = Game(minScore = MIN_SCORE, maxScore = MAX_SCORE, word = word)
                createdGames.put(word.id, game)
            }
            return game
        }
    }
}