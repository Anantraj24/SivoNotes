package com.anant.sivonotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = SivoPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = SivoPrimaryContainer,
    onPrimaryContainer = SivoOnPrimaryContainer,
    secondary = SivoSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = SivoSecondaryContainer,
    onSecondaryContainer = SivoOnSecondaryContainer,
    tertiary = PastelMint,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    background = SivoBgLight,
    onBackground = SivoTextPrimaryLight,
    surface = SivoSurfaceLight,
    onSurface = SivoTextPrimaryLight,
    surfaceVariant = SivoSurfaceVariantLight,
    onSurfaceVariant = SivoTextSecondaryLight,
    outline = SivoBorderLight,
    outlineVariant = SivoBorderLight,
    error = SivoError,
    errorContainer = SivoErrorContainer,
    onError = androidx.compose.ui.graphics.Color.White,
    onErrorContainer = SivoError
)

private val DarkColorScheme = darkColorScheme(
    primary = SivoPrimaryLight,
    onPrimary = SivoBgDark,
    primaryContainer = SivoPrimaryVariant,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = SivoSecondary,
    onSecondary = SivoBgDark,
    secondaryContainer = SivoSurfaceVariantDark,
    onSecondaryContainer = SivoTextPrimaryDark,
    tertiary = PastelMint,
    onTertiary = SivoBgDark,
    background = SivoBgDark,
    onBackground = SivoTextPrimaryDark,
    surface = SivoSurfaceDark,
    onSurface = SivoTextPrimaryDark,
    surfaceVariant = SivoSurfaceVariantDark,
    onSurfaceVariant = SivoTextSecondaryDark,
    outline = SivoBorderDark,
    outlineVariant = SivoBorderDark,
    error = SivoError,
    errorContainer = SivoErrorContainer,
    onError = androidx.compose.ui.graphics.Color.White,
    onErrorContainer = SivoError
)

@Composable
fun SivoNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Stitch brand palette consistent by default
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = SivoShapes,
        typography = Typography,
        content = content
    )
}