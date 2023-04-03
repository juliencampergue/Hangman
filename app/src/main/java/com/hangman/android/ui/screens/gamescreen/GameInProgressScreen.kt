package com.hangman.android.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.stringResource
import com.hangman.android.R
import com.hangman.android.bouding.*
import com.hangman.android.ui.previewutils.DayNightModePreviews
import com.hangman.android.ui.screens.gamescreen.GameStartingAnimationsState
import com.hangman.android.ui.screens.gamescreen.rememberGameStartingAnimationsState
import com.hangman.android.ui.screens.shareableuicomponents.*
import com.hangman.android.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The duration of the game start animation in milliseconds.
 */
const val ANIMATION_DURATION_MILLIS = 1000

/**
 * The delay between two updates of the playtime value in milliseconds.
 */
const val PLAYTIME_UPDATE_DELAY = 100L

/**
 * The composable that will act as the entry point for the game in progress screen.
 * The game in progress screen will be responsible for the game not started screen as well as the
 * game in progress screen itself.
 *
 * @param game The [IGame] instance representing the game to play/display
 * @param settings The current [ISettings] to be applied to the game.
 * @param saveGame The action to perform when the game is over and needs to be saved permanently.
 * @param modifier The modifier to apply to the screen (Not Started and In Progress alike).
 */
@Composable
fun GameInProgressScreen(
    game: IGame,
    settings: ISettings,
    saveGame: (IGameDetail) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Get the word from the game. Transform it to match the requirements.
    // TODO : Should be transformed elsewhere (into the game?). No reason why this should be done
    // TODO : by the "final" client especially because it is important for letters comparison during
    // TODO : play.
    val word = game.currentWord.word.uppercase()

    // Collect necessary states to compute our screen's state.
    val gameState by game.gameState.collectAsState()
    val score by game.currentScore.collectAsState()
    val playedLetters by game.playedLetters.collectAsState()
    // Compute (or remember) the current state of this screen.
    val hangmanDrawingState = rememberHangmanDrawingState(
        score = score,
        maxScore = game.maxScore,
        displayPlaceHolder = true
    )

    // If the game is over, we will request it to be saved. Once saved, this screen should leave the
    // composition, replaced by the game over screen.
    if (gameState == IGame.GameState.OVER_SUCCESS || gameState == IGame.GameState.OVER_FAILURE) {
        saveGame(game.getGameDetail())
    }

    // shouldStartAnimation should be true when we are still in not_started state at first
    // composition (because it is remembered). It is a mutableState to be able to trigger
    // recomposition whenever its value change, ie : when animation has already been started and
    // should not be anymore.
    var shouldStartAnimation by remember {mutableStateOf(gameState == IGame.GameState.NOT_STARTED)}
    // compute or remember the animation object to use to animate the transition between game not
    // started and game in progress screens.
    val animation = rememberGameStartingAnimationsState()

    if (shouldStartAnimation || animation.isRunning) {
        // We will do the following, whether animation is running, or game is still not started.

        if (gameState != IGame.GameState.NOT_STARTED) {
            // If game is started, we are either already running the animation, or it should be
            // started.

            // The LaunchedEffect will start the animation into a coroutine. The animation
            // key will prevent it from starting it multiple times (when it is already running)
            LaunchedEffect(key1 = animation) {
                // Start the actual animation
                animation.StartAnimations(this)
                // Declare that the animation has already been fired once. This should trigger a
                // recomposition. Now we will only enter the if (shouldStartAnimation || animation.isRunning)
                // while the animation is running, until it stops.
                shouldStartAnimation = false
            }
        }

        /*
         * INFO :
         * GamePlayingScreen needs to be put first onto the screen, because otherwise,
         * it might catch GameNotStartedScreen inputs (like clicks) because it would then
         * be placed "on top" of the other screen. As only the GameNotStartedScreen will
         * need to receive user inputs (the GamePlayingScreen will be displayed alone after
         * start animation), we need it to catch any and all user inputs.
         */
        // Display both screens. We pass the animation object in order for screens to display *
        // properly depending on the current state of the animation.
        GamePlayingScreen(
            word = word,
            time = null,
            hangmanDrawingState = hangmanDrawingState,
            playedLetters = playedLetters,
            //As this composition will only be added until the animation finishes and the game is
            // playing, we won't allow the user to click in any letter
            onLetterPlayed = {},
            modifier = modifier.fillMaxSize(),
            gameStartingAnimationsState = animation,
        )
        GameNotStartedScreen(
            word = word,
            onStart = game::start,
            // We apply the same modifier to both screens, because both will be displayed in exactly
            // the same space, and should thus respond to the same constraints.
            modifier = modifier.fillMaxSize(),
            gameStartingAnimationsState = animation,
        )
    } else {
        // When here, it means that the game has been started, and the start animation has ended.
        // So we will only need to display the game in progress screen.

        // remember the current play time. It will be updated often enough, no need to fetch it
        // again in case of out of sync recomposition.
        // Use a MutableState in order to trigger a recomposition whenever the play time changes.
        var playTime by remember { mutableStateOf(0L) }
        if (settings.displayTimer) {
            // No need to launch effect if timer shouldn't be displayed.

            // Launch a coroutine to update playTime at regular intervals
            LaunchedEffect(gameState) {
                // As long as the game is in progress, we will update the playTime every
                // PLAYTIME_UPDATE_DELAY ms.
                while (gameState == IGame.GameState.PLAYING) {
                    delay(PLAYTIME_UPDATE_DELAY)
                    playTime = game.getPlayTime()
                }
            }
        }
        // Display the actual playing screen. This game, the letters are clickable and playable.
        GamePlayingScreen(
            word = word,
            time = if (settings.displayTimer) playTime else null,
            hangmanDrawingState = hangmanDrawingState,
            playedLetters = playedLetters,
            onLetterPlayed = game::playLetter,
            modifier = modifier.fillMaxSize(),
        )
    }
}

