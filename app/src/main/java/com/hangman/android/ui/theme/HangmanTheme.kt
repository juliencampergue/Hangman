package com.hangman.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The colors to use when the device is in light mode.
 */
private val LightColors = lightColors(
    primary = LightBlue300,
    primaryVariant = LightBlueDark,
    onPrimary = Color.Black,
    secondary = LightBlue300,
    secondaryVariant = LightBlueDark,
    onSecondary = Color.Black,
    error = Red800,
    background = VeryLightGray,
    onError = Color.White
)

/**
 * The colors to use when the device is in dark mode.
 */
private val DarkColors = darkColors(
    primary = LightBlue300,
    primaryVariant = LightBlueDark,
    onPrimary = Color.Black,
    secondary = LightBlue300,
    onSecondary = Color.Black,
    error = Red100,
    onError = Red800
)

/**
 * The color to use as a background on items showing an information about something won (a good
 * letter played, a game won, etc...)
 */
val Colors.winningColor: Color
    get() = Green900

/**
 * The color to use on foreground items displayed over a winning colored item
 */
val Colors.onWinningColor: Color
    get() = Color.White.copy(alpha = 0.8f)

/**
 * The color to use as a background on items showing an information about something lost (a bad
 * letter played, a game lost, etc...)
 */
val Colors.losingColor: Color
    get() = Red900

/**
 * The color to use on foreground items displayed over a losing colored item
 */
val Colors.onLosingColor: Color
    get() = Color.White.copy(alpha = 0.8f)

/*
 * INFO : Status Bar Color is still defined in themes.xml file, with xml colors only available
 */
/**
 * A Material Theme customized for the Hangman Application.
 *
 * @param darkTheme Should we use dark or light theme.
 * @param content The actual composable to be themed with this theme.
 */
@Composable
fun HangmanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        shapes = HangmanShapes,
        content = content
    )
}