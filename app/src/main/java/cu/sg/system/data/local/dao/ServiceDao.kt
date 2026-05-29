package cu.sg.system.data.local.dao

import androidx.room.*
import cu.sg.system.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY name ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>
    
    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: Long): ServiceEntity?
    
    @Query("SELECT * FROM services ORDER BY name ASC")
    suspend fun getAllServicesForExport(): List<ServiceEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long
    
    @Update
    suspend fun updateService(service: ServiceEntity)
    
    @Delete
    suspend fun deleteService(service: ServiceEntity)
}