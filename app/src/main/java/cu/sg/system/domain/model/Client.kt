package cu.sg.system.domain.model

data class Client(
    val uid: String,
    val firstName: String,
    val secondName: String? = null,
    val lastName: String,
    val ci: String,
    val address: String? = null,
    val contact: String,
    val status: String = "En trámite",
    val services: List<Service> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)