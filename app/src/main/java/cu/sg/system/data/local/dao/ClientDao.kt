package cu.sg.system.data.local.dao

import androidx.room.*
import cu.sg.system.data.local.entity.ClientEntity
import cu.sg.system.data.local.entity.ClientServiceCrossRef
import cu.sg.system.data.local.entity.ClientWithServices
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Transaction
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun getAllClientsWithServices(): Flow<List<ClientWithServices>>
    
    @Transaction
    @Query("SELECT * FROM clients WHERE uid = :uid")
    suspend fun getClientWithServices(uid: String): ClientWithServices?
    
    @Query("SELECT * FROM clients WHERE uid = :uid")
    suspend fun getClient(uid: String): ClientEntity?
    
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    suspend fun getAllClientsForExport(): List<ClientEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)
    
    @Update
    suspend fun updateClient(client: ClientEntity)
    
    @Delete
    suspend fun deleteClient(client: ClientEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientServiceCrossRef(crossRef: ClientServiceCrossRef)
    
    @Delete
    suspend fun deleteClientServiceCrossRef(crossRef: ClientServiceCrossRef)
    
    @Query("DELETE FROM client_service_cross_ref WHERE clientUid = :clientUid")
    suspend fun deleteAllServicesFromClient(clientUid: String)
    
    @Query("SELECT * FROM clients WHERE uid LIKE '%' || :query || '%' OR firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%'")
    suspend fun searchClients(query: String): List<ClientEntity>
    
    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<ClientEntity>
}