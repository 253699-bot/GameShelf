package com.aikodelvalle.gameshelf.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCoffee,
    onPrimary = OnPrimaryText,
    secondary = SecondaryCoffee,
    onSecondary = OnPrimaryText,
    background = MainBackground,
    onBackground = MainText,
    surface = SurfaceWhite,
    onSurface = MainText,
    outline = BorderColor,
    outlineVariant = BorderColor,
)

@Composable
fun GameShelfTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
