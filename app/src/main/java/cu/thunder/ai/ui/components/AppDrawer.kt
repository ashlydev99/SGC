package cu.thunder.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import cu.thunder.ai.data.local.entity.Conversation
import cu.thunder.ai.ui.screens.chat.ChatViewModel
import cu.thunder.ai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    navController: NavController,
    viewModel: ChatViewModel,
    onCloseDrawer: () -> Unit
) {
    val conversations by viewModel.getAllConversations().collectAsStateWithLifecycle(initialValue = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredConversations = remember(conversations, searchQuery) {
        if (searchQuery.isBlank()) {
            conversations
        } else {
            conversations.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.preview.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val pinnedConversations = filteredConversations.filter { it.isPinned }
    val recentConversations = filteredConversations.filter { !it.isPinned }

    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .background(BattleNetDark)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    style = MaterialTheme.typography.headlineLarge,
                    color = BattleNetDark
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Usuario",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "Tu asistente personal",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Buscar en el chat...", color = TextSecondary)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = TextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = SurfaceMedium,
                cursorColor = ElectricBlue,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        // Lista de conversaciones
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (pinnedConversations.isNotEmpty()) {
                item {
                    Text(
                        text = "📌 Fijados",
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricBlue,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(pinnedConversations, key = { it.id }) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = {
                            viewModel.loadConversation(conversation.id)
                            onCloseDrawer()
                        },
                        onTogglePin = { viewModel.togglePin(conversation) },
                        onDelete = { viewModel.deleteConversation(conversation) }
                    )
                }
            }

            item {
                Text(
                    text = "Recientes",
                    style = MaterialTheme.typography.labelLarge,
                    color = ElectricBlue,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(recentConversations, key = { it.id }) { conversation ->
                ConversationItem(
                    conversation = conversation,
                    onClick = {
                        viewModel.loadConversation(conversation.id)
                        onCloseDrawer()
                    },
                    onTogglePin = { viewModel.togglePin(conversation) },
                    onDelete = { viewModel.deleteConversation(conversation) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        viewModel.newConversation()
                        onCloseDrawer()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricBlue
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva conversacion")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                HorizontalDivider(color = SurfaceMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("settings")
                            onCloseDrawer()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Configuracion",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Configuracion",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceMedium.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conversation.preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            Column {
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = if (conversation.isPinned) "Desfijar" else "Fijar",
                        tint = if (conversation.isPinned) ElectricBlue else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ErrorRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}