package com.example.magnus.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EventPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = EventPrimaryVariant,
    onPrimaryContainer = Color.White,
    
    secondary = EventSecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = EventSecondaryVariant,
    onSecondaryContainer = Color.White,
    
    tertiary = EventTertiary,
    onTertiary = Color.White,
    
    background = EventBackgroundDark,
    onBackground = EventTextPrimaryDark,
    
    surface = EventSurfaceDark,
    onSurface = EventTextPrimaryDark,
    surfaceVariant = EventSurfaceVariantDark,
    onSurfaceVariant = EventTextSecondaryDark,
    
    error = EventError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EventPrimary,
    onPrimary = Color.White,
    primaryContainer = EventPrimaryVariant,
    onPrimaryContainer = Color.White,
    
    secondary = EventSecondary,
    onSecondary = Color.White,
    secondaryContainer = EventSecondaryVariant,
    onSecondaryContainer = Color.White,
    
    tertiary = EventTertiary,
    onTertiary = Color.White,
    
    background = EventBackground,
    onBackground = EventTextPrimary,
    
    surface = EventSurface,
    onSurface = EventTextPrimary,
    surfaceVariant = EventSurfaceVariant,
    onSurfaceVariant = EventTextSecondary,
    
    error = EventError,
    onError = Color.White,
    
    outline = EventTextTertiary
)

@Composable
fun MagnusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}