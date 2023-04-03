package com.hangman.android.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hangman.android.ui.previewutils.DayNightModePreviews

/**
 * A TopBar that is not a TopAppBar
 * It will have the same role in the application but is a plain composable instead of a part
 * of the Android API handling buttons and actions by itself.
 *
 * @param modifier The modifier to apply to this TopBar. It will be applied to the TopBar itself,
 * not to the content.
 * Note that the content padding inside the bar cannot be set via this modifier.
 * @param content The content of the bar itself (titles, buttons, etc...). It can be any composable.
 */
@Composable
fun HangmanTopBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.height(HangmanTopBars.minHeight),
        color = HangmanTopBars.getColor(),
        elevation = HangmanTopBars.elevation,
    ) {
        // INFO : As the Surface is really a surface modifier, any padding modifier applied to the
        // surface will in reality be applied BEFORE the surface modifier, thus adding padding
        // AROUND the surface instead of inside it. So to have padding inside the surface, we add
        // a box inside the surface and around the content to add padding to the content only.
        // But as that padding need to wrap the surface fully, the box needs to fillMaxSize. So
        // we also need to fix surface size, as otherwise, it will take all available space (ie :
        // all screen if possible).
        Box(Modifier.fillMaxSize().padding(HangmanTopBars.contentPadding)) {
            content()
        }
    }
}

/**
 * A TopBar which background color will change depending on the result.
 * It will change the contentColor as well to allow content to use the
 * proper colors on it.
 *
 * @param result Is the game won or lost.
 * @param modifier The modifier to apply to this TopBar. It will be applied to the TopBar itself,
 * not to the content.
 * Note that the content padding inside the bar cannot be set via this modifier.
 * @param content The content of the bar itself (titles, buttons, etc...). It can be any composable.
 */
@Composable
fun HangmanResultTopBar(
    result: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.height(HangmanTopBars.minHeight),
        color = if (result) MaterialTheme.colors.winningColor else MaterialTheme.colors.losingColor,
        contentColor = if (result) MaterialTheme.colors.onWinningColor else MaterialTheme.colors.onLosingColor,
        elevation = HangmanTopBars.elevation,
    ) {
        // INFO : As the Surface is really a surface modifier, any padding modifier applied to the
        // surface will in reality be applied BEFORE the surface modifier, thus adding padding
        // AROUND the surface instead of inside it. So to have padding inside the surface, we add
        // a box inside the surface and around the content to add padding to the content only.
        // But as that padding need to wrap the surface fully, the box needs to fillMaxSize. So
        // we also need to fix surface size, as otherwise, it will take all available space (ie :
        // all screen if possible).
        Box(Modifier.fillMaxSize().padding(HangmanTopBars.contentPadding)) {
            content()
        }
    }
}

/**
 * Stores some of the needed TopBar definitions
 * This is were default sizes, elevations, alphas, etc... Should be defined and fetched by TopBar
 * composables.
 */
object HangmanTopBars {
    /**
     * The minimum height of a TopBar
     */
    val minHeight = 56.dp

    /**
     * The default elevation of a TopBar
     */
    val elevation = 2.dp

    /**
     * The default padding around the content of a TopBar.
     */
    val contentPadding = smallSpacing

    /**
     * Get the default background color of a TopBar.
     *
     * @return the default background color of a TopBar.
     */
    @Composable
    fun getColor(): Color {
        return MaterialTheme.colors.primarySurface
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
fun previewHangmanTopBar() {
    HangmanTheme() {
        HangmanTopBar() {
            HangmanWordText(text = "WORD")
        }
    }
}

@DayNightModePreviews
@Composable
fun previewHangmanResultWinningTopBar() {
    HangmanTheme() {
        HangmanResultTopBar(result = true) {
            HangmanWordText(text = "WINNING")
        }
    }
}

@DayNightModePreviews
@Composable
fun previewHangmanResultLosingTopBar() {
    HangmanTheme() {
        HangmanResultTopBar(result = false) {
            HangmanWordText(text = "LOSING")
        }
    }
}
//endregion
