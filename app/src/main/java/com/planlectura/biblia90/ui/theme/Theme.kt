package com.planlectura.biblia90.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private val LightColors = lightColorScheme(
    primary = Color(0xFF1F3A5F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD5E3FF),
    onPrimaryContainer = Color(0xFF001B3C),
    secondary = Color(0xFF8A5A00),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDDB0),
    onSecondaryContainer = Color(0xFF2B1700),
    background = Color(0xFFFBF4E8),
    onBackground = Color(0xFF1E1B16),
    surface = Color(0xFFFBF4E8),
    onSurface = Color(0xFF1E1B16),
    surfaceVariant = Color(0xFFEDE1CF),
    onSurfaceVariant = Color(0xFF4D4539),
    surfaceContainer = Color(0xFFF3EADB),
    surfaceContainerHigh = Color(0xFFEEE4D3),
    outline = Color(0xFF7F7667),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8C8FF),
    onPrimary = Color(0xFF003061),
    primaryContainer = Color(0xFF1F3A5F),
    onPrimaryContainer = Color(0xFFD5E3FF),
    secondary = Color(0xFFFFB950),
    onSecondary = Color(0xFF482A00),
    secondaryContainer = Color(0xFF673F00),
    onSecondaryContainer = Color(0xFFFFDDB0),
    background = Color(0xFF15130F),
    onBackground = Color(0xFFE9E1D6),
    surface = Color(0xFF15130F),
    onSurface = Color(0xFFE9E1D6),
    surfaceVariant = Color(0xFF4D4539),
    onSurfaceVariant = Color(0xFFD0C5B4),
    surfaceContainer = Color(0xFF221F1A),
    surfaceContainerHigh = Color(0xFF2C2924),
    outline = Color(0xFF999080),
)

private val BaseTypography = Typography()

/** Serif para títulos (evoca el texto bíblico) y sans para la interfaz. */
private val AppTypography = BaseTypography.copy(
    displaySmall = BaseTypography.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = BaseTypography.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = FontFamily.Serif),
)

@Composable
fun BibliaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
