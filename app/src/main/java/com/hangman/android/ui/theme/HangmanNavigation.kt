package com.hangman.android.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

/**
 * The bottom navigation bar that will be displayed throughout the application.
 * It will contain every top level destination accessible at all times during the use of the
 * application.
 *
 * @param modifier The modifier to apply to this navigation bar.
 * @param content The content of the bar itself (buttons, texts, etc...). It can be any composable.
 */
@Composable
fun HangmanBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    BottomNavigation(
        backgroundColor = HangmanNavigations.navigationBarBackgroundColor(),
        contentColor = HangmanNavigations.navigationBarContentColor(),
        elevation = largeElevation,
        modifier = modifier,
        content = content,
    )
}

/**
 * A navigation item. It is made to be used in the navigation bar. It will display a top level
 * destination of the application.
 * It will have an icon and a title. The icon might be different when the item is selected if
 * said icon is given in the parameters.
 * A small colored shape will be displayed behind the icon when the item is selected.
 *
 * @param selected Is the current destination the active one.
 * @param onClick The action that will be performed when this item is clicked
 * @param icon The default icon to display in the item
 * @param modifier The modifier to apply to this item
 * @param selectedIcon The icon to display when this item is selected. Defaults to unselected icon
 * if null.
 * @param label The title to display alongside the icon. can be null for no label.
 */
@Composable
fun RowScope.HangmanNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    label: (@Composable () -> Unit)? = null,
) {
    // Describe the selection/deselection animation
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(HangmanNavigations.selectionAnimationDuration)
    )

    BottomNavigationItem(
        selected = selected,
        onClick = onClick,
        icon = {
            // The item's icon. The first Box will define behaviors common to both icons (selected
            // and unselected). ie : Background and padding. The inner boxes will hold the actual
            // icons.
            Box(
                modifier = Modifier
                    // This box' background is the selection indicator. It will be a small colored
                    // shape drawn behind the icon to indicate which one is selected.
                    .background(
                        color = HangmanNavigations
                            .navigationBarSelectedItemBackgroundColor()
                            .copy(alpha=animationProgress),
                        shape = HangmanNavigationShapes.large,
                    )
                    .padding(
                        horizontal = HangmanNavigations.selectionIndicatorHorizontalPadding,
                        vertical = HangmanNavigations.selectionIndicatorVerticalPadding,
                    )
            ) {
                Box(Modifier.alpha(animationProgress)) {
                    selectedIcon()
                }
                Box(Modifier.alpha(1f - animationProgress)) {
                    icon()
                }
            }
        },
        modifier = modifier,
        label = label,
    )
}

/**
 * Stores some of the needed Navigation definitions
 * This is were default colors, padding, etc... Should be defined and fetched by navigation
 * composables.
 */
object HangmanNavigations {
    /**
     * The default background color for the navigation bar
     */
    @Composable
    fun navigationBarBackgroundColor() = MaterialTheme.colors.surface

    /**
     * The default color for the content of the navigation bar, ie, navigation item labels, etc...
     */
    @Composable
    fun navigationBarContentColor() = MaterialTheme.colors.onSurface

    /**
     * The background color for the selected item only
     */
    @Composable
    fun navigationBarSelectedItemBackgroundColor() = MaterialTheme.colors.primaryVariant

    /**
     * The content color for the selected item
     */
    @Composable
    fun navigationBarSelectedItemBackgroundContentColor() = MaterialTheme.colors.onPrimary

    /**
     * The duration of the selection/deselection animation
     */
    val selectionAnimationDuration = 500 //500ms

    /**
     * The horizontal padding for the selection indicator
     */
    val selectionIndicatorHorizontalPadding = normalSpacing

    /**
     * The vertical padding for the selection indicator
     */
    val selectionIndicatorVerticalPadding = tinySpacing
}