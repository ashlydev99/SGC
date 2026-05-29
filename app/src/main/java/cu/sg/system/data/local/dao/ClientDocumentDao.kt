package cu.sg.system.data.local.dao

import androidx.room.*
import cu.sg.system.data.local.entity.ClientDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDocumentDao {
    @Query("SELECT * FROM client_documents WHERE clientUid = :clientUid ORDER BY createdAt DESC")
    fun getDocumentsByClient(clientUid: String): Flow<List<ClientDocumentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: ClientDocumentEntity): Long
    
    @Delete
    suspend fun deleteDocument(document: ClientDocumentEntity)
}