package cu.thunder.ai.data

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isCodeBlock: Boolean = false
)