package cu.thunder.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = AccentBlue,
    tertiary = ElectricBlue.copy(alpha = 0.8f),
    background = BattleNetDark,
    surface = DeepBlue,
    surfaceVariant = Color(0xFF112240),
    onPrimary = BattleNetDark,
    onSecondary = BattleNetDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primaryContainer = ElectricBlue.copy(alpha = 0.15f),
    secondaryContainer = AccentBlue.copy(alpha = 0.1f)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),
    secondary = AccentBlue,
    background = Color(0xFFF5F7FA),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E)
)

@Composable
fun ThunderAITheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = BattleNetDark,
        darkIcons = false
    )
    systemUiController.setNavigationBarColor(
        color = BattleNetDark,
        darkIcons = false
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}