package cu.thunder.ai.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = ElectricBlue.copy(alpha = 0.3f),
    onPrimaryContainer = ElectricBlue,
    secondary = TextSecondary,
    onSecondary = Color.White,
    background = BattleNetDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMedium,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = TextSecondary.copy(alpha = 0.5f),
    inverseSurface = TextPrimary,
    inverseOnSurface = BattleNetDark
)

@Composable
fun ThunderAITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Fondo con gradiente
object BackgroundGradient {
    val VerticalGradient = Brush.verticalGradient(
        colors = listOf(
            BattleNetDark,
            SurfaceDark,
            SurfaceMedium.copy(alpha = 0.3f)
        )
    )
}