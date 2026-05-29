package cu.sg.system.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = false)
    val uid: String,
    val firstName: String,
    val secondName: String? = null,
    val lastName: String,
    val ci: String,
    val address: String? = null,
    val contact: String,
    val status: String = "En trámite",
    val createdAt: Long = System.currentTimeMillis()
)