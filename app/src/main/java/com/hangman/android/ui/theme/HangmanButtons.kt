package com.hangman.android.ui.theme

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.hangman.android.R
import com.hangman.android.ui.previewutils.DayNightModePreviews
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * The different types of buttons that can be found throughout the app
 */
enum class ButtonType {
    /**
     * A filled button will have visible shape and a non-transparent background.
     * It will contain a text and an optional icon.
     */
    Filled,

    /**
     * A Texted button will only display a text, and won't have visible shape or background
     * It will contain a text and an optional icon.
     */
    Texted,

    /**
     * A lettered button is a kind of filled button that is made to only display a letter
     * It will only contain a letter.
     */
    Lettered,

    /**
     * A button With clickable content is an invisible kind of button, made to match its content
     * and that is used to allow a click to be outside of the content (but inside the button) and
     * still have the wanted effect on the content. Typical use would be a settings switchable item
     * that should be clickable on all the item, even outside of the switch itself.
     */
    WithClickableContent,
}

/**
 * A Filled button with the app's designs. Content is not customizable and only a text and an
 * optional icon can be added.
 *
 * @param onClick The action to perform when the button is clicked
 * @param modifier The modifier to apply to this button
 * @param enabled Is this button enabled?
 * @param text The text to display on the button
 * @param icon The icon to display on the button. Optional
 */
@Composable
fun HangmanFilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(HangmanButtons.height(ButtonType.Filled)),
        enabled = enabled,
        colors = HangmanButtons.buttonColors(ButtonType.Filled),
        contentPadding = HangmanButtons.contentPadding(ButtonType.Filled),
    ) {
        HangmanDefaultButtonContent(type=ButtonType.Filled, text=text, icon=icon)
    }
}

/**
 * A Text button with the app's designs. Content is not customizable and only a text and an
 * optional icon can be added.
 *
 * @param onClick The action to perform when the button is clicked
 * @param modifier The modifier to apply to this button
 * @param enabled Is this button enabled?
 * @param text The text to display on the button
 * @param icon The icon to display on the button. Optional
 */
@Composable
fun HangmanTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(HangmanButtons.height(ButtonType.Texted)),
        enabled = enabled,
        colors = HangmanButtons.buttonColors(ButtonType.Texted),
        contentPadding = HangmanButtons.contentPadding(ButtonType.Texted),
    ) {
        HangmanDefaultButtonContent(type=ButtonType.Texted, text=text, icon=icon)
    }
}

/**
 * The default content for a button in the app. It will basically be a button containing a text
 * and an optional icon that will be placed at the beginning of the button
 *
 * @param type the [ButtonType] of this button
 * @param text The text to display on the button
 * @param icon The icon to display on the button. Optional
 */
@Composable
private fun HangmanDefaultButtonContent(
    type: ButtonType,
    text: @Composable () -> Unit,
    icon: @Composable (() -> Unit)? = null,
) {
    if (icon != null) {
        Box(
            Modifier
                .sizeIn(maxHeight = HangmanButtons.iconSize(type))
                .padding(end = HangmanButtons.contentSpacing(type))
        ) {
            icon()
        }
    }
    text()
}

/**
 * A Letter button with the app's designs. Content is not customizable and only a letter can be
 * added. A valid argument is added to display the button differently whether the letter is valid or
 * not.
 *
 * @param onClick The action to perform when the button is clicked
 * @param modifier The modifier to apply to this button
 * @param valid Is the letter a good letter or not? Can be null, in which case the button will
 * display in a third way, corresponding to a letter that has not been played yet.
 * @param letter The letter to display on the button
 */
@Composable
fun HangmanLetterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    valid: Boolean? = null,
    letter: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(HangmanButtons.height(ButtonType.Lettered)),
        enabled = valid == null,
        colors = if (valid == null) HangmanButtons.buttonColors(ButtonType.Lettered) else HangmanButtons.validableButtonColors(valid),
        contentPadding = HangmanButtons.contentPadding(ButtonType.Lettered),
    ) {
        letter()
    }
}