/**
 * The composable to display the game not started screen.
 *
 * @param word The word to find during the game.
 * @param onStart The action to perform when user presses the "Start" Button.
 * @param modifier The modifier to apply to this screen.
 * @param gameStartingAnimationsState The current state of the start game animation.
 */
@Composable
fun GameNotStartedScreen(
    word: String,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
    gameStartingAnimationsState: GameStartingAnimationsState = rememberGameStartingAnimationsState(),
) {
    // Extract the offset from the animation state. This offset will be applied to the word to make
    // it move from its starting position into the game not started screen to its end position.
    val offset = gameStartingAnimationsState.relativeOffset
    // Extract the game not started screen visibility from the animation state.
    val visibility by remember(gameStartingAnimationsState) {
        gameStartingAnimationsState.gameNotStartedScreenVisibilityState
    }

    // The screen is a column with the word to find displayed first (with undescore instead of the
    // actual letters), then the Start button is displayed below. All this will be centered in the
    // Column.
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        // Display the Word as First item of the column. We need to get its current position which
        // will be used as the starting point for the game start animation.
        HangmanWordComposable(
            word,
            emptySet(),
            Modifier
                // We use .onGloballyPositioned to get current Word Position and set it inside the
                // animation. See https://stackoverflow.com/questions/67502807/how-do-we-get-the-position-size-of-a-composable-in-a-screen
                .onGloballyPositioned {
                    gameStartingAnimationsState.startWordPosition = it.positionInRoot()
                }
                // Then, apply the offset computed in the animation to move the word on the screen
                // towards its final position.
                .offset { offset }
        )
        Spacer(modifier = Modifier.height(hugeSpacing))
        StartGameButton(onStart = onStart, Modifier.alpha(visibility))
    }
}

/**
 * The Composable to use as the button to start the game in the game not started screen.
 *
 * @param onStart The action to perform when the button is clicked.
 * @param modifier The modifier to apply to this button.
 */
@Composable
fun StartGameButton(onStart: () -> Unit, modifier: Modifier = Modifier) {
    HangmanFilledButton(
        onClick = {onStart()},
        text = { Text(text = stringResource(id = R.string.game_screen_start_game)) },
        modifier = modifier,
    )
}

/**
 * The composable used to display the actual game in progress screen, the one displayed when user
 * is currently playing.
 *
 * @param word The word to find
 * @param time The current play time. If null, the play time will not be displayed at all.
 * @param hangmanDrawingState The state of the hangman's drawing, allowing us to display the proper
 * drawable depending on the current score.
 * @param playedLetters The list of letters that were already played during this game.
 * @param onLetterPlayed The action to perform when user presses a letter to play.
 * @param modifier The modifier to apply to this screen.
 * @param gameStartingAnimationsState The current state of the start game animation. Or null if no
 * animation is to be applied
 */
