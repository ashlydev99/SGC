package cu.sg.system.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BlueElectric,
    onPrimary = Color.White,
    primaryContainer = BlueElectricDark,
    onPrimaryContainer = Color.White,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    secondaryContainer = OrangeAccent,
    onSecondaryContainer = Color.Black,
    tertiary = StatusSolucionado,
    background = BlueDark,
    onBackground = OnBackgroundDark,
    surface = BlueSurface,
    onSurface = OnSurfaceDark,
    surfaceVariant = BlueSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextHint,
    error = StatusPendiente,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BlueElectricLight,
    onPrimary = Color.White,
    primaryContainer = BlueElectric,
    onPrimaryContainer = Color.White,
    secondary = AmberAccent,
    onSecondary = Color.Black,
    secondaryContainer = OrangeAccent,
    onSecondaryContainer = Color.Black,
    tertiary = StatusSolucionado,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = BlueLight,
    onSurfaceVariant = OnSurfaceLight,
    outline = TextHint,
    error = StatusPendiente,
    onError = Color.White
)

@Composable
fun SGCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}