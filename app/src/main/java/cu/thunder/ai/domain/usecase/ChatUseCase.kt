package cu.thunder.ai.domain.usecase

import android.content.Context
import android.util.Log
import cu.thunder.ai.domain.model.ModelFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ChatUseCase {
    private var isModelLoaded = false
    private var modelFormat: ModelFormat = ModelFormat.UNKNOWN
    private var modelPath: String = ""
    private var appContext: Context? = null
    private var mediaPipeInference: Any? = null

    fun setContext(context: Context) {
        this.appContext = context
    }

    fun loadModel(path: String, format: ModelFormat): Boolean {
        return try {
            unloadModel()
            modelPath = path
            modelFormat = format
            
            when (format) {
                ModelFormat.TASK -> {
                    Log.d("ThunderAI", "Modelo TASK: $path")
                    isModelLoaded = true
                }
                else -> {
                    isModelLoaded = false
                }
            }
            isModelLoaded
        } catch (e: Exception) {
            Log.e("ThunderAI", "Error: ${e.message}")
            isModelLoaded = false
            false
        }
    }

    fun unloadModel() {
        try {
            if (mediaPipeInference != null) {
                try {
                    mediaPipeInference?.javaClass?.getMethod("close")?.invoke(mediaPipeInference)
                } catch (e: Exception) {}
                mediaPipeInference = null
            }
        } catch (e: Exception) {}
        
        isModelLoaded = false
        modelFormat = ModelFormat.UNKNOWN
        modelPath = ""
    }

    fun generateResponse(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ): Flow<String> = flow {
        if (!isModelLoaded) {
            emit("Error: No hay un modelo cargado.")
            return@flow
        }

        if (modelPath.isEmpty()) {
            emit("Error: Ruta del modelo no encontrada.")
            return@flow
        }

        try {
            when (modelFormat) {
                ModelFormat.TASK -> generateWithMediaPipe(prompt, temperature, maxTokens)
                else -> emit("Formato no soportado.")
            }
        } catch (e: Exception) {
            emit("\n\nError: ${e.message}")
        }
    }

    private suspend fun FlowCollector<String>.generateWithMediaPipe(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ) {
        try {
            val response = withContext(Dispatchers.IO) {
                if (mediaPipeInference == null) {
                    try {
                        val optionsClass = Class.forName(
                            "com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions"
                        )
                        val inferenceClass = Class.forName(
                            "com.google.mediapipe.tasks.genai.llminference.LlmInference"
                        )
                        
                        val builder = optionsClass.getMethod("builder").invoke(null)
                        builder.javaClass.getMethod("setModelPath", String::class.java)
                            .invoke(builder, modelPath)
                        builder.javaClass.getMethod("setMaxTokens", Int::class.java)
                            .invoke(builder, maxTokens)
                        builder.javaClass.getMethod("setTemperature", Float::class.java)
                            .invoke(builder, temperature)
                        builder.javaClass.getMethod("setTopK", Int::class.java)
                            .invoke(builder, 40)
                        builder.javaClass.getMethod("setRandomSeed", Int::class.java)
                            .invoke(builder, 0)
                        
                        val options = builder.javaClass.getMethod("build").invoke(builder)
                        mediaPipeInference = inferenceClass
                            .getMethod("createFromOptions", Context::class.java, optionsClass)
                            .invoke(null, appContext, options)
                        
                        Log.d("ThunderAI", "MediaPipe inicializado")
                    } catch (e: Exception) {
                        Log.e("ThunderAI", "Error inicializando: ${e.message}")
                        return@withContext "Error al cargar MediaPipe: ${e.message}"
                    }
                }

                try {
                    mediaPipeInference?.javaClass
                        ?.getMethod("generateResponse", String::class.java)
                        ?.invoke(mediaPipeInference, prompt) as? String
                        ?: "Error: Respuesta nula"
                } catch (e: Exception) {
                    "Error en inferencia: ${e.message}"
                }
            }

            val cleanResponse = response
                .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "")
                .trim()

            if (cleanResponse.startsWith("Error")) {
                emit(cleanResponse)
                return
            }

            if (cleanResponse.isBlank()) {
                emit("El modelo genero una respuesta vacia.")
                return
            }

            val words = cleanResponse.split(" ")
            for ((index, word) in words.withIndex()) {
                emit(if (index < words.size - 1) "$word " else word)
                delay(15)
            }
        } catch (e: Exception) {
            emit("\n\nError: ${e.message}")
        }
    }

    fun isModelReady(): Boolean = isModelLoaded
    fun getModelFormat(): ModelFormat = modelFormat
    fun getModelPath(): String = modelPath
}