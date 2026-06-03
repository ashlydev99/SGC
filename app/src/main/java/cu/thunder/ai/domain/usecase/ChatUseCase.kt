package cu.thunder.ai.domain.usecase

import android.content.Context
import cu.thunder.ai.domain.model.ModelFormat
import cu.thunder.ai.llama.LlamaNative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ChatUseCase {
    private var isModelLoaded = false
    private var modelFormat: ModelFormat = ModelFormat.UNKNOWN
    private var modelPath: String = ""
    private var appContext: Context? = null
    
    // Punteros a los modelos cargados
    private var llamaModelPtr: Long = 0
    private var mediaPipeInference: Any? = null // MediaPipe LlmInference

    fun setContext(context: Context) {
        this.appContext = context
    }

    fun loadModel(path: String, format: ModelFormat): Boolean {
        return try {
            // Descargar modelo anterior si existe
            unloadModel()
            
            modelPath = path
            modelFormat = format
            
            when (format) {
                ModelFormat.GGUF -> {
                    llamaModelPtr = LlamaNative.loadModel(path)
                    isModelLoaded = llamaModelPtr != 0L
                }
                ModelFormat.TASK -> {
                    // MediaPipe se carga bajo demanda en generateResponse
                    isModelLoaded = true
                }
                ModelFormat.UNKNOWN -> {
                    isModelLoaded = false
                }
            }
            
            isModelLoaded
        } catch (e: Exception) {
            isModelLoaded = false
            false
        }
    }

    fun unloadModel() {
        try {
            when (modelFormat) {
                ModelFormat.GGUF -> {
                    if (llamaModelPtr != 0L) {
                        LlamaNative.freeModel(llamaModelPtr)
                        llamaModelPtr = 0
                    }
                }
                ModelFormat.TASK -> {
                    // Cerrar MediaPipe si está abierto
                    if (mediaPipeInference != null) {
                        try {
                            // Llamar al método close() por reflexión
                            mediaPipeInference?.javaClass?.getMethod("close")?.invoke(mediaPipeInference)
                        } catch (e: Exception) {
                            // Ignorar
                        }
                        mediaPipeInference = null
                    }
                }
                ModelFormat.UNKNOWN -> {}
            }
        } catch (e: Exception) {
            // Ignorar errores al descargar
        }
        
        isModelLoaded = false
        modelFormat = ModelFormat.UNKNOWN
        modelPath = ""
        llamaModelPtr = 0
        mediaPipeInference = null
    }

    fun generateResponse(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ): Flow<String> = flow {
        if (!isModelLoaded) {
            emit("❌ Error: No hay un modelo cargado. Ve a Configuración para seleccionar uno.")
            return@flow
        }

        if (modelPath.isEmpty()) {
            emit("❌ Error: Ruta del modelo no encontrada.")
            return@flow
        }

        try {
            when (modelFormat) {
                ModelFormat.GGUF -> generateWithLlamaCpp(prompt, temperature, maxTokens)
                ModelFormat.TASK -> generateWithMediaPipe(prompt, temperature, maxTokens)
                ModelFormat.UNKNOWN -> emit("❌ Formato de modelo no soportado.")
            }
        } catch (e: Exception) {
            emit("\n\n⚠️ Error en generación: ${e.message}")
        }
    }

    // ============================================================
    // 🦙 MOTOR 1: llama.cpp (archivos .gguf)
    // ============================================================
    private suspend fun kotlinx.coroutines.flow.FlowCollector<String>.generateWithLlamaCpp(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ) {
        if (llamaModelPtr == 0L) {
            emit("❌ Error: Puntero de modelo llama.cpp inválido.")
            return
        }

        try {
            // Ejecutar inferencia en hilo de fondo
            val response = withContext(Dispatchers.IO) {
                LlamaNative.generate(
                    llamaModelPtr,
                    prompt,
                    temperature.coerceIn(0.1f, 2.0f),
                    maxTokens.coerceIn(1, 4096)
                )
            }

            // Limpiar respuesta de caracteres no deseados
            val cleanResponse = response
                .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "") // Quitar caracteres de control
                .trim()

            if (cleanResponse.isBlank()) {
                emit("⚠️ El modelo generó una respuesta vacía. Intenta con otro prompt o ajusta la temperatura.")
                return
            }

            // Emitir palabra por palabra para simular streaming
            val words = cleanResponse.split(" ")
            for ((index, word) in words.withIndex()) {
                emit(if (index < words.size - 1) "$word " else word)
                delay(20) // Pequeño delay para streaming suave
            }
        } catch (e: Exception) {
            emit("\n\n❌ Error en llama.cpp: ${e.message}")
        }
    }

    // ============================================================
    // 🧠 MOTOR 2: MediaPipe Tasks GenAI (archivos .task)
    // ============================================================
    private suspend fun kotlinx.coroutines.flow.FlowCollector<String>.generateWithMediaPipe(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ) {
        try {
            val response = withContext(Dispatchers.IO) {
                // Cargar MediaPipe si no está cargado
                if (mediaPipeInference == null) {
                    try {
                        // Usar reflexión para cargar MediaPipe (evita errores de compilación si no está disponible)
                        val optionsClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions")
                        val builderClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference\$LlmInferenceOptions\$Builder")
                        val inferenceClass = Class.forName("com.google.mediapipe.tasks.genai.llminference.LlmInference")
                        
                        val builder = optionsClass.getMethod("builder").invoke(null)
                        builder.javaClass.getMethod("setModelPath", String::class.java).invoke(builder, modelPath)
                        builder.javaClass.getMethod("setMaxTokens", Int::class.java).invoke(builder, maxTokens)
                        builder.javaClass.getMethod("setTemperature", Float::class.java).invoke(builder, temperature)
                        builder.javaClass.getMethod("setTopK", Int::class.java).invoke(builder, 40)
                        builder.javaClass.getMethod("setRandomSeed", Int::class.java).invoke(builder, 0)
                        
                        val options = builder.javaClass.getMethod("build").invoke(builder)
                        mediaPipeInference = inferenceClass
                            .getMethod("createFromOptions", Context::class.java, optionsClass)
                            .invoke(null, appContext, options)
                    } catch (e: ClassNotFoundException) {
                        return@withContext "❌ MediaPipe Tasks GenAI no está disponible. Asegúrate de tener la dependencia en build.gradle.kts:\nimplementation(\"com.google.mediapipe:tasks-genai:0.10.8\")"
                    } catch (e: Exception) {
                        return@withContext "❌ Error al cargar MediaPipe: ${e.message}"
                    }
                }

                // Generar respuesta
                try {
                    mediaPipeInference?.javaClass
                        ?.getMethod("generateResponse", String::class.java)
                        ?.invoke(mediaPipeInference, prompt) as? String
                        ?: "❌ Respuesta nula de MediaPipe"
                } catch (e: Exception) {
                    "❌ Error en inferencia MediaPipe: ${e.message}"
                }
            }

            // Limpiar respuesta
            val cleanResponse = response
                .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), "")
                .trim()

            if (cleanResponse.startsWith("❌")) {
                emit(cleanResponse)
                return
            }

            if (cleanResponse.isBlank()) {
                emit("⚠️ El modelo generó una respuesta vacía.")
                return
            }

            // Streaming palabra por palabra
            val words = cleanResponse.split(" ")
            for ((index, word) in words.withIndex()) {
                emit(if (index < words.size - 1) "$word " else word)
                delay(15)
            }
        } catch (e: Exception) {
            emit("\n\n❌ Error en MediaPipe: ${e.message}")
        }
    }

    fun isModelReady(): Boolean = isModelLoaded
    fun getModelFormat(): ModelFormat = modelFormat
    fun getModelPath(): String = modelPath
}