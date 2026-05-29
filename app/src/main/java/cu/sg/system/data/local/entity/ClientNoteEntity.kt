package cu.sg.system.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "client_notes",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["uid"],
            childColumns = ["clientUid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientUid")]
)
data class ClientNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientUid: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)