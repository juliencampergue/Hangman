package com.hangman.android.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.hangman.android.R
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews

/**
 * An image, but tinted to the app's color.
 *
 * @param drawableResId the resource id of the drawable to tint and display
 * @param contentDescription the image's content description
 * @param modifier the modifier to apply to this image
 */
@Composable
fun HangmanTintedImage(
    drawableResId: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(HangmanImages.tintColor())
    )
}

/**
 * An image, but tinted with the app's placeholder color.
 *
 * @param drawableResId the resource id of the drawable to tint and display
 * @param contentDescription the image's content description
 * @param modifier the modifier to apply to this image
 */
@Composable
fun HangmanTintedPlaceholderImage(
    drawableResId: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = drawableResId),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(
            HangmanImages.tintColor().copy(alpha=HangmanImages.placeholderImageAlpha)
        )
    )
}

/**
 * Stores some of the needed images definitions
 * This is were default alpha, colors, etc... Should be defined and fetched by image composables.
 */
private object HangmanImages {
    /**
     * The alpha to apply to a placeholder image's tint
     */
    const val placeholderImageAlpha = 0.1f

    /**
     * The default color with which to tint images.
     */
    @Composable
    fun tintColor(): Color {
        return MaterialTheme.colors.onSurface
    }
}

//region Previews
/*
 * --------
 * Previews
 * --------
 */
@DayNightModeWithBackgroundPreviews
@Composable
fun previewsHangmanTintedImage() {
    HangmanTheme() {
        HangmanTintedImage(drawableResId = R.drawable.game_image_placeholder)
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewsHangmanDrawingWithScore() {
    HangmanTheme() {
        HangmanTintedPlaceholderImage(drawableResId = R.drawable.game_image_placeholder)
    }
}
//endregion