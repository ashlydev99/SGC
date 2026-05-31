package cu.thunder.ai.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_histories")
data class ChatHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val preview: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val messagesJson: String // Guardar lista de mensajes como JSON
)