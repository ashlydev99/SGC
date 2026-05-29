package cu.sg.system.domain.model

data class Service(
    val id: Long = 0,
    val name: String,
    val type: String,
    val price: Double,
    val createdAt: Long = System.currentTimeMillis()
)