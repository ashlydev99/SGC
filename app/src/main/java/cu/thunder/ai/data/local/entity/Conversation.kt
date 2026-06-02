package cu.thunder.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cu.thunder.ai.domain.model.ChatMessage

@Entity(tableName = "conversations")
@TypeConverters(Converters::class)
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val preview: String,
    val messages: List<ChatMessage>,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromChatMessageList(value: List<ChatMessage>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toChatMessageList(value: String): List<ChatMessage> {
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(value, type)
    }
}