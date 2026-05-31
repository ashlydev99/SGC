package cu.thunder.ai.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {
    @Query("SELECT * FROM chat_histories ORDER BY isPinned DESC, timestamp DESC")
    fun getAllHistories(): Flow<List<ChatHistory>>
    
    @Query("SELECT * FROM chat_histories WHERE title LIKE '%' || :query || '%' OR preview LIKE '%' || :query || '%'")
    fun searchHistories(query: String): Flow<List<ChatHistory>>
    
    @Insert
    suspend fun insert(history: ChatHistory): Long
    
    @Update
    suspend fun update(history: ChatHistory)
    
    @Delete
    suspend fun delete(history: ChatHistory)
    
    @Query("UPDATE chat_histories SET isPinned = :isPinned WHERE id = :id")
    suspend fun setPinned(id: Long, isPinned: Boolean)
    
    @Query("DELETE FROM chat_histories")
    suspend fun deleteAll()
}