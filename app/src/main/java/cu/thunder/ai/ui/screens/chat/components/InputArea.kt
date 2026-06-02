package cu.thunder.ai.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.*

@Composable
fun InputArea(
    onSendMessage: (String) -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var quickReplyMode by remember { mutableStateOf<String?>(null) }

    fun sendMessage() {
        if (message.isNotBlank() && !isGenerating) {
            val finalMessage = when (quickReplyMode) {
                "quick" -> "Responde de forma breve y concisa: $message"
                "think" -> "Piensa paso a paso antes de responder: $message"
                else -> message
            }
            onSendMessage(finalMessage)
            message = ""
            quickReplyMode = null
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Boton de respuesta rapida
            IconButton(
                onClick = {
                    quickReplyMode = if (quickReplyMode == "quick") null else "quick"
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Respuesta rapida",
                    tint = if (quickReplyMode == "quick") ElectricBlue else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(2.dp))

            // Boton de pensar
            IconButton(
                onClick = {
                    quickReplyMode = if (quickReplyMode == "think") null else "think"
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Pensar",
                    tint = if (quickReplyMode == "think") ElectricBlue else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Campo de texto
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(
                        text = when (quickReplyMode) {
                            "quick" -> "Respuesta rapida..."
                            "think" -> "Pensando paso a paso..."
                            else -> "Escribe un mensaje..."
                        },
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = ElectricBlue,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary
                ),
                maxLines = 4,
                singleLine = false,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Boton de enviar (mismo tamaño que los otros botones)
            IconButton(
                onClick = { sendMessage() },
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (message.isNotBlank()) ElectricBlue else ButtonDisabled,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Enviar",
                    tint = if (message.isNotBlank()) TextPrimary else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}