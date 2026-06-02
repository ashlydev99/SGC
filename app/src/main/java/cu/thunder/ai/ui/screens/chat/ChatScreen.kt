package cu.thunder.ai.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cu.thunder.ai.domain.model.ChatMessage
import cu.thunder.ai.ui.components.AppDrawer
import cu.thunder.ai.ui.screens.chat.components.*
import cu.thunder.ai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentResponse by viewModel.currentResponse.collectAsState()
    val isWelcomeScreen by viewModel.isWelcomeScreen.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Auto-scroll cuando hay nuevos mensajes
    LaunchedEffect(messages.size, currentResponse) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                viewModel = viewModel,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = drawerState.isOpen || messages.isEmpty()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "ThunderAI",
                                style = MaterialTheme.typography.titleLarge,
                                color = ElectricBlue
                            )
                            Text(
                                text = "Tu asistente personal",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            viewModel.newConversation()
                            scope.launch { drawerState.close() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nueva conversación",
                                tint = ElectricBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BattleNetDark,
                        titleContentColor = ElectricBlue
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                BattleNetDark,
                                SurfaceDark,
                                SurfaceMedium.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                if (isWelcomeScreen && messages.isEmpty()) {
                    WelcomeScreen(
                        onSuggestionClick = { suggestion ->
                            viewModel.sendMessage(suggestion)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = messages,
                            key = { message -> message.id }
                        ) { message ->
                            ChatBubble(
                                message = message,
                                isStreaming = false,
                                onCopyClick = { viewModel.copyMessage(message.content) },
                                onShareClick = { viewModel.shareMessage(context, message.content) },
                                onRegenerateClick = { 
                                    if (message == messages.lastOrNull { !it.isUser }) {
                                        viewModel.regenerateLastResponse()
                                    }
                                }
                            )
                        }

                        // Mostrar respuesta en streaming
                        if (isGenerating && currentResponse.isNotEmpty()) {
                            item {
                                ChatBubble(
                                    message = ChatMessage(
                                        content = currentResponse,
                                        isUser = false,
                                        timestamp = System.currentTimeMillis()
                                    ),
                                    isStreaming = true,
                                    onCopyClick = {},
                                    onShareClick = {},
                                    onRegenerateClick = {}
                                )
                            }
                        }

                        // Mostrar indicador de escritura
                        if (isGenerating && currentResponse.isEmpty()) {
                            item {
                                TypingIndicator()
                            }
                        }

                        // Espacio para el input
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                // Área de input fija en la parte inferior
                InputArea(
                    onSendMessage = { message ->
                        viewModel.sendMessage(message)
                    },
                    isGenerating = isGenerating,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .imePadding()
                )
            }
        }
    }
}