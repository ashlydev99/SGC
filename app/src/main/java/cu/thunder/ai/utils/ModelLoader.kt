package cu.thunder.ai.util

import android.content.Context
import android.net.Uri
import cu.thunder.ai.domain.model.ModelFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModelLoader {
    fun detectFormat(fileName: String): ModelFormat {
        return when {
            fileName.endsWith(".gguf", ignoreCase = true) -> ModelFormat.GGUF
            fileName.endsWith(".task", ignoreCase = true) -> ModelFormat.TASK
            else -> ModelFormat.UNKNOWN
        }
    }

    suspend fun loadModel(
        context: Context,
        uri: Uri
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = uri.lastPathSegment ?: "model.bin"
            val format = detectFormat(fileName)

            if (format == ModelFormat.UNKNOWN) {
                return@withContext Result.failure(
                    IllegalArgumentException("Formato no soportado. Usa .gguf o .task")
                )
            }

            val modelsDir = File(context.filesDir, "models").apply {
                if (!exists()) mkdirs()
            }

            val modelFile = File(modelsDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                modelFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            Result.success(modelFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isModelLoaded(context: Context, fileName: String): Boolean {
        val modelFile = File(context.filesDir, "models/$fileName")
        return modelFile.exists()
    }
}