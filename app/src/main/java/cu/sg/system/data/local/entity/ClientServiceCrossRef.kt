package cu.sg.system.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "client_service_cross_ref",
    primaryKeys = ["clientUid", "serviceId"],
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["uid"],
            childColumns = ["clientUid"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("clientUid"),
        Index("serviceId")
    ]
)
data class ClientServiceCrossRef(
    val clientUid: String,
    val serviceId: Long
)