@Composable
fun GamePlayingScreen(
    word: String,
    time: Long?,
    hangmanDrawingState: HangmanDrawingState,
    playedLetters: List<ILetter>,
    onLetterPlayed: (Char) -> Unit,
    modifier: Modifier = Modifier,
    gameStartingAnimationsState: GameStartingAnimationsState? = null,
) {
    // Extract the visibility of this screen from the animation state if it has changed.
    val visibility by remember (gameStartingAnimationsState) {
        gameStartingAnimationsState?.gamePlayingScreenVisibilityState ?: mutableStateOf(1f)
    }

    // We use a scaffold because we need some things to be displayed at the top of the screen, some
    // things to be displayed at the bottom, and the rest of the screen should take up the remaining
    // space in between.
    Scaffold(
        // Apply current animation's visibility if applicable.
        modifier = modifier.alpha(visibility),
        topBar = {
            HangmanTopBar() {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Display the word in the screen's top bar. This will be the end position for
                    // the word in the game start animation, which is why we will need to get its
                    // current position.
                    HangmanWordComposable(
                        word,
                        // Set the played letters. Only those will be displayed, the rest will be
                        // replaced by underscores.
                        playedLetters.map {it.letter}.toSet(),
                        Modifier
                            .align(Alignment.Center)
                            // We use .onGloballyPositioned to get current Word Position and set it inside
                            // the animation
                            // See https://stackoverflow.com/questions/67502807/how-do-we-get-the-position-size-of-a-composable-in-a-screen
                            .onGloballyPositioned {
                                gameStartingAnimationsState?.endPosition = it.positionInRoot()
                            }
                    )

                    // Display the playtime if needed. If null, the play time composable will be
                    // removed from the composition.
                    if (time != null) {
                        HangmanPlayedTime(
                            time = time,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        },
        // The bottom bar will host the keyboard that the user will use to play the game.
        bottomBar = {
            HangmanKeyboardComposable(
                playedLetters,
                onLetterPlayed,
            )
        }
    ) {
        // Finally, in between both bars, we will display the rest of the content. ie : The hangman's
        // drawing indicating the current score.
        // The given padding will represent the space taken by both top and bottom bars.
        Box(Modifier.fillMaxSize().padding(it)) {
            HangmanDrawing(
                state = hangmanDrawingState,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

//region Previews
/*
 * --------
 * Previews
 * --------
 */
@DayNightModePreviews
@Composable
fun previewGameNotStartedScreen() {
    HangmanTheme() {
        GameNotStartedScreen("Hello", {})
    }
}

@DayNightModePreviews
@Composable
fun previewGameNotStarted() {
    HangmanTheme() {
        GameInProgressScreen(
            game = object: IGame {
                override val minScore: Int = 0
                override val maxScore: Int = 10
                override val currentScore: StateFlow<Int> = MutableStateFlow(0)
                override val currentWord: IWord = Word("word", 1234, "hello")
                override fun start(): Boolean {TODO("Not yet implemented")}
                override val gameState: StateFlow<IGame.GameState> = MutableStateFlow(IGame.GameState.NOT_STARTED)
                override fun playLetter(letter: Char): Boolean {TODO("Not yet implemented")}
                override val playedLetters: StateFlow<List<ILetter>> = MutableStateFlow(emptyList())
                override fun getGameDetail(): IGameDetail {TODO("Not yet implemented")}
                override fun getPlayTime(): Long  = 0
            },
            saveGame = {},
            settings = object: ISettings {
                override val displayTimer: Boolean = false
            }
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGamePlayingHidingTimer() {
    HangmanTheme() {
        GameInProgressScreen(
            game = object: IGame {
                override val minScore: Int = 0
                override val maxScore: Int = 10
                override val currentScore: StateFlow<Int> = MutableStateFlow(2)
                override val currentWord: IWord = Word("word", 1234, "hello")
                override fun start(): Boolean {TODO("Not yet implemented")}
                override val gameState: StateFlow<IGame.GameState> = MutableStateFlow(IGame.GameState.PLAYING)
                override fun playLetter(letter: Char): Boolean {TODO("Not yet implemented")}
                override val playedLetters: StateFlow<List<ILetter>> = MutableStateFlow(listOf(Letter('H', true), Letter('X', false), Letter('Y', false)))
                override fun getGameDetail(): IGameDetail {TODO("Not yet implemented")}
                override fun getPlayTime(): Long  = 37000
            },
            saveGame = {},
            settings = object: ISettings {
                override val displayTimer: Boolean = false
            }
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGamePlayingShowingTimer() {
    HangmanTheme() {
        GameInProgressScreen(
            game = object: IGame {
                override val minScore: Int = 0
                override val maxScore: Int = 10
                override val currentScore: StateFlow<Int> = MutableStateFlow(2)
                override val currentWord: IWord = Word("word", 1234, "hello")
                override fun start(): Boolean {TODO("Not yet implemented")}
                override val gameState: StateFlow<IGame.GameState> = MutableStateFlow(IGame.GameState.PLAYING)
                override fun playLetter(letter: Char): Boolean {TODO("Not yet implemented")}
                override val playedLetters: StateFlow<List<ILetter>> = MutableStateFlow(listOf(Letter('H', true), Letter('X', false), Letter('Y', false)))
                override fun getGameDetail(): IGameDetail {TODO("Not yet implemented")}
                override fun getPlayTime(): Long  = 37000
            },
            saveGame = {},
            settings = object: ISettings {
                override val displayTimer: Boolean = true
            }
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGameSuccess() {
    HangmanTheme() {
        GameInProgressScreen(
            game = object: IGame {
                override val minScore: Int = 0
                override val maxScore: Int = 10
                override val currentScore: StateFlow<Int> = MutableStateFlow(2)
                override val currentWord: IWord = Word("word", 1234, "hello")
                override fun start(): Boolean {TODO("Not yet implemented")}
                override val gameState: StateFlow<IGame.GameState> = MutableStateFlow(IGame.GameState.OVER_SUCCESS)
                override fun playLetter(letter: Char): Boolean {TODO("Not yet implemented")}
                override val playedLetters: StateFlow<List<ILetter>> = MutableStateFlow(listOf(
                        Letter('H', true),
                        Letter('X', false),
                        Letter('Y', false),
                        Letter('E', true),
                        Letter('L', true),
                        Letter('O', true),
                    )
                )
                override fun getGameDetail(): IGameDetail {
                    return GameDetail(
                    1,
                    1,
                    Word("word", 1234, "hello"),
                    true,
                    true,
                    emptyList(),
                    17,
                    )
                }
                override fun getPlayTime(): Long  = 170000
            },
            saveGame = {},
            settings = object: ISettings {
                override val displayTimer: Boolean = false
            }
        )
    }
}

@DayNightModePreviews
@Composable
fun previewGameFailure() {
    HangmanTheme() {
        GameInProgressScreen(
            game = object: IGame {
                override val minScore: Int = 0
                override val maxScore: Int = 10
                override val currentScore: StateFlow<Int> = MutableStateFlow(10)
                override val currentWord: IWord = Word("word", 1234, "hello")
                override fun start(): Boolean {TODO("Not yet implemented")}
                override val gameState: StateFlow<IGame.GameState> = MutableStateFlow(IGame.GameState.OVER_FAILURE)
                override fun playLetter(letter: Char): Boolean {TODO("Not yet implemented")}
                override val playedLetters: StateFlow<List<ILetter>> = MutableStateFlow(listOf(
                        Letter('H', true),
                        Letter('X', false),
                        Letter('Y', false),
                        Letter('E', true),
                        Letter('L', true),
                        Letter('B', true),
                        Letter('C', true),
                        Letter('D', true),
                        Letter('F', true),
                        Letter('G', true),
                        Letter('H', true),
                        Letter('I', true),
                        Letter('J', true),
                    )
                )
                override fun getGameDetail(): IGameDetail {
                    return GameDetail(
                        1,
                        1,
                        Word("word", 1234, "hello"),
                        false,
                        true,
                        emptyList(),
                        17,
                    )
                }
                override fun getPlayTime(): Long  = 1700000
            },
            saveGame = {},
            settings = object: ISettings {
                override val displayTimer: Boolean = false
            }
        )
    }
}
//endregion Previews
