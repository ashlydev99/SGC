package cu.thunder.ai.data.repository

import cu.thunder.ai.data.local.dao.ConversationDao
import cu.thunder.ai.data.local.entity.Conversation
import kotlinx.coroutines.flow.Flow

class ConversationRepository(private val dao: ConversationDao) {
    fun getAllConversations(): Flow<List<Conversation>> = dao.getAllConversations()
    
    fun searchConversations(query: String): Flow<List<Conversation>> = dao.searchConversations(query)
    
    suspend fun getConversationById(id: Long): Conversation? = dao.getConversationById(id)
    
    suspend fun saveConversation(conversation: Conversation): Long = dao.insertConversation(conversation)
    
    suspend fun updateConversation(conversation: Conversation) = dao.updateConversation(conversation)
    
    suspend fun deleteConversation(conversation: Conversation) = dao.deleteConversation(conversation)
    
    suspend fun deleteAllConversations() = dao.deleteAllConversations()
}