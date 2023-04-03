package com.hangman.android.ui.screens.gamescreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.hangman.android.ui.screens.components.ANIMATION_DURATION_MILLIS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Helper function to either compute or remember an animation state.
 *
 * @return The computed or remembered animation state.
 */
@Composable
fun rememberGameStartingAnimationsState(): GameStartingAnimationsState {
    // Don't rememberSaveable as we don't want animation to "continue" in case of screen rotation.
    return remember {GameStartingAnimationsState()}
}

/**
 * The state of the current start animation. The start animation will run when the game of the day
 * has not been started yet, and the user clicks on the "start" button of the game.
 *
 * This state allows the game in progress screen to access the current animation data, as well as
 * start the animation when needed. The animation is a one way animation as there is no way for the
 * user to stop playing a game and go back to the "idle" state.
 */
class GameStartingAnimationsState() {
    /**
     * The animatable value of the game not started visibility (alpha).
     */
    private val gameNotStartedScreenVisibility = Animatable(1f)

    /**
     * The animatable value of the game in progress visibility (alpha).
     */
    private val gamePlayingScreenVisibility = Animatable(0f)

    /**
     * The animatable value of the position of the word. It is a float between 0 and 1. 0 will
     * represent the start position and 1 the end position. Any value in between will indicate a
     * position in between start and end positions (while the animation is in progress).
     */
    private val wordPosition = Animatable(0f)

    /**
     * A [State] containing the current value of the game not started screen visibility.
     */
    val gameNotStartedScreenVisibilityState = gameNotStartedScreenVisibility.asState()

    /**
     * A [State] containing the current value of the game in progress screen visibility.
     */
    val gamePlayingScreenVisibilityState = gamePlayingScreenVisibility.asState()

    /**
     * True if the animation is currently running, false otherwise.
     */
    val isRunning
        get() = gameNotStartedScreenVisibility.isRunning
                    || gamePlayingScreenVisibility.isRunning
                    || wordPosition.isRunning

    /**
     * The start position for the word, must be absolute position in Root
     */
    var startWordPosition: Offset? = null

    /**
     * The end position for the word, must be absolute position in Root
     */
    var endPosition: Offset? = null

    /**
     * The offset as an absolute position. Meaning that this value will be at start position's
     * value if at start position, and will have start+end position value if at end position.
     * Might be more "dangerous" to use if you do not "control" the display size?
     * Already remembered during computation so no need to do it.
     */
    val absoluteOffset: IntOffset
        @Composable
        get() {
            // Don't compute if nothing changed
            // Don't rememberSaveable as positions will have change if screen rotates
            return remember(startWordPosition, endPosition, wordPosition.value) {
                val start = startWordPosition
                val end = endPosition
                val wordPos = wordPosition.value

                if (start == null) {
                    // If start position is null, no way to calculate an offset. Returns a default one
                    IntOffset(0, 0)
                } else if (end == null) {
                    // Else if end position is null, just return the start position.
                    IntOffset(start.x.roundToInt(), start.y.roundToInt())
                } else {
                    // Else, compute the actual absolute offset of the word
                    val xDiff = end.x - start.x
                    val yDiff = end.y - start.y

                    // This offset will depend on the current animation state (wordPos).
                    // If not started, wordPos == 0 and word will be at start position.
                    // If animation is ended, wordPos == 1 and word will be at end position.
                    // If animation is in progress, we'll have 0 < wordPos < 1 and word will be
                    // somewhere in between start and stop position, in proportion to wordPos.
                    IntOffset(
                        x = (start.x + (wordPos * xDiff)).roundToInt(),
                        y = (start.y + (wordPos * yDiff)).roundToInt()
                    )
                }
            }
        }

    /**
     * The offset relative to the start position. Meaning that this value will be 0 if at start
     * position, and will have the actual offset between end and start positions if at end position.
     * Its already remembered during computation so no need to do it.
     */
    val relativeOffset: IntOffset
        @Composable
        get() {
            // Don't compute if nothing changed
            // Don't rememberSaveable as positions will have change if screen rotates
            return remember(startWordPosition, endPosition, wordPosition.value) {
                val start = startWordPosition
                val end = endPosition
                val wordPos = wordPosition.value

                if (start == null || end == null) {
                    // If start or end is null, then return a null offset. No way to calculate offset.
                    IntOffset(0, 0)
                } else {
                    // Else, compute the actual offset of the word from its starting position.
                    val xDiff = end.x - start.x
                    val yDiff = end.y - start.y

                    // This offset will depend on the current animation state (wordPos).
                    // If not started, wordPos == 0 and relative offset will be 0.
                    // If animation is ended, wordPos == 1 and relative offset is the offset between
                    // the end and start positions.
                    // If animation is in progress, we'll have 0 < wordPos < 1 and word will be
                    // somewhere in between start and stop position, in proportion to wordPos.
                    IntOffset(
                        x = ((wordPos * xDiff)).roundToInt(),
                        y = ((wordPos * yDiff)).roundToInt()
                    )
                }
            }
        }

    /**
     * The function to call to actually start the animation associated to this state.
     * Note that, as this animation is one way only, it will only be effective once.
     *
     * @param scope The coroutine scope in which those animations will be launched.
     */
    fun StartAnimations(scope: CoroutineScope) {
        // There is three animations in total. The game not started screen will fade out, the game
        // in progress screen will fade in, and the word will move from its position in the game
        // not started screen to its position in the game in progress screen. But all those three
        // animations won't be run at the same time. It will actually happen in the following way :
        // The game not started screen will fade out and at the same time the word will move from
        // its starting position to its ending position. Then, when both those animations are over,
        // the game in progress screen will fade in.
        // To do that, we will start two coroutines concurrently.
        // The first will move the word, then, once this animation is over, will make the game in
        // progress fade in.
        // The second one will make the game not started screen fade out.
        // Starting them concurrently allows the fade out animation and the word move animation to
        // happen at the same time.
        // The word move and the fade in animations are part of the same coroutine, which means that
        // they will be run sequentially.
        // All animations have the same duration, but if, for some reason, the fade out animation is
        // not over when the fade in animation starts, it doesn't matter as it will just make a
        // cross fade effect. But the word needs to be at end position before the fade in animation
        // starts, which is why they are the ones that are part of the same coroutine, and not the
        // two visibility animations.
        scope.launch {
            wordPosition.animateTo(
                1f,
                animationSpec = tween(ANIMATION_DURATION_MILLIS),
            )
            gamePlayingScreenVisibility.animateTo(
                1f,
                animationSpec = tween(ANIMATION_DURATION_MILLIS),
            )
        }
        scope.launch {
            gameNotStartedScreenVisibility.animateTo(
                0f,
                animationSpec = tween(ANIMATION_DURATION_MILLIS),
            )
        }
    }
}