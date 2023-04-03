package com.hangman.android.core.engines

import com.hangman.android.core.bounding.IGameEngine
import com.hangman.android.core.engines.impl.GameEngine

/**
 * The provider for the Game Engine. This class is responsible for properly instanciating the
 * Game Engine implemented in this package.
 */
class GameEngineProvider {
    /**
     * The Game Engine provided by this package.
     */
    val gameEngine: IGameEngine = GameEngine()
}