package com.hangman.android.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.hangman.android.ui.previewutils.DayNightModeWithBackgroundPreviews


/**
 * A basic list item to use in any kind of vertical list.
 *
 * @param elevation The elevation of this item.
 * @param modifier The modifier to apply to this item.
 * @param onClick The function to be executed when this item is clicked.
 * @param backgroundColor The background color of this item.
 * @param content The content of the item itself. Can be any composable
 */
@Composable
fun HangmanListItem(
    elevation: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit,
) {
    // A list item will be a card, to be able to handle elevation
    Card(
        modifier = modifier,
        shape = HangmanListShapes.large,
        elevation = elevation,
        backgroundColor = backgroundColor ?: HangmanListItems.color(),
    ) {
        // If the item is clickable, then we use a button with clickable content. This is to be
        // able handle click events and display click animations (ripples) on the full item and
        // not just on the content.
        if (onClick != null) {
            HangmanButtonWithClickableContent(
                onClick = onClick,
                enabled = true,
                shape = HangmanListShapes.large,
            ) {
                // The surface (here card) padding will add a padding around the surface.
                // Meaning that, for exemple, the shape of the surface will be drawn inside the padding.
                // But we also want a padding from the surface "borders", and the content. So
                // we will simply add a box, whose only job is to add padding to the content.
                Box(Modifier.padding(HangmanListItems.itemContentPadding)) {
                    content()
                }
            }
        } else {
            // The surface (here card) padding will add a padding around the surface.
            // Meaning that, for exemple, the shape of the surface will be drawn inside the padding.
            // But we also want a padding from the surface "borders", and the content. So
            // we will simply add a box, whose only job is to add padding to the content.
            Box(Modifier.padding(HangmanListItems.itemContentPadding)) {
                content()
            }
        }
    }
}

/**
 * An item for a "fixed" list. A fixed list is a list that is not dynamic and might not need
 * to be scrollable. Typical "fixed" list is the settings list.
 *
 * @param modifier The modifier to apply to this item.
 * @param onClick The action to perform when this item is clicked.
 * @param content The content of the item itself. Can be any Composable.
 */
@Composable
fun HangmanFixedListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    HangmanListItem(
        elevation = HangmanListItems.fixed_elevation,
        onClick = onClick,
        modifier = modifier
            .sizeIn(minHeight = HangmanListItems.itemHeight)
            .padding(HangmanListItems.surfacePadding),
        content = content,
    )
}

/**
 * An item for a "moving" list. A moving list is a list that is dynamic and needs to be scrolled.
 * Typical "moving" list is the game's history list.
 *
 * @param modifier the modifier to apply to this item
 * @param onClick the action to perform when this item is clicked.
 * @param backgroundColor the color of this item's background
 * @param content the actual content of this item itself. Can be any Composable.
 */
@Composable
fun HangmanMovingListItem(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color? = null,
    content: @Composable () -> Unit,
) {
    HangmanListItem(
        elevation = HangmanListItems.elevation,
        onClick = onClick,
        backgroundColor = backgroundColor,
        modifier = modifier
            .sizeIn(minHeight = HangmanListItems.itemHeight)
            .padding(HangmanListItems.surfacePadding),
        content = content,
    )
}

/**
 * The pull to refresh indicator used by any Hangman vertical list.
 * It is declared in the BoxScope, meaning that the indicator should be displayed in a Box context.
 * This allows some alignments to be done, which are part of the default indicator's style.
 *
 * @param thresholdReached When pulling to refresh, there is a threshold that needs to be reached
 * before the refresh is effectively started. Knowing that that threshold has been reached allows
 * for this indicator to behave/display differently when refresh has started and before.
 * @param modifier The modifier to apply to this indicator.
 */
