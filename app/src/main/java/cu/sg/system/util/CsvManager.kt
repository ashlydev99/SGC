package cu.sg.system.util

import android.content.Context
import android.widget.Toast
import cu.sg.system.data.local.entity.ClientEntity
import cu.sg.system.data.local.entity.ServiceEntity
import java.io.*

class CsvManager(private val context: Context) {
    
    fun exportClients(clients: List<ClientEntity>, outputStream: OutputStream) {
        try {
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.write("UID,Nombre,SegundoNombre,Apellidos,CI,Dirección,Contacto,Estado,FechaCreación")
            writer.newLine()
            
            clients.forEach { client ->
                writer.write("${client.uid},${client.firstName},${client.secondName ?: ""},${client.lastName},${client.ci},${client.address ?: ""},${client.contact},${client.status},${client.createdAt}")
                writer.newLine()
            }
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun importClients(inputStream: InputStream): List<ClientEntity> {
        val clients = mutableListOf<ClientEntity>()
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            reader.readLine() // Saltar cabecera
            var line: String? = reader.readLine()
            
            while (line != null) {
                val parts = line.split(",")
                if (parts.size >= 8) {
                    val client = ClientEntity(
                        uid = parts[0],
                        firstName = parts[1],
                        secondName = parts[2].ifBlank { null },
                        lastName = parts[3],
                        ci = parts[4],
                        address = parts[5].ifBlank { null },
                        contact = parts[6],
                        status = parts[7],
                        createdAt = parts.getOrNull(8)?.toLongOrNull() ?: System.currentTimeMillis()
                    )
                    clients.add(client)
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return clients
    }
    
    fun exportServices(services: List<ServiceEntity>, outputStream: OutputStream) {
        try {
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            writer.write("ID,Nombre,Tipo,Precio,FechaCreación")
            writer.newLine()
            
            services.forEach { service ->
                writer.write("${service.id},${service.name},${service.type},${service.price},${service.createdAt}")
                writer.newLine()
            }
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun importServices(inputStream: InputStream): List<ServiceEntity> {
        val services = mutableListOf<ServiceEntity>()
        try {
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            reader.readLine() // Saltar cabecera
            var line: String? = reader.readLine()
            
            while (line != null) {
                val parts = line.split(",")
                if (parts.size >= 4) {
                    val service = ServiceEntity(
                        name = parts[1],
                        type = parts[2],
                        price = parts[3].toDoubleOrNull() ?: 0.0,
                        createdAt = parts.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis()
                    )
                    services.add(service)
                }
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return services
    }
}