package cu.thunder.ai.ui.screens.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cu.thunder.ai.ThunderAIApp
import cu.thunder.ai.data.repository.ConversationRepository
import cu.thunder.ai.domain.model.ModelFormat
import cu.thunder.ai.util.ModelLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ConversationRepository
    
    private val _userName = MutableStateFlow("Usuario")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _modelPath = MutableStateFlow<String?>(null)
    val modelPath: StateFlow<String?> = _modelPath.asStateFlow()

    private val _modelFormat = MutableStateFlow(ModelFormat.UNKNOWN)
    val modelFormat: StateFlow<ModelFormat> = _modelFormat.asStateFlow()

    private val _isModelLoaded = MutableStateFlow(false)
    val isModelLoaded: StateFlow<Boolean> = _isModelLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _temperature = MutableStateFlow(0.7f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()

    private val _maxTokens = MutableStateFlow(2048)
    val maxTokens: StateFlow<Int> = _maxTokens.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val database = (application as ThunderAIApp).database
        repository = ConversationRepository(database.conversationDao())
        
        // Cargar preferencias guardadas
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            try {
                // Aquí cargarías las preferencias desde DataStore
                // Por ahora usamos valores por defecto
                _userName.value = "Usuario"
                _temperature.value = 0.7f
                _maxTokens.value = 2048
                
                // Verificar si hay un modelo cargado previamente
                val modelsDir = File(getApplication<Application>().filesDir, "models")
                if (modelsDir.exists()) {
                    val modelFiles = modelsDir.listFiles { file ->
                        file.name.endsWith(".gguf") || file.name.endsWith(".task")
                    }
                    if (modelFiles != null && modelFiles.isNotEmpty()) {
                        _modelPath.value = modelFiles.first().name
                        _modelFormat.value = ModelLoader.detectFormat(modelFiles.first().name)
                        _isModelLoaded.value = true
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar preferencias: ${e.message}"
            }
        }
    }

    fun loadModel(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val result = ModelLoader.loadModel(getApplication(), uri)
                
                result.onSuccess { modelFile ->
                    _modelPath.value = modelFile.name
                    _modelFormat.value = ModelLoader.detectFormat(modelFile.name)
                    _isModelLoaded.value = true
                    
                    // Guardar preferencia
                    saveModelPath(modelFile.name)
                }.onFailure { exception ->
                    _errorMessage.value = "Error al cargar el modelo: ${exception.message}"
                    _isModelLoaded.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
                _isModelLoaded.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserName(name: String) {
        _userName.value = name
        viewModelScope.launch {
            saveUserName(name)
        }
    }

    fun updateTemperature(temp: Float) {
        _temperature.value = temp
        viewModelScope.launch {
            saveTemperature(temp)
        }
    }

    fun updateMaxTokens(tokens: Int) {
        _maxTokens.value = tokens
        viewModelScope.launch {
            saveMaxTokens(tokens)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.deleteAllConversations()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al limpiar el historial: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun unloadModel() {
        viewModelScope.launch {
            try {
                val modelFile = File(getApplication<Application>().filesDir, "models/${_modelPath.value}")
                if (modelFile.exists()) {
                    modelFile.delete()
                }
                _modelPath.value = null
                _modelFormat.value = ModelFormat.UNKNOWN
                _isModelLoaded.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Error al descargar el modelo: ${e.message}"
            }
        }
    }

    fun getModelFile(): File? {
        return _modelPath.value?.let { path ->
            File(getApplication<Application>().filesDir, "models/$path")
        }
    }

    private suspend fun saveUserName(name: String) {
        // Implementar guardado en DataStore
        // preferencesDataStore.edit { preferences ->
        //     preferences[USER_NAME_KEY] = name
        // }
    }

    private suspend fun saveModelPath(path: String) {
        // Implementar guardado en DataStore
    }

    private suspend fun saveTemperature(temp: Float) {
        // Implementar guardado en DataStore
    }

    private suspend fun saveMaxTokens(tokens: Int) {
        // Implementar guardado en DataStore
    }
}