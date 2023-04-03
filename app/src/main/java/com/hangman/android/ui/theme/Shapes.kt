package com.hangman.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * Default shapes used throughout the app
 */
val HangmanShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(0.dp)
)

/**
 * Shapes used on lists and list items.
 */
val HangmanListShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(8.dp),
)

/**
 * Shapes used specificaly by the navigation items, ie: the bottom navigation bar and its items.
 */
val HangmanNavigationShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)