/**
 * A button With clickable content is an invisible kind of button, made to match its content
 * and that is used to allow a click to be outside of the content (but inside the button) and
 * still have the wanted effect on the content. Typical use would be a settings switchable item
 * that should be clickable on all the item, even outside of the switch itself.
 *
 * @param onClick The action to perform when the button is clicked
 * @param enabled Is this button enabled?
 * @param shape The shape to use for this button. Must match the shape of the content in order to
 * display animations (like ripples) inside the actual item
 * @param modifier The modifier to apply to this button
 * @param content The actual content of this button. Can be any composable.
 */
@Composable
fun HangmanButtonWithClickableContent(
    onClick: () -> Unit,
    enabled: Boolean,
    shape: Shape = MaterialTheme.shapes.small,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = HangmanButtons.buttonColors(ButtonType.WithClickableContent),
        contentPadding = HangmanButtons.contentPadding(ButtonType.WithClickableContent),
    ) {
        content()
    }
}

/**
 * Stores some of the needed buttons definitions
 * This is were default colors, height, etc... Should be defined and fetched by button composables.
 */
private object HangmanButtons {
    /**
     * The default color for a valid button. A valid button will be used to display something won.
     */
    val validColor = Green900

    /**
     * The default color for an invalid button. An invalid button will be used to display something
     * lost.
     */
    val invalidColor = Red900

    /**
     * The default color for the content of a valid button.
     */
    val onValidColor = Color.White.copy(alpha = 0.8f)

    /**
     * The default color for the content of an invalid button.
     */
    val onInvalidColor = Color.White.copy(alpha = 0.8f)

    /**
     * The interaction source that disables the ripple effect on a button.
     */
    val noRipple = object: MutableInteractionSource {
        override val interactions: Flow<Interaction> = emptyFlow()
        override suspend fun emit(interaction: Interaction) {}
        override fun tryEmit(interaction: Interaction): Boolean = true
    }

    /**
     * Get the default height of a button depending on its type.
     *
     * @param type the [ButtonType] of the button of which we want to get the height value.
     * @return the default height for a button of the given type.
     */
    fun height(type: ButtonType): Dp {
        return when(type) {
            ButtonType.Filled,
            ButtonType.Texted,
            ButtonType.Lettered -> normalHeight
            ButtonType.WithClickableContent -> largeHeight
        }
    }

    /**
     * Get the default size of an icon that will be displayed on a button, depending on button's
     * type.
     *
     * @param type the [ButtonType] of the button on which we want to display the icon.
     * @return the default size for the icon of a button of the given type.
     */
    fun iconSize(type: ButtonType): Dp {
        return when(type) {
            ButtonType.Filled,
            ButtonType.Texted -> normalContentSize
            ButtonType.Lettered -> smallContentSize
            ButtonType.WithClickableContent -> largeContentSize
        }
    }

    /**
     * Get the default space separating content that will be displayed on a button, depending on
     * button's type.
     *
     * @param type the [ButtonType] of the button on which the content will be displayed.
     * @return the default spacing for the content of a button of the given type.
     */
    fun contentSpacing(
        type: ButtonType
    ): Dp {
        return when(type) {
            ButtonType.Filled,
            ButtonType.Texted -> normalSpacing
            ButtonType.Lettered,
            ButtonType.WithClickableContent -> noSpacing
        }
    }

    /**
     * Get the default padding that will be applied around the content that will be displayed on a
     * button, depending on button's type.
     *
     * @param type the [ButtonType] of the button on which the content will be displayed.
     * @return the default spacing for the content of a button of the given type.
     */
    fun contentPadding(
        type: ButtonType,
    ): PaddingValues {
        return when(type) {
            ButtonType.Filled,
            ButtonType.Texted ,
            ButtonType.Lettered -> PaddingValues(
                horizontal = smallSpacing,
                vertical = smallSpacing,
            )
            ButtonType.WithClickableContent -> PaddingValues(
                horizontal = noSpacing,
                vertical = noSpacing,
            )
        }
    }