// As the PullRefresh indicator should be inside the PullRefresh Box,
// We will declare it as a BoxScope extension. This allows us to add
// the Alignment.TopCenter aligment, which is a part of this item's
// way of being displayed.
@Composable
fun BoxScope.HangmanPullRefreshProgressIndicator(
    thresholdReached: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .align(Alignment.TopCenter),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = HangmanListItems.pullRefreshIndicatorColor(thresholdReached),
        )
    }
}

/**
 * A Grid that will take all the available horizontal space and be vertically scrollable.
 * The grid's height will depend on the number of cells to display, and the number of cells to
 * display will depend on the available width. Available width can be restricted via the modifier
 * if necessary.
 *
 * @param modifier The modifier to apply to this grid
 * @param items The grids items to be declared as in any Compose grid.
 */
@Composable
fun HangmanVerticalGrid(
    modifier: Modifier = Modifier,
    items: LazyGridScope.() -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = HangmanGridItems.gridCellMinSize),
        horizontalArrangement = Arrangement.spacedBy(HangmanGridItems.cellSpacing),
        verticalArrangement = Arrangement.spacedBy(HangmanGridItems.cellSpacing),
        // The actual space between two cells. As it is applied on every cell, the space will
        // actually be worth 2*cellSpacing.
        modifier = modifier.padding(HangmanGridItems.cellSpacing)
    ) {
        items()
    }
}

/**
 * Stores some of the needed grid items definitions
 * This is were default size, spacing, etc... Should be defined and fetched by grid item composables.
 */
object HangmanGridItems {
    /**
     * The space to add between each cell.
     */
    val cellSpacing = smallSpacing

    /**
     * The minimum size (width and height) of a grid cell.
     */
    val gridCellMinSize = normalHeight
}

/**
 * Stores some of the needed list items definitions
 * This is were default height, spacing, etc... Should be defined and fetched by list item
 * composables.
 */
object HangmanListItems {
    /**
     * The elevation to use for "fixed" list items.
     */
    val fixed_elevation = noElevation

    /**
     * The default elevation to use for list items, unless specified otherwise.
     */
    val elevation = normalElevation

    /**
     * The padding around the list item's card, ie : around the list item itself.
     */
    val surfacePadding = smallSpacing

    /**
     * The padding to apply between the content and the surrounding card/item.
     */
    val itemContentPadding = smallSpacing

    /**
     * The horizontal spacing that should visually separate two objects into an item.
     * For exemple, if an item contains an image and a text, one next to the other, then this
     * value should be used as the space between both items.
     * This cannot be directly apply to any list item because the content is not known beforehand,
     * and thus should be used when creating the content directly.
     */
    val horizontalSpacing = smallSpacing

    /**
     * The default height of any list item.
     */
    val itemHeight = hugeHeight

    /**
     * The color of the pull to refresh indicator itself.
     */
    @Composable
    fun pullRefreshIndicatorColor(thresholdReached: Boolean): Color {
        return if(thresholdReached) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    }

    /**
     * The default color for any list item's background.
     */
    @Composable
    fun color(): Color {
        return MaterialTheme.colors.surface
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
fun previewFixedListItem() {
    HangmanTheme() {
        HangmanFixedListItem(
            content = { Text("Fixed") },
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewMovingListItem() {
    HangmanTheme() {
        HangmanMovingListItem(
            content = { Text("Moving") },
        )
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHangmanVerticalGrid() {
    HangmanTheme() {
        HangmanVerticalGrid {
            items(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, "Hello")) {
                Text(text="" +it)
            }
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHangmanPullRefreshProgressIndicatorThresholdNotReached() {
    HangmanTheme() {
        Box {
            HangmanPullRefreshProgressIndicator(thresholdReached = false)
        }
    }
}

@DayNightModeWithBackgroundPreviews
@Composable
fun previewHangmanPullRefreshProgressIndicatorThresholdReached() {
    HangmanTheme() {
        Box {
            HangmanPullRefreshProgressIndicator(thresholdReached = true)
        }
    }
}
//endregion Previews