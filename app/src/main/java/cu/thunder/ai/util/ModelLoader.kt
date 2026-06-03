package cu.thunder.ai.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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

    /**
     * Obtiene un nombre de archivo válido desde la URI
     */
    private fun getFileName(context: Context, uri: Uri): String {
        var name = "model.bin"
        
        // Intentar obtener el nombre real del archivo
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        
        // Sanitizar: eliminar caracteres problemáticos y espacios
        name = name.replace(Regex("[/\\\\:*?\"<>|]"), "_") // Reemplazar caracteres no válidos
            .trim()                                          // Quitar espacios al inicio/final
            .replace(Regex("\\s+"), "_")                     // Reemplazar espacios por guiones bajos
        
        // Si el nombre quedó vacío, usar uno por defecto
        if (name.isBlank()) name = "model.bin"
        
        // Asegurar que tenga extensión
        if (!name.contains(".")) {
            name += ".bin"
        }
        
        return name
    }

    suspend fun loadModel(
        context: Context,
        uri: Uri
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = getFileName(context, uri)
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

            // Copiar archivo
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                modelFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IllegalStateException("No se pudo abrir el archivo")

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