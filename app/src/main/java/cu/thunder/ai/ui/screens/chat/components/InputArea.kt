package cu.thunder.ai.ui.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.thunder.ai.ui.theme.*

@Composable
fun InputArea(
    onSendMessage: (String) -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
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
        ),
        border = if (isFocused) 
            androidx.compose.foundation.BorderStroke(2.dp, ElectricBlue) 
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Botones de modo
            IconButton(
                onClick = {
                    quickReplyMode = if (quickReplyMode == "quick") null else "quick"
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Respuesta rápida",
                    tint = if (quickReplyMode == "quick") ElectricBlue else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = {
                    quickReplyMode = if (quickReplyMode == "think") null else "think"
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Pensar",
                    tint = if (quickReplyMode == "think") ElectricBlue else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Campo de texto
            TextField(
                value = message,
                onValueChange = { 
                    if (it.length <= 500) message = it 
                },
                placeholder = {
                    Text(
                        text = when (quickReplyMode) {
                            "quick" -> "Respuesta rápida..."
                            "think" -> "Pensando paso a paso..."
                            else -> "Escribe un mensaje..."
                        },
                        color = TextSecondary
                    )
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceMedium,
                    unfocusedContainerColor = SurfaceMedium,
                    focusedIndicatorColor = ElectricBlue,
                    unfocusedIndicatorColor = SurfaceMedium,
                    cursorColor = ElectricBlue,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 5,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                onFocusChange = { isFocused = it }
            )

            // Botón de enviar
            IconButton(
                onClick = { sendMessage() },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (message.isNotBlank()) ElectricBlue else TextSecondary.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Enviar",
                    tint = if (message.isNotBlank()) 
                        TextPrimary 
                    else 
                        TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}