package com.cs407.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BackgroundWarm = Color(0xFFFFF4F2)

val CardSurfaceWhite = Color(0xFFFFFFFF)

val OnSurfaceNeutral = Color(0xFF47413C)

private val LightColorScheme = lightColorScheme(

    primary = MadisonRed,
    onPrimary = Color.White,
    primaryContainer = MadisonRedDark,
    onPrimaryContainer = Color.White,

    secondary = MapleOrange,
    onSecondary = Color.White,

    background = BackgroundWarm,
    onBackground = OnSurfaceNeutral,

    surface = CardSurfaceWhite,
    onSurface = OnSurfaceNeutral,

    surfaceVariant = Color(0xFFF7F2EF),
    onSurfaceVariant = Color(0xFF665E56),

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MadisonMappleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = MadisonShapes,
        content = content
    )
}
