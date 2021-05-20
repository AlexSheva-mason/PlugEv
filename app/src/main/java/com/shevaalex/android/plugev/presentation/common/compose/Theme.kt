package com.shevaalex.android.plugev.presentation.common.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Teal400,
    primaryVariant = Teal700,
    background = Teal100
)

private val LightColorPalette = lightColors(
    primary = Teal700,
    primaryVariant = Teal300,
    background = Teal100
)

@Composable
fun PlugEvTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = PlugEvTypography,
        shapes = Shapes,
        content = content
    )
}
