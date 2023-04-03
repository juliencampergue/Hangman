package com.hangman.android.ui.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hangman.android.R
import com.hangman.android.ui.theme.HangmanBottomNavigation
import com.hangman.android.ui.theme.HangmanNavigationItem

/**
 * Describes all the screens accessible from the navigation bar, along with all necessary elements
 * to interact with.
 *
 * @param route The navigation route to access the given screen
 * @param titleId The ID of the String resource that will be displayed in the nav bar for this item.
 * @param iconId The ID of the drawable resource that will be displayed in the nav bar for this item.
 * @param selectedIconId The ID of the drawable resource that will be displayed in the nav bar for
 * this item when it will be the currently selected screen/route.
 */
sealed class BottomBarAccessibleScreens(
    val route: String,
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
    @DrawableRes val selectedIconId: Int,
) {
    /**
     * Describes the navbar route, title and icons for the part of the app that will display
     * word of today's games.
     */
    object GAME: BottomBarAccessibleScreens(
        GAME_SCREEN_ROUTE,
        R.string.bottom_bar_game_screen_title,
        R.drawable.ic_game_screen_24,
        R.drawable.ic_game_screen_24_filled,
    )

    /**
     * Describes the navbar route, title and icons for the part of the app that will display the
     * settings.
     */
    object SETTINGS : BottomBarAccessibleScreens(
        SETTINGS_SCREEN_ROUTE,
        R.string.bottom_bar_settings_screen_tile,
        R.drawable.ic_outline_settings_24,
        R.drawable.ic_baseline_settings_24,
    )

    /**
     * Describes the navbar route, title and icons for the part of the app that will display
     * the history and details of previous games
     */
    object HISTORY : BottomBarAccessibleScreens(
        HISTORY_AND_DETAILS_SCREEN_ROUTE,
        R.string.bottom_bar_history_and_details_screen_title,
        R.drawable.ic_baseline_history_toggle_off_24,
        R.drawable.ic_baseline_history_24,
    )
}

/**
 * The list of screens that must be accessible from the nav bar.
 */
val bottomBarAccessibleScreens = listOf(
    BottomBarAccessibleScreens.HISTORY,
    BottomBarAccessibleScreens.GAME,
    BottomBarAccessibleScreens.SETTINGS,
)

/**
 * The actual bottom navigation bar. It will display an icon for each of the accessible screens.
 * It will also handle the action to perform when user clicks on one of the items.
 *
 * @param navigationController The navController associated to this navigation bar, used to perform
 * the actual navigations (change of screen / back handling, etc...)
 */
@Composable
fun HangmanBottomBar(navigationController: NavController) {
    HangmanBottomNavigation() {
        // Get the currently selected navigation destination
        val navBackStackEntry by navigationController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Display each nav bar destination item
        bottomBarAccessibleScreens.forEach { screen ->
            HangmanNavigationItem(
                icon = {Icon(painter = painterResource(id = screen.iconId), null)},
                selectedIcon = {Icon(painter = painterResource(id = screen.selectedIconId), null)},
                label = {Text(text = stringResource(id = screen.titleId))},
                // The item will be selected if its route is part of the currently selected nav
                // destination hierarchy routes.
                selected = currentDestination?.hierarchy?.any {it.route == screen.route} == true,
                onClick = {
                    navigationController.navigate(screen.route) {
                        // When clicking on a nav item, popup to the start of the current nav
                        // destination first, but save the state so that, if user clicks on this
                        // root destination again, it will display the actual screen the user was on.
                        popUpTo(navigationController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        // Then navigate to the requested destination, launching it as SingleTop,
                        // and restore state if any has previously been saved for the selected
                        // destination.
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
