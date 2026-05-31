package cu.thunder.ai.llm

import android.content.ContentResolver
import android.net.Uri
import com.ljcamargo.llamacpp.LlamaModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class LlamaHelper(
    private val contentResolver: ContentResolver,
    private val scope: CoroutineScope
) {
    private var model: LlamaModel? = null
    private var generationJob: Job? = null
    
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating
    
    suspend fun loadModel(uriString: String): Result<Unit> {
        return try {
            val uri = Uri.parse(uriString)
            val modelFile = copyModelToCache(uri)
                ?: return Result.failure(Exception("No se pudo copiar el modelo"))
            
            model = LlamaModel(context = null)
            model?.load(modelFile.absolutePath)
            _isLoaded.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun generate(
        prompt: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (model == null) {
            onError("Modelo no cargado")
            return
        }
        
        generationJob = scope.launch(Dispatchers.IO) {
            _isGenerating.value = true
            try {
                val fullPrompt = buildPrompt(prompt)
                model?.generate(fullPrompt) { token ->
                    onToken(token)
                }
                onComplete()
            } catch (e: Exception) {
                onError(e.message ?: "Error en generación")
            } finally {
                _isGenerating.value = false
            }
        }
    }
    
    fun cancel() {
        generationJob?.cancel()
        generationJob = null
        _isGenerating.value = false
    }
    
    private fun buildPrompt(userInput: String): String = buildString {
        append("<|im_start|>system\n")
        append("Eres ThunderAI, un asistente de IA útil, honesto y amigable.\n")
        append("<|im_end|>\n")
        append("<|im_start|>user\n")
        append(userInput)
        append("\n<|im_end|>\n")
        append("<|im_start|>assistant\n")
    }
    
    private fun copyModelToCache(uri: Uri): File? {
        return try {
            val cacheDir = File(context?.filesDir ?: return null, "models")
            cacheDir.mkdirs()
            val modelFile = File(cacheDir, "model_${System.currentTimeMillis()}.gguf")
            
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }
            modelFile
        } catch (e: Exception) {
            null
        }
    }
    
    private var context: android.content.Context? = null
    fun setContext(ctx: android.content.Context) { context = ctx }
}