    /**
     * Get the set of colors of a button, depending on whether the button is valid or not.
     * A valid button will display a "win" information (like a good letter played). An invalid
     * button will display a "lost" information.
     *
     * @param valid Is the button displaying something won like a good letter or not?
     * @return the [ButtonColors] for this button depending on its [valid] state.
     */
    @Composable
    fun validableButtonColors(valid: Boolean): ButtonColors {
        return if (valid) {
            ButtonDefaults.buttonColors(
                backgroundColor = validColor,
                contentColor = onValidColor,
                disabledBackgroundColor = validColor,
                disabledContentColor = onValidColor,
            )
        } else {
            ButtonDefaults.buttonColors(
                backgroundColor = invalidColor,
                contentColor = onInvalidColor,
                disabledBackgroundColor = invalidColor,
                disabledContentColor = onInvalidColor,
            )
        }
    }

    /**
     * Get the default set of colors of a button, depending on its type.
     *
     * @param type the [ButtonType] of the button for which we are fetching the colors.
     * @return the default [ButtonColors] for this button.
     */
    @Composable
    fun buttonColors(
        type: ButtonType,
    ): ButtonColors {
        return when(type) {
            ButtonType.Filled -> ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary,
                disabledBackgroundColor = MaterialTheme.colors.primaryVariant.copy(
                    alpha = disabledAlpha,
                ),
                disabledContentColor = MaterialTheme.colors.onPrimary.copy(
                    alpha = disabledAlpha,
                ),
            )
            ButtonType.Texted -> ButtonDefaults.textButtonColors(
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.onBackground,
                disabledContentColor = MaterialTheme.colors.onBackground.copy(
                    alpha = disabledAlpha,
                ),
            )
            ButtonType.Lettered -> ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                disabledBackgroundColor = MaterialTheme.colors.surface.copy(
                    alpha = disabledAlpha,
                ),
                disabledContentColor = MaterialTheme.colors.onSurface.copy(
                    alpha = disabledAlpha,
                ),
            )
            ButtonType.WithClickableContent -> ButtonDefaults.textButtonColors(
                backgroundColor = Color.Transparent,
                contentColor = MaterialTheme.colors.onBackground,
                disabledContentColor = MaterialTheme.colors.onBackground.copy(
                    alpha = disabledAlpha,
                ),
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
fun previewFilledButton() {
    HangmanTheme() {
        HangmanFilledButton(
            onClick = {},
            text = {Text("Filled")},
        )
    }
}

@DayNightModePreviews
@Composable
fun previewFilledButtonWithIcon() {
    HangmanTheme() {
        HangmanFilledButton(
            onClick = {},
            text = {Text("Filled")},
            icon = {Icon(
                painter = painterResource(id = R.drawable.ic_baseline_share_24),
                contentDescription = null,
            )}
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewTextButton() {
    HangmanTheme() {
        HangmanTextButton(
            onClick = {},
            text = {Text("Texted")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewTextButtonWithIcon() {
    HangmanTheme() {
        HangmanTextButton(
            onClick = {},
            text = {Text("Texted")},
            icon = {Icon(
                painter = painterResource(id = R.drawable.ic_baseline_share_24),
                contentDescription = null,
            )}
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewUnplayedLetteredButton() {
    HangmanTheme() {
        HangmanLetterButton(
            onClick = {},
            valid = null,
            letter = {Text("W")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewValidLetteredButton() {
    HangmanTheme() {
        HangmanLetterButton(
            onClick = {},
            valid = true,
            letter = {Text("H")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewInvalidLetteredButton() {
    HangmanTheme() {
        HangmanLetterButton(
            onClick = {},
            valid = false,
            letter = {Text("X")},
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewClickableWithContentButtonEnabled() {
    HangmanTheme() {
        HangmanButtonWithClickableContent(
            onClick = {},
            enabled = true,
        ) {
            Text("Clickable")
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewClickableWithContentButtonDisabled() {
    HangmanTheme() {
        HangmanButtonWithClickableContent(
            onClick = {},
            enabled = false,
        ) {
            Text("Clickable")
        }
    }
}
//endregion Previews