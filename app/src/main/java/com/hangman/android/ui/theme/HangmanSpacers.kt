package com.hangman.android.ui.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable representing a vertical blank space to add spacing between to composables vertically
 * stacked.
 *
 * @param modifier The modifier to apply to this Spacer, for exemple to change the default height.
 */
@Composable
fun HangmanVerticalSpacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(HangmanSpacers.spacerHeight))
}

/**
 * Stores some of the needed Spacer definitions
 * This is were default sizes, etc... Should be defined and fetched by spacer composables.
 */
private object HangmanSpacers {
    /**
     * The default height of a spacer.
     */
    val spacerHeight = smallSpacing
}