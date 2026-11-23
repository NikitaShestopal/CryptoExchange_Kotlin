package com.example.cryptoexchange.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ExchangeColors {
    val Positive = Color(0xFF0ECB81)
    val PositiveBgDark = Color(0xFF102A22)
    val PositiveBgLight = Color(0xFFE6F9F1)

    val Negative = Color(0xFFF6465D)
    val NegativeBgDark = Color(0xFF2D161C)
    val NegativeBgLight = Color(0xFFFDECEF)

    val AccentAmber = Color(0xFFF0B90B)
    val DividerDark = Color(0xFF252A33)
    val DividerLight = Color(0xFFE5E7EB)
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB48300),
    onPrimary = Color.White,
    secondary = Color(0xFF2563EB),
    background = Color(0xFFF7F8FA),
    surface = Color.White,
    surfaceVariant = Color(0xFFEEF0F4),
    onBackground = Color(0xFF111418),
    onSurface = Color(0xFF111418),
    onSurfaceVariant = Color(0xFF5E6673),
    outline = ExchangeColors.DividerLight,
    error = ExchangeColors.Negative
)

private val DarkColorScheme = darkColorScheme(
    primary = ExchangeColors.AccentAmber,
    onPrimary = Color(0xFF121417),
    secondary = Color(0xFF60A5FA),
    background = Color(0xFF0E1116),
    surface = Color(0xFF161A20),
    surfaceVariant = Color(0xFF1E232B),
    onBackground = Color(0xFFEAECEF),
    onSurface = Color(0xFFEAECEF),
    onSurfaceVariant = Color(0xFF848E9C),
    outline = ExchangeColors.DividerDark,
    error = ExchangeColors.Negative
)

@Composable
fun CryptoExchangeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
