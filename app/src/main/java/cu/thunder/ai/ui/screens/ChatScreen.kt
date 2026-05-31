package cu.thunder.ai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cu.thunder.ai.data.UserProfile
import cu.thunder.ai.data.UserProfileKeys
import cu.thunder.ai.ui.components.ChatBubble
import cu.thunder.ai.ui.components.MessageActions
import cu.thunder.ai.ui.components.TypingIndicator
import cu.thunder.ai.ui.theme.BattleNetDark
import cu.thunder.ai.ui.theme.ElectricBlue
import cu.thunder.ai.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Definir dataStore a nivel de archivo, no dentro del Composable
private val Context.dataStore by preferencesDataStore("thunderai_settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    mainViewModel: MainViewModel,
    userProfile: UserProfile,
    onNewConversation: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val messages by mainViewModel.messages.collectAsStateWithLifecycle()
    val isModelLoaded by mainViewModel.isModelLoaded.collectAsStateWithLifecycle()
    val isLoading by mainViewModel.isLoading.collectAsStateWithLifecycle()
    val currentResponse by mainViewModel.currentResponse.collectAsStateWithLifecycle()
    
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val isModelMissing = !isModelLoaded && !isLoading
    
    // Obtener nombre del usuario desde DataStore
    val userPreferences = context.dataStore.data.collectAsState(initial = emptyPreferences())
    val userName = userPreferences.value[UserProfileKeys.USER_NAME] ?: "Usuario"
    
    // Auto-scroll cuando hay nuevos mensajes
    LaunchedEffect(messages.size, currentResponse) {
        if (messages.isNotEmpty() || currentResponse.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }
    
    // Gradient background estilo Battle.net
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(BattleNetDark, Color(0xFF0D1B2A), Color(0xFF1B263B))
    )
    
    // Función para enviar mensaje
    val sendMessage = { text: String ->
        if (text.isNotBlank() && isModelLoaded && !isLoading) {
            keyboardController?.hide()
            mainViewModel.sendMessage(text)
            inputText = TextFieldValue("")
            scope.launch {
                delay(100)
                listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
            }
        }
    }
    
    // Función para regenerar el último mensaje
    val handleRegenerate = {
        val lastUserMsg = messages.lastOrNull { it.isUser }
        if (lastUserMsg != null) {
            mainViewModel.regenerateLastMessage(lastUserMsg.content)
        }
    }
    
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp,
                color = BattleNetDark.copy(alpha = 0.95f)
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Botón para abrir drawer (menú lateral)
                            IconButton(onClick = onOpenDrawer) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = ElectricBlue)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = ElectricBlue
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("⚡", color = BattleNetDark, fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("ThunderAI", style = MaterialTheme.typography.titleMedium, color = ElectricBlue)
                                Text("Tu asistente personal", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    },
                    actions = {
                        // Botón Nueva conversación (+)
                        IconButton(onClick = onNewConversation) {
                            Icon(Icons.Default.Add, contentDescription = "Nueva conversación", tint = ElectricBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(paddingValues)
        ) {
            when {
                isModelMissing -> {
                    // Pantalla sin modelo cargado
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(32.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = ElectricBlue.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚡", fontSize = 48.sp, color = ElectricBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("ThunderAI", style = MaterialTheme.typography.headlineMedium, color = ElectricBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tu asistente personal offline",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Para comenzar, necesitas cargar un modelo")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onOpenDrawer,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = BattleNetDark),
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    Icon(Icons.Default.FolderOpen, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Abrir menú y cargar modelo")
                                }
                            }
                        }
                    }
                }
                messages.isEmpty() && currentResponse.isEmpty() -> {
                    // Pantalla de bienvenida personalizada con el nombre del usuario
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(60.dp))
                        Text(
                            text = "Hola $userName",
                            style = MaterialTheme.typography.headlineSmall,
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¿En qué puedo ayudarte hoy?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        val suggestions = listOf(
                            "💻 Escribe una función en Python",
                            "📝 Redacta un correo profesional",
                            "🎨 Explícame qué es la IA",
                            "📊 Crea una lista de verificación"
                        )
                        
                        suggestions.forEach { suggestion ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                                onClick = {
                                    if (isModelLoaded && !isLoading) {
                                        mainViewModel.sendMessage(suggestion.replace(Regex("^[📝💻🎨📊]\\s"), ""))
                                    }
                                }
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Lista de mensajes
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { message ->
                            Column {
                                ChatBubble(message = message)
                                // Mostrar acciones solo en el último mensaje del asistente
                                if (!message.isUser && message == messages.lastOrNull() && !isLoading) {
                                    MessageActions(
                                        content = message.content,
                                        onRegenerate = handleRegenerate,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        // Respuesta parcial (mientras se genera)
                        if (currentResponse.isNotEmpty()) {
                            item {
                                ChatBubble(
                                    message = cu.thunder.ai.data.ChatMessage(
                                        content = currentResponse,
                                        isUser = false,
                                        timestamp = System.currentTimeMillis()
                                    ),
                                    isPartial = true
                                )
                            }
                        }
                        
                        // Indicador de escritura (3 puntitos)
                        if (isLoading && currentResponse.isEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        TypingIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Input area (sube con el teclado gracias a imePadding)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Botón Respuesta Rápida
                    IconButton(
                        onClick = { sendMessage("Responde de forma breve y concisa: ") },
                        enabled = isModelLoaded && !isLoading,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Respuesta rápida",
                            tint = if (isModelLoaded && !isLoading) ElectricBlue else Color.Gray
                        )
                    }
                    
                    // Botón Pensar (razonamiento paso a paso)
                    IconButton(
                        onClick = { sendMessage("Piensa paso a paso antes de responder: ") },
                        enabled = isModelLoaded && !isLoading,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Pensar",
                            tint = if (isModelLoaded && !isLoading) ElectricBlue else Color.Gray
                        )
                    }
                    
                    // Campo de texto
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = if (isModelLoaded) "Escribe un mensaje..." else "Primero carga un modelo",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        enabled = isModelLoaded && !isLoading,
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 5,
                        minLines = 1
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Botón enviar (flecha azul hacia arriba estilo DeepSeek)
                    FloatingActionButton(
                        onClick = { sendMessage(inputText.text) },
                        containerColor = if (isModelLoaded && !isLoading && inputText.text.isNotBlank()) {
                            ElectricBlue
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Enviar",
                            tint = if (isModelLoaded && !isLoading && inputText.text.isNotBlank()) {
                                BattleNetDark
                            } else {
                                Color.Gray
                            }
                        )
                    }
                }
            }
        }
    }
}