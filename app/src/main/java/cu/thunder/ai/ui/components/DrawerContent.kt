package cu.thunder.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.thunder.ai.data.ChatHistory
import cu.thunder.ai.ui.theme.BattleNetDark
import cu.thunder.ai.ui.theme.ElectricBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    userName: String,
    histories: List<ChatHistory>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onHistoryClick: (ChatHistory) -> Unit,
    onTogglePinned: (ChatHistory) -> Unit,
    onDeleteHistory: (ChatHistory) -> Unit,
    onNewConversation: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Separar historiales fijados y normales
    val pinnedHistories = histories.filter { it.isPinned }
    val normalHistories = histories.filter { !it.isPinned }
    
    ModalDrawerSheet(
        drawerContainerColor = BattleNetDark,
        drawerShape = RoundedCornerShape(end = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con perfil de usuario
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ElectricBlue.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = ElectricBlue
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = BattleNetDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Tu asistente personal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar en el chat...", color = Color.White.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            
            // Botón nueva conversación
            Button(
                onClick = onNewConversation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = BattleNetDark),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nueva conversación")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de conversaciones
            Text(
                text = "Conversaciones",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (pinnedHistories.isNotEmpty()) {
                    item {
                        Text(
                            text = "📌 Fijados",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(pinnedHistories) { history ->
                        HistoryItem(
                            history = history,
                            onClick = { onHistoryClick(history) },
                            onTogglePinned = { onTogglePinned(history) },
                            onDelete = { onDeleteHistory(history) }
                        )
                    }
                }
                
                if (normalHistories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Recientes",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = if (pinnedHistories.isNotEmpty()) 16.dp else 0.dp, bottom = 4.dp)
                        )
                    }
                    items(normalHistories) { history ->
                        HistoryItem(
                            history = history,
                            onClick = { onHistoryClick(history) },
                            onTogglePinned = { onTogglePinned(history) },
                            onDelete = { onDeleteHistory(history) }
                        )
                    }
                }
                
                if (pinnedHistories.isEmpty() && normalHistories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay conversaciones guardadas",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
            
            // Botón de configuración al final
            Divider(color = Color.White.copy(alpha = 0.1f))
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = ElectricBlue) },
                label = { Text("Configuración", color = Color.White) },
                selected = false,
                onClick = onOpenSettings,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun HistoryItem(
    history: ChatHistory,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = history.preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    maxLines = 1
                )
            }
            
            Row {
                IconButton(onClick = onTogglePinned, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (history.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Fijar",
                        tint = if (history.isPinned) ElectricBlue else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Eliminar",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}