package cu.sg.system.data.repository

import cu.sg.system.data.local.dao.ServiceDao
import cu.sg.system.data.local.entity.ServiceEntity
import cu.sg.system.domain.model.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepository(private val serviceDao: ServiceDao) {
    
    fun getAllServices(): Flow<List<Service>> {
        return serviceDao.getAllServices().map { list ->
            list.map { it.toDomainModel() }
        }
    }
    
    suspend fun getServiceById(id: Long): Service? {
        return serviceDao.getServiceById(id)?.toDomainModel()
    }
    
    suspend fun createService(service: Service): Long {
        val entity = service.toEntity()
        return serviceDao.insertService(entity)
    }
    
    suspend fun updateService(service: Service) {
        serviceDao.updateService(service.toEntity())
    }
    
    suspend fun deleteService(service: Service) {
        serviceDao.deleteService(service.toEntity())
    }
    
    private fun ServiceEntity.toDomainModel(): Service {
        return Service(
            id = id,
            name = name,
            type = type,
            price = price,
            createdAt = createdAt
        )
    }
    
    private fun Service.toEntity(): ServiceEntity {
        return ServiceEntity(
            id = id,
            name = name,
            type = type,
            price = price,
            createdAt = createdAt
        )
    }
}