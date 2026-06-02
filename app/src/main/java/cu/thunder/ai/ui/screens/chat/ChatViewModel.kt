package cu.thunder.ai.ui.screens.chat

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cu.thunder.ai.ThunderAIApp
import cu.thunder.ai.data.local.entity.Conversation
import cu.thunder.ai.data.repository.ConversationRepository
import cu.thunder.ai.domain.model.ChatMessage
import cu.thunder.ai.domain.usecase.ChatUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ConversationRepository
    private val chatUseCase = ChatUseCase()
    private val prefs: SharedPreferences = application.getSharedPreferences("thunderai_prefs", Context.MODE_PRIVATE)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()

    private val _isWelcomeScreen = MutableStateFlow(true)
    val isWelcomeScreen: StateFlow<Boolean> = _isWelcomeScreen.asStateFlow()

    private val _userName = MutableStateFlow("Usuario")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _temperature = MutableStateFlow(0.7f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _maxTokens = MutableStateFlow(2048)
    val maxTokens: StateFlow<Int> = _maxTokens.asStateFlow()

    private val _modelPath = MutableStateFlow<String?>(null)
    val modelPath: StateFlow<String?> = _modelPath.asStateFlow()

    private var currentConversationId: Long? = null

    init {
        val database = (application as ThunderAIApp).database
        repository = ConversationRepository(database.conversationDao())
        
        // Cargar preferencias guardadas
        loadPreferences()
        
        // Cargar última conversación si existe
        viewModelScope.launch {
            repository.getAllConversations().collect { conversations ->
                if (conversations.isNotEmpty() && _messages.value.isEmpty()) {
                    val lastConversation = conversations.first()
                    loadConversation(lastConversation.id)
                }
            }
        }
    }

    private fun loadPreferences() {
        _userName.value = prefs.getString("user_name", "Usuario") ?: "Usuario"
        _temperature.value = prefs.getFloat("temperature", 0.7f)
        _maxTokens.value = prefs.getInt("max_tokens", 2048)
        _modelPath.value = prefs.getString("model_path", null)
    }

    fun updateUserName(name: String) {
        _userName.value = name
        prefs.edit().putString("user_name", name).apply()
    }

    fun updateTemperature(temp: Float) {
        _temperature.value = temp
        prefs.edit().putFloat("temperature", temp).apply()
    }

    fun updateMaxTokens(tokens: Int) {
        _maxTokens.value = tokens
        prefs.edit().putInt("max_tokens", tokens).apply()
    }

    fun updateModelPath(path: String?) {
        _modelPath.value = path
        if (path != null) {
            prefs.edit().putString("model_path", path).apply()
        } else {
            prefs.edit().remove("model_path").apply()
        }
    }

    // ... (el resto del código existente se mantiene igual)
    fun sendMessage(message: String) { /* ... */ }
    fun regenerateLastResponse() { /* ... */ }
    fun newConversation() { /* ... */ }
    fun loadConversation(conversationId: Long) { /* ... */ }
    fun copyMessage(content: String) { /* ... */ }
    fun shareMessage(context: Context, content: String) { /* ... */ }
    fun getAllConversations() = repository.getAllConversations()
    fun searchConversations(query: String) = repository.searchConversations(query)
    fun togglePin(conversation: Conversation) { /* ... */ }
    fun deleteConversation(conversation: Conversation) { /* ... */ }
    fun deleteAllConversations() { /* ... */ }
    fun loadModel(path: String, format: cu.thunder.ai.domain.model.ModelFormat): Boolean { /* ... */ }
    fun isModelReady(): Boolean = chatUseCase.isModelReady()
}