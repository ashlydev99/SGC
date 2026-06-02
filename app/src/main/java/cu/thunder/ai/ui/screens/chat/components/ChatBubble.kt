package cu.thunder.ai.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cu.thunder.ai.domain.model.ChatMessage
import cu.thunder.ai.ui.theme.*
import cu.thunder.ai.ui.util.MarkdownText
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatBubble(
    message: ChatMessage,
    isStreaming: Boolean = false,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (message.isUser) ElectricBlueTransparent
                    else CodeBackground
                )
                .padding(12.dp)
        ) {
            if (message.isUser) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            } else {
                MarkdownText(
                    content = message.content,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(message.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )

            if (!message.isUser && !isStreaming) {
                MessageActions(
                    onCopyClick = onCopyClick,
                    onShareClick = onShareClick,
                    onRegenerateClick = onRegenerateClick
                )
            }
        }
    }
}