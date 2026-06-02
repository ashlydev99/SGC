package cu.thunder.ai.ui.theme

import androidx.compose.ui.graphics.Color

// Colores principales - Battle.net + Microsoft Copilot
val BattleNetDark = Color(0xFF0A1929)
val BattleNetDarker = Color(0xFF060F1A)
val SurfaceDark = Color(0xFF0D1B2A)
val SurfaceMedium = Color(0xFF1B263B)
val SurfaceLight = Color(0xFF243447)

// Color de acento - Azul eléctrico
val ElectricBlue = Color(0xFF00D4FF)
val ElectricBlueLight = Color(0xFF33DDFF)
val ElectricBlueDark = Color(0xFF00A8CC)
val ElectricBlueTransparent = Color(0x2600D4FF) // 15% opacidad
val ElectricBlueMedium = Color(0x4000D4FF) // 25% opacidad

// Colores de texto
val TextPrimary = Color(0xFFE8EDF2)
val TextSecondary = Color(0xFF8A99AA)
val TextTertiary = Color(0xFF5A6A7A)
val TextWhite = Color(0xFFFFFFFF)

// Fondos especiales
val CodeBackground = Color(0xFF1E2A3A)
val CodeBlockBackground = Color(0xFF112240)
val CardBackground = Color(0xFF1B263B)
val InputBackground = Color(0xFF1A2738)

// Colores de estado
val ErrorRed = Color(0xFFFF5252)
val ErrorRedDark = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF4CAF50)
val SuccessGreenDark = Color(0xFF388E3C)
val WarningYellow = Color(0xFFFFC107)
val WarningOrange = Color(0xFFFF9800)

// Colores especiales
val PinYellow = Color(0xFFFFD700)
val PinYellowLight = Color(0xFFFFE44D)

// Colores de gradiente
val GradientStart = BattleNetDark
val GradientMiddle = SurfaceDark
val GradientEnd = Color(0xFF1B263B).copy(alpha = 0.3f)

// Colores para burbujas de chat
val UserBubbleBackground = ElectricBlueTransparent
val AssistantBubbleBackground = CodeBackground
val UserBubbleBorder = ElectricBlue.copy(alpha = 0.3f)
val AssistantBubbleBorder = Color.Transparent

// Colores para botones
val ButtonPrimary = ElectricBlue
val ButtonSecondary = SurfaceMedium
val ButtonDisabled = TextSecondary.copy(alpha = 0.3f)
val ButtonTextLight = TextWhite
val ButtonTextDark = BattleNetDark

// Colores para el drawer
val DrawerBackground = BattleNetDark
val DrawerHeaderBackground = SurfaceDark
val DrawerItemSelected = ElectricBlue.copy(alpha = 0.1f)
val DrawerDivider = SurfaceMedium

// Colores para la barra superior
val TopBarBackground = BattleNetDark
val TopBarTitle = ElectricBlue
val TopBarSubtitle = TextSecondary
val TopBarIcon = TextPrimary

// Colores para el indicador de escritura
val TypingDotColor = ElectricBlue
val TypingDotInactive = TextSecondary.copy(alpha = 0.3f)

// Colores para código
val SyntaxHighlight = ElectricBlue
val CodeTextColor = TextPrimary
val CodeLineNumber = TextSecondary

// Colores para sliders y controles
val SliderActive = ElectricBlue
val SliderInactive = SurfaceMedium
val SliderThumb = ElectricBlue
val SliderTrack = SurfaceMedium

// Colores para estados de modelo
val ModelLoaded = SuccessGreen
val ModelNotLoaded = ErrorRed
val ModelLoading = WarningYellow

// Sombras y overlays
val ShadowColor = Color.Black.copy(alpha = 0.3f)
val OverlayDark = Color.Black.copy(alpha = 0.5f)
val OverlayLight = Color.White.copy(alpha = 0.1f)