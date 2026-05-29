package cu.sg.system.data.local.dao

import androidx.room.*
import cu.sg.system.data.local.entity.ClientNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientNoteDao {
    @Query("SELECT * FROM client_notes WHERE clientUid = :clientUid ORDER BY createdAt DESC")
    fun getNotesByClient(clientUid: String): Flow<List<ClientNoteEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ClientNoteEntity): Long
    
    @Update
    suspend fun updateNote(note: ClientNoteEntity)
    
    @Delete
    suspend fun deleteNote(note: ClientNoteEntity)
}