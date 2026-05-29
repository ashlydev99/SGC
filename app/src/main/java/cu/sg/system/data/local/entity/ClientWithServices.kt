package cu.sg.system.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ClientWithServices(
    @Embedded
    val client: ClientEntity,
    @Relation(
        parentColumn = "uid",
        entityColumn = "id",
        associateBy = Junction(
            ClientServiceCrossRef::class,
            parentColumn = "clientUid",
            entityColumn = "serviceId"
        )
    )
    val services: List<ServiceEntity>
)