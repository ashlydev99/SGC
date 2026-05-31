package cu.thunder.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cu.thunder.ai.data.ChatHistory
import cu.thunder.ai.data.ChatHistoryDatabase
import cu.thunder.ai.data.UserProfile
import cu.thunder.ai.data.UserProfileKeys
import cu.thunder.ai.ui.components.DrawerContent
import cu.thunder.ai.ui.screens.ChatScreen
import cu.thunder.ai.ui.screens.SettingsScreen
import cu.thunder.ai.ui.theme.ThunderAITheme
import cu.thunder.ai.utils.ChatHistoryManager
import cu.thunder.ai.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("thunderai_settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThunderAITheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ThunderAINavigation()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThunderAINavigation() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    
    // DrawerState correcto (no Boolean)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    
    val preferencesManager = cu.thunder.ai.data.PreferencesManager(context)
    val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(context.contentResolver, preferencesManager))
    val chatHistoryManager = remember { ChatHistoryManager(context) }
    
    var userProfile by remember { mutableStateOf(UserProfile()) }
    var searchQuery by remember { mutableStateOf("") }
    var histories by remember { mutableStateOf<List<ChatHistory>>(emptyList()) }
    var currentChatId by remember { mutableStateOf<Long?>(null) }
    
    // Función para abrir el drawer
    val openDrawer = {
        scope.launch {
            drawerState.open()
        }
    }
    
    // Función para cerrar el drawer
    val closeDrawer = {
        scope.launch {
            drawerState.close()
        }
    }
    
    // Cargar nombre de usuario
    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        val userName = prefs[UserProfileKeys.USER_NAME] ?: "Usuario"
        userProfile = userProfile.copy(name = userName)
    }
    
    // Cargar historiales
    LaunchedEffect(Unit) {
        chatHistoryManager.getAllHistories().collect { historyList ->
            histories = if (searchQuery.isNotBlank()) {
                historyList.filter { 
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.preview.contains(searchQuery, ignoreCase = true)
                }
            } else {
                historyList
            }
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DrawerContent(
                userName = userProfile.name,
                histories = histories,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onHistoryClick = { history ->
                    scope.launch {
                        val messages = chatHistoryManager.loadChat(history.id)
                        viewModel.restoreChat(messages)
                        currentChatId = history.id
                        closeDrawer() // Cerrar drawer después de seleccionar
                    }
                },
                onTogglePinned = { history ->
                    scope.launch {
                        chatHistoryManager.togglePinned(history)
                    }
                },
                onDeleteHistory = { history ->
                    scope.launch {
                        chatHistoryManager.deleteHistory(history)
                        if (currentChatId == history.id) {
                            viewModel.clearChat()
                            currentChatId = null
                        }
                    }
                },
                onNewConversation = {
                    viewModel.clearChat()
                    currentChatId = null
                    closeDrawer() // Cerrar drawer después de nueva conversación
                    // Navegar al chat si no está ya allí
                    if (navController.currentDestination?.route != "chat") {
                        navController.navigate("chat") {
                            popUpTo("chat") { inclusive = true }
                        }
                    }
                },
                onOpenSettings = {
                    closeDrawer() // Cerrar drawer antes de navegar
                    navController.navigate("settings")
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "chat"
        ) {
            composable("chat") {
                ChatScreen(
                    mainViewModel = viewModel,
                    userProfile = userProfile,
                    onNewConversation = {
                        viewModel.clearChat()
                        currentChatId = null
                        closeDrawer()
                    },
                    onOpenDrawer = openDrawer,  // ← NUEVO PARÁMETRO
                    onRegenerateLastMessage = {
                        // Regenerar último mensaje
                        val lastUserMessage = viewModel.messages.value.lastOrNull { it.isUser }
                        if (lastUserMessage != null) {
                            viewModel.regenerateLastMessage(lastUserMessage.content)
                        }
                    }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { 
                        navController.popBackStack()
                        // Opcional: abrir drawer al volver
                        // openDrawer()
                    },
                    onUserProfileUpdated = {
                        scope.launch {
                            val prefs = context.dataStore.data.first()
                            val newName = prefs[UserProfileKeys.USER_NAME] ?: "Usuario"
                            userProfile = userProfile.copy(name = newName)
                        }
                    }
                )
            }
        }
    }
}