package cu.thunder.ai.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cu.thunder.ai.data.ChatHistory
import cu.thunder.ai.data.ChatHistoryDatabase
import cu.thunder.ai.data.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ChatHistoryManager(private val context: Context) {
    private val dao = ChatHistoryDatabase.getInstance(context).chatHistoryDao()
    private val gson = Gson()
    
    fun getAllHistories(): Flow<List<ChatHistory>> = dao.getAllHistories()
    
    fun searchHistories(query: String): Flow<List<ChatHistory>> = dao.searchHistories(query)
    
    suspend fun saveCurrentChat(messages: List<ChatMessage>, title: String): Long {
        val messagesJson = gson.toJson(messages)
        val preview = messages.firstOrNull { !it.isUser }?.content?.take(60) ?: "Nueva conversación"
        
        return dao.insert(
            ChatHistory(
                title = title,
                preview = preview,
                messagesJson = messagesJson
            )
        )
    }
    
    suspend fun updateChat(history: ChatHistory, messages: List<ChatMessage>) {
        val updatedHistory = history.copy(
            messagesJson = gson.toJson(messages),
            preview = messages.firstOrNull { !it.isUser }?.content?.take(60) ?: history.preview
        )
        dao.update(updatedHistory)
    }
    
    suspend fun loadChat(historyId: Long): List<ChatMessage> {
        val history = dao.getAllHistories().first().find { it.id == historyId }
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(history?.messagesJson, type) ?: emptyList()
    }
    
    suspend fun deleteHistory(history: ChatHistory) = dao.delete(history)
    
    suspend fun togglePinned(history: ChatHistory) = dao.setPinned(history.id, !history.isPinned)
    
    suspend fun deleteAll() = dao.deleteAll()
}