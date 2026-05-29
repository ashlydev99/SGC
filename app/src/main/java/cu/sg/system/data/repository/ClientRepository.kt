package cu.sg.system.data.repository

import cu.sg.system.data.local.dao.ClientDao
import cu.sg.system.data.local.entity.ClientEntity
import cu.sg.system.data.local.entity.ClientServiceCrossRef
import cu.sg.system.data.local.entity.ClientWithServices
import cu.sg.system.domain.model.Client
import cu.sg.system.domain.model.Service
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientRepository(private val clientDao: ClientDao) {
    
    fun getAllClients(): Flow<List<Client>> {
        return clientDao.getAllClientsWithServices().map { list ->
            list.map { it.toDomainModel() }
        }
    }
    
    suspend fun getClientByUid(uid: String): Client? {
        return clientDao.getClientWithServices(uid)?.toDomainModel()
    }
    
    suspend fun createClient(client: Client) {
        val entity = client.toEntity()
        clientDao.insertClient(entity)
        
        client.services.forEach { service ->
            if (service.id > 0) {
                clientDao.insertClientServiceCrossRef(
                    ClientServiceCrossRef(
                        clientUid = client.uid,
                        serviceId = service.id
                    )
                )
            }
        }
    }
    
    suspend fun updateClient(client: Client) {
        clientDao.updateClient(client.toEntity())
    }
    
    suspend fun updateClientStatus(uid: String, newStatus: String) {
        val client = clientDao.getClient(uid)
        client?.let {
            clientDao.updateClient(it.copy(status = newStatus))
        }
    }
    
    suspend fun deleteClient(uid: String) {
        val client = clientDao.getClient(uid)
        client?.let {
            clientDao.deleteClient(it)
        }
    }
    
    suspend fun searchClients(query: String): List<Client> {
        return clientDao.searchClients(query).map { entity ->
            val services = clientDao.getClientWithServices(entity.uid)?.services ?: emptyList()
            Client(
                uid = entity.uid,
                firstName = entity.firstName,
                secondName = entity.secondName,
                lastName = entity.lastName,
                ci = entity.ci,
                address = entity.address,
                contact = entity.contact,
                status = entity.status,
                services = services.map { it.toDomainModel() },
                createdAt = entity.createdAt
            )
        }
    }
    
    private fun ClientWithServices.toDomainModel(): Client {
        return Client(
            uid = client.uid,
            firstName = client.firstName,
            secondName = client.secondName,
            lastName = client.lastName,
            ci = client.ci,
            address = client.address,
            contact = client.contact,
            status = client.status,
            services = services.map { it.toDomainModel() },
            createdAt = client.createdAt
        )
    }
    
    private fun Client.toEntity(): ClientEntity {
        return ClientEntity(
            uid = uid,
            firstName = firstName,
            secondName = secondName,
            lastName = lastName,
            ci = ci,
            address = address,
            contact = contact,
            status = status,
            createdAt = createdAt
        )
    }
    
    private fun cu.sg.system.data.local.entity.ServiceEntity.toDomainModel(): Service {
        return Service(
            id = id,
            name = name,
            type = type,
            price = price,
            createdAt = createdAt
        )
    }
}