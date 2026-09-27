package com.maria.movierank.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinemaColorScheme = darkColorScheme(
    primary = CinemaGold,
    onPrimary = OnCinemaGold,
    primaryContainer = CinemaGoldDark,
    onPrimaryContainer = CinemaGold,
    secondary = CinemaIndigo,
    onSecondary = TextPrimary,
    tertiary = CinemaRed,
    onTertiary = TextPrimary,
    background = CinemaBackground,
    onBackground = TextPrimary,
    surface = CinemaSurface,
    onSurface = TextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    error = CinemaRed,
    onError = TextPrimary
)

@Composable
fun MovieRankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CinemaColorScheme,
        //typography = Typography,
        content = content
    )
}
