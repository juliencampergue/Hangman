package com.hangman.android.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews

/**
 * All available types of text. Each type can have a different style, size, color, etc...
 */
enum class TextType {
    /**
     * The title of a list item
     */
    ItemTitle,

    /**
     * The subtitle of a list item. To display some additional information about a specific item.
     */
    ItemSubtitle,

    /**
     * The type to use for the word in a game.
     */
    Word,

    /**
     * The title of a section in a screen. Can also be used as text that will be displayed alone
     * on the screen. For example, an empty list description.
     */
    SectionTitle,

    /**
     * A time information, like a game played time.
     */
    Time,
}

/**
 * A text composable styled as a list item title. (see TextType for description of the types)
 *
 * @param text The text to display
 * @param modifier The modifier that will be applied to this text composable.
 */
@Composable
fun HangmanItemTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = HangmanTexts.textStyle(type = TextType.ItemTitle),
    )
}

/**
 * A text composable styled as a list item subtitle. (see TextType for description of the types)
 *
 * @param text The text to display
 * @param modifier The modifier that will be applied to this text composable.
 */
@Composable
fun HangmanItemSubtitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.alpha(HangmanTexts.subtitleAlpha),
        style = HangmanTexts.textStyle(type = TextType.ItemSubtitle)
    )
}

/**
 * A text composable styled as a time. (see TextType for description of the types)
 *
 * @param text The text to display
 * @param modifier The modifier that will be applied to this text composable.
 */
@Composable
fun HangmanTimeText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.alpha(HangmanTexts.timeAlpha),
        style = HangmanTexts.textStyle(type = TextType.Time)
    )
}

/**
 * A text composable styled as a section title. (see TextType for description of the types)
 *
 * @param text The text to display
 * @param modifier The modifier that will be applied to this text composable.
 */
@Composable
fun HangmanSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = HangmanTexts.textStyle(type = TextType.SectionTitle)
    )
}

/**
 * A text composable styled as the word of a game. (see TextType for description of the types)
 *
 * @param text The text to display
 * @param modifier The modifier that will be applied to this text composable.
 */
@Composable
fun HangmanWordText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = HangmanTexts.textStyle(type = TextType.Word),
    )
}

/**
 * Apply the list item title style to an existing composable. (see TextType for description of the types)
 *
 * @param text The text composable on which to apply the wanted style.
 */
@Composable
fun ApplyHangmanItemTitleStyle(text: @Composable () -> Unit) {
    ProvideTextStyle(
        value = HangmanTexts.textStyle(type = TextType.ItemTitle)
    ) {
        text()
    }
}

/**
 * Apply the list item subtitle style to an existing composable. (see TextType for description of the types)
 *
 * @param text The text composable on which to apply the wanted style.
 */
@Composable
fun ApplyHangmanItemSubtitleStyle(text: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha.provides(HangmanTexts.subtitleAlpha)) {
        ProvideTextStyle(
            value = HangmanTexts.textStyle(type = TextType.ItemSubtitle)
        ) {
            text()
        }
    }
}

/**
 * Apply the Word (of a game) style to an existing composable. (see TextType for description of the types)
 *
 * @param text The text composable on which to apply the wanted style.
 */
@Composable
fun ApplyHangmanWordTextStyle(
    text: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = HangmanTexts.textStyle(type = TextType.Word),
    ) {
        text()
    }
}

/**
 * Apply the section title style to an existing composable. (see TextType for description of the types)
 *
 * @param text The text composable on which to apply the wanted style.
 */
@Composable
fun ApplyHangmanSectionTitleStyle(text: @Composable () -> Unit) {
    ProvideTextStyle(
        value = HangmanTexts.textStyle(type = TextType.SectionTitle)
    ) {
        text()
    }
}

/**
 * Apply the time style to an existing composable. (see TextType for description of the types)
 *
 * @param text The text composable on which to apply the wanted style.
 */
@Composable
fun ApplyHangmanTimeStyle(text: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha.provides(HangmanTexts.timeAlpha)) {
        ProvideTextStyle(
            value = HangmanTexts.textStyle(type = TextType.Time)
        ) {
            text()
        }
    }
}

/**
 * Stores some of the needed Text style definitions
 * This is were default styles, colors, alphas, etc... Should be defined and fetched by styled texts
 * composables.
 */
private object HangmanTexts {
    /**
     * The alpha value to apply to subtitles
     */
    val subtitleAlpha = 0.5f

    /**
     * The alpha value to apply to time texts.
     */
    val timeAlpha = 0.7f

    /**
     * Get the style of a text depending on its type.
     *
     * @param type the TextType for which we want to get the style.
     * @return the corresponding TextStyle object.
     */
    @Composable
    fun textStyle(type: TextType): TextStyle {
        return when(type) {
            TextType.SectionTitle,
            TextType.ItemTitle -> MaterialTheme.typography.body1.copy()
            TextType.ItemSubtitle,
            TextType.Time -> MaterialTheme.typography.subtitle2.copy()
            TextType.Word -> MaterialTheme.typography.h5.copy()
        }
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
fun previewWord() {
    HangmanTheme() {
        // We use surface here so that Texts will have the Themed content color applied
        Surface() {
            Column() {
                HangmanWordText(text = "A _ B _ _")
                HangmanItemTitle(text = "item title")
            }
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewItemTitle() {
    HangmanTheme() {
        Surface() {
            HangmanItemTitle(
                text = "Item Title",
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewItemSubtitle() {
    HangmanTheme() {
        Surface() {
            HangmanItemSubtitle("Item Subtitle")
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewSectionTitle() {
    HangmanTheme() {
        Surface() {
            HangmanSectionTitle(
                text = "Section Title",
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewTime() {
    HangmanTheme() {
        Surface() {
            HangmanTimeText(
                text = "Time",
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewItemTitleStyle() {
    HangmanTheme() {
        Surface() {
            ApplyHangmanItemTitleStyle(
                text = { Text("Title style") },
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewItemSubtitleStyle() {
    HangmanTheme() {
        Surface() {
            ApplyHangmanItemSubtitleStyle(
                text = { Text("Subtitle style") },
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewWordTextStyle() {
    HangmanTheme() {
        Surface() {
            ApplyHangmanWordTextStyle(
                text = { Text("Word Text Style") },
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewSectionTitleStyle() {
    HangmanTheme() {
        Surface() {
            ApplyHangmanSectionTitleStyle(
                text = { Text("Section Title style") },
            )
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewTimeStyle() {
    HangmanTheme() {
        Surface() {
            ApplyHangmanTimeStyle(
                text = { Text("time style") },
            )
        }
    }
}
//endregion Previews