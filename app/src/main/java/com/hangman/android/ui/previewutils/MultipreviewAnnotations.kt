package com.hangman.android.ui.previewutils

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Use this annotation to have a preview of a UI component both in daytime mode and in nighttime
 * mode.
 * No background will be shown using this annotation. If you need a background as well to properly
 * display your preview, see the DayNightModeWithBackgroundPreviews annotation instead.
 */
@Preview(
    name="dayMode",
    group="themes",
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name="nightMode",
    group="themes",
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class DayNightModePreviews

/**
 * Use this annotation to have a preview of a UI component both in daytime and in nighttime mode.
 * Both previews will be added a light or dark background as well.
 */
@Preview(
    name="dayBackground",
    group="themes",
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name="nightBackground",
    group="themes",
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    uiMode = UI_MODE_NIGHT_YES,
)
annotation class DayNightModeWithBackgroundPreviews

/**
 * Use this annotation to have a preview of a UI component both in portait and in landscape modes.
 * Both previews will be added a light or dark background as well.
 * Both previews will be displayed on a Phone screen
 */
@Preview(
    name="portraitDevice",
    group="devices",
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    uiMode = UI_MODE_NIGHT_NO,
    device = Devices.PHONE,
    widthDp = 720,
    heightDp = 1024,
)
@Preview(
    name="landscapeDevice",
    group="devices",
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    uiMode = UI_MODE_NIGHT_NO,
    device = Devices.PHONE,
    widthDp = 1024,
    heightDp = 720,
)
annotation class PortraitAndLandscapePreviews

/**
 * Use this annotation to have a preview of a UI component only in landscape mode.
 * Preview will be added a light background by default as well.
 * Preview will be displayed on a Phone screen.
 */
@Preview(
    name="landscapeDevice",
    group="devices",
    showBackground = true,
    backgroundColor = 0xFFF0F0F0,
    uiMode = UI_MODE_NIGHT_NO,
    device = Devices.PHONE,
    widthDp = 1024,
    heightDp = 720,
)
annotation class LandscapeOnlyPreview
