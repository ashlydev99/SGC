package cu.thunder.ai.viewmodel

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cu.thunder.ai.data.ChatMessage
import cu.thunder.ai.data.PreferencesManager
import cu.thunder.ai.llm.LlamaHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val contentResolver: ContentResolver,
    private val prefs: PreferencesManager
) : ViewModel() {

    // Estado del chat
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    private val _modelPath = MutableStateFlow("")
    val modelPath: StateFlow<String> = _modelPath.asStateFlow()

    // Instancia del motor LLM
    private var llamaHelper: LlamaHelper? = null
    
    // Guardar el último mensaje del usuario para regeneración
    private var lastUserMessage: String? = null

    init {
        viewModelScope.launch {
            prefs.modelPathFlow.collect { path ->
                _modelPath.value = path
                if (path.isNotBlank()) {
                    loadModel(path)
                }
            }
        }
    }

    /**
     * Carga un modelo desde un URI (seleccionado por el usuario)
     * @param uriString URI del archivo .gguf
     */
    fun loadModel(uriString: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadError.value = null

            try {
                llamaHelper = LlamaHelper(contentResolver, viewModelScope)
                llamaHelper?.load(uriString) { success, error ->
                    if (success) {
                        _isModelLoaded.value = true
                        _loadError.value = null
                        // Mensaje de bienvenida
                        _messages.value = listOf(
                            ChatMessage(
                                content = "¡Hola! Soy ThunderAI, tu asistente personal. Todo se procesa en tu dispositivo. ¿En qué puedo ayudarte hoy?",
                                isUser = false
                            )
                        )
                    } else {
                        _isModelLoaded.value = false
                        _loadError.value = error ?: "Error desconocido al cargar el modelo"
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _isModelLoaded.value = false
                _loadError.value = e.message ?: "Error al inicializar el motor LLM"
            }
        }
    }

    /**
     * Envía un mensaje al modelo y genera respuesta
     * @param content Texto del mensaje del usuario
     */
    fun sendMessage(content: String) {
        if (!_isModelLoaded.value || _isLoading.value) return

        viewModelScope.launch {
            // Guardar el mensaje del usuario para posible regeneración
            lastUserMessage = content
            
            // Añadir mensaje del usuario a la lista
            val userMessage = ChatMessage(content = content, isUser = true)
            _messages.value = _messages.value + userMessage

            _isLoading.value = true
            _currentResponse.value = ""

            var fullResponse = ""
            llamaHelper?.generate(
                prompt = content,
                onToken = { token ->
                    fullResponse += token
                    _currentResponse.value = fullResponse
                },
                onComplete = {
                    // Añadir la respuesta completa a la lista de mensajes
                    _messages.value = _messages.value + ChatMessage(
                        content = fullResponse,
                        isUser = false
                    )
                    _currentResponse.value = ""
                    _isLoading.value = false
                },
                onError = { error ->
                    _messages.value = _messages.value + ChatMessage(
                        content = "❌ Error: $error",
                        isUser = false
                    )
                    _currentResponse.value = ""
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Regenera el último mensaje del asistente
     * @param originalPrompt El prompt original del usuario
     */
    fun regenerateLastMessage(originalPrompt: String) {
        if (!_isModelLoaded.value || _isLoading.value) return

        viewModelScope.launch {
            // Eliminar el último mensaje del asistente si existe
            val currentMessages = _messages.value.toMutableList()
            if (currentMessages.isNotEmpty() && !currentMessages.last().isUser) {
                currentMessages.removeAt(currentMessages.size - 1)
                _messages.value = currentMessages
            }

            // Actualizar el último mensaje del usuario guardado
            lastUserMessage = originalPrompt
            
            _isLoading.value = true
            _currentResponse.value = ""

            var fullResponse = ""
            llamaHelper?.generate(
                prompt = originalPrompt,
                onToken = { token ->
                    fullResponse += token
                    _currentResponse.value = fullResponse
                },
                onComplete = {
                    _messages.value = _messages.value + ChatMessage(
                        content = fullResponse,
                        isUser = false
                    )
                    _currentResponse.value = ""
                    _isLoading.value = false
                },
                onError = { error ->
                    _messages.value = _messages.value + ChatMessage(
                        content = "❌ Error: $error",
                        isUser = false
                    )
                    _currentResponse.value = ""
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Cancela la generación actual
     */
    fun cancelGeneration() {
        llamaHelper?.cancel()
        _currentResponse.value = ""
        _isLoading.value = false
    }

    /**
     * Limpia todo el historial del chat actual
     */
    fun clearChat() {
        _messages.value = emptyList()
        _currentResponse.value = ""
        lastUserMessage = null
    }

    /**
     * Restaura una conversación guardada previamente
     * @param messages Lista de mensajes a restaurar
     */
    fun restoreChat(messages: List<ChatMessage>) {
        _messages.value = messages
        _currentResponse.value = ""
        _isLoading.value = false
    }

    /**
     * Establece un mensaje de error manualmente
     * @param error Mensaje de error
     */
    fun setLoadError(error: String) {
        _loadError.value = error
    }

    /**
     * Libera recursos del modelo cuando se destruye el ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        llamaHelper?.release()
    }

    /**
     * Factory para crear instancias de MainViewModel
     */
    class Factory(
        private val contentResolver: ContentResolver,
        private val prefs: PreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(contentResolver, prefs) as T
        }
    }
}