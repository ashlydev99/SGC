package cu.thunder.ai.domain.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ModelFormat {
    GGUF,
    TASK,
    UNKNOWN
}

data class UserPreferences(
    val userName: String = "Usuario",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val modelPath: String? = null
)