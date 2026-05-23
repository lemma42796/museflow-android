package com.ixuea.courses.mymusic.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F7A68),
    onPrimary = Color.White,
    secondary = Color(0xFF5D6C28),
    background = Color(0xFFFAFBF8),
    onBackground = Color(0xFF1B1D1A),
    surface = Color.White,
    onSurface = Color(0xFF1B1D1A),
    surfaceVariant = Color(0xFFE0E4DC),
    onSurfaceVariant = Color(0xFF44483F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF67DCC2),
    onPrimary = Color(0xFF00382E),
    secondary = Color(0xFFC7D98A),
    background = Color(0xFF111411),
    onBackground = Color(0xFFE2E4DE),
    surface = Color(0xFF191C18),
    onSurface = Color(0xFFE2E4DE),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C8BD),
)

@Composable
fun MuseFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}

internal val ColorScheme.subtleDivider: Color
    get() = surfaceVariant.copy(alpha = 0.7f)
