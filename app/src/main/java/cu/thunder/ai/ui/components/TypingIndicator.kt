package cu.thunder.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.ElectricBlue

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val delay1 = 0
    val delay2 = 200
    val delay3 = 400
    
    Row(
        modifier = modifier
            .padding(12.dp)
            .width(52.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(transition, delay1)
        Dot(transition, delay2)
        Dot(transition, delay3)
    }
}

@Composable
fun Dot(transition: InfiniteTransition, delay: Int) {
    val scale by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(8.dp * scale)
            .clip(CircleShape)
            .background(ElectricBlue)
    )
}