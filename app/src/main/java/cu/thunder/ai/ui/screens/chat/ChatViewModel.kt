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
import cu.thunder.ai.domain.model.ModelFormat
import cu.thunder.ai.domain.usecase.ChatUseCase
import cu.thunder.ai.util.ModelLoader
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

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

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded.asStateFlow()

    private var currentConversationId: Long? = null

    init {
        val database = (application as ThunderAIApp).database
        repository = ConversationRepository(database.conversationDao())
        
        chatUseCase.setContext(application)
        loadPreferences()
        loadModelFromStorage()
    }

    private fun loadPreferences() {
        _userName.value = prefs.getString("user_name", "Usuario") ?: "Usuario"
        _temperature.value = prefs.getFloat("temperature", 0.7f)
        _maxTokens.value = prefs.getInt("max_tokens", 2048)
        _modelPath.value = prefs.getString("model_path", null)
    }

    private fun loadModelFromStorage() {
        val savedPath = _modelPath.value ?: return
        
        viewModelScope.launch {
            try {
                val modelFile = File(getApplication<Application>().filesDir, "models/$savedPath")
                if (modelFile.exists()) {
                    val format = ModelLoader.detectFormat(modelFile.name)
                    if (format == ModelFormat.TASK) {
                        val success = chatUseCase.loadModel(modelFile.absolutePath, format)
                        _isModelLoaded.value = success
                        if (!success) {
                            _modelPath.value = null
                            prefs.edit().remove("model_path").apply()
                        }
                    }
                } else {
                    _modelPath.value = null
                    prefs.edit().remove("model_path").apply()
                }
            } catch (e: Exception) {
                _isModelLoaded.value = false
            }
        }
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

    fun loadModelFromFile(modelFile: File, format: ModelFormat) {
        val fileName = modelFile.name
        updateModelPath(fileName)
        
        viewModelScope.launch {
            try {
                _isModelLoaded.value = chatUseCase.loadModel(modelFile.absolutePath, format)
            } catch (e: Exception) {
                _isModelLoaded.value = false
            }
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank() || _isGenerating.value) return

        if (!_isModelLoaded.value) {
            val errorMessage = ChatMessage(
                content = "Error: No hay un modelo cargado. Selecciona un modelo .task en Configuracion.",
                isUser = false
            )
            _messages.update { it + errorMessage }
            return
        }

        val userMessage = ChatMessage(content = message, isUser = true)
        _messages.update { it + userMessage }
        _isWelcomeScreen.value = false
        _isGenerating.value = true
        _currentResponse.value = ""

        viewModelScope.launch {
            try {
                var fullResponse = ""
                chatUseCase.generateResponse(
                    prompt = message,
                    temperature = _temperature.value,
                    maxTokens = _maxTokens.value
                ).collect { chunk ->
                    fullResponse += chunk
                    _currentResponse.value = fullResponse
                }

                val assistantMessage = ChatMessage(content = fullResponse, isUser = false)
                _messages.update { it + assistantMessage }
                _currentResponse.value = ""
                _isGenerating.value = false
                saveCurrentConversation()
            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Error al generar respuesta: ${e.message}",
                    isUser = false
                )
                _messages.update { it + errorMessage }
                _currentResponse.value = ""
                _isGenerating.value = false
            }
        }
    }

    fun regenerateLastResponse() {
        val messagesList = _messages.value
        if (messagesList.isEmpty()) return
        val lastUserMessage = messagesList.findLast { it.isUser } ?: return
        _messages.update { it.dropLast(1) }
        sendMessage(lastUserMessage.content)
    }

    fun newConversation() {
        if (_messages.value.isNotEmpty()) {
            viewModelScope.launch { saveCurrentConversation() }
        }
        _messages.value = emptyList()
        _isWelcomeScreen.value = true
        currentConversationId = null
        _currentResponse.value = ""
        _isGenerating.value = false
    }

    private suspend fun saveCurrentConversation() {
        val currentMessages = _messages.value
        if (currentMessages.isEmpty()) return
        val title = currentMessages.firstOrNull { it.isUser }?.content?.take(30) ?: "Nueva conversacion"
        val preview = currentMessages.lastOrNull { !it.isUser }?.content?.take(60) ?: ""
        val conversation = Conversation(
            id = currentConversationId ?: 0,
            title = title,
            preview = preview,
            messages = currentMessages,
            timestamp = System.currentTimeMillis()
        )
        try {
            if (currentConversationId != null) {
                repository.updateConversation(conversation)
            } else {
                val id = repository.saveConversation(conversation)
                currentConversationId = id
            }
        } catch (e: Exception) { }
    }

    fun loadConversation(conversationId: Long) {
        viewModelScope.launch {
            try {
                repository.getConversationById(conversationId)?.let { conversation ->
                    if (_messages.value.isNotEmpty()) saveCurrentConversation()
                    _messages.value = conversation.messages
                    _isWelcomeScreen.value = false
                    currentConversationId = conversation.id
                    _isGenerating.value = false
                }
            } catch (e: Exception) { }
        }
    }

    fun copyMessage(content: String) {
        try {
            val clipboard = getApplication<Application>()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("message", content))
        } catch (e: Exception) { }
    }

    fun shareMessage(context: Context, content: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Compartir mensaje"))
        } catch (e: Exception) { }
    }

    fun getAllConversations(): Flow<List<Conversation>> = repository.getAllConversations()
    fun searchConversations(query: String): Flow<List<Conversation>> = repository.searchConversations(query)

    fun togglePin(conversation: Conversation) {
        viewModelScope.launch {
            try {
                repository.updateConversation(conversation.copy(isPinned = !conversation.isPinned))
            } catch (e: Exception) { }
        }
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            try {
                repository.deleteConversation(conversation)
                if (conversation.id == currentConversationId) {
                    _messages.value = emptyList()
                    _isWelcomeScreen.value = true
                    currentConversationId = null
                }
            } catch (e: Exception) { }
        }
    }

    fun deleteAllConversations() {
        viewModelScope.launch {
            try {
                repository.deleteAllConversations()
                _messages.value = emptyList()
                _isWelcomeScreen.value = true
                currentConversationId = null
            } catch (e: Exception) { }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch { saveCurrentConversation() }
        chatUseCase.unloadModel()
    }
}