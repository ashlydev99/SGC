package cu.sg.system.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import cu.sg.system.domain.model.Client

class ShareManager(private val context: Context) {
    
    fun shareClientInfo(client: Client) {
        val text = buildString {
            appendLine("📋 *FICHA DE CLIENTE*")
            appendLine("━━━━━━━━━━━━━━━━")
            appendLine("🆔 *UID:* ${client.uid}")
            appendLine("👤 *Nombre:* ${client.firstName} ${client.lastName}")
            appendLine("🪪 *CI:* ${client.ci}")
            appendLine("📞 *Contacto:* ${client.contact}")
            appendLine("📊 *Estado:* ${client.status}")
            appendLine("━━━━━━━━━━━━━━━━")
            appendLine("🛠️ *Servicios:*")
            client.services.forEach { service ->
                appendLine("  • ${service.name}: $${String.format("%.2f", service.price)}")
            }
            appendLine("💰 *Total:* $${String.format("%.2f", client.services.sumOf { it.price })}")
        }
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Ficha de Cliente: ${client.firstName} ${client.lastName}")
        }
        context.startActivity(Intent.createChooser(intent, "Compartir ficha"))
    }
    
    fun shareViaWhatsApp(client: Client) {
        val text = buildString {
            appendLine("*SGC - Estado del Trámite*")
            appendLine("━━━━━━━━━━━━━━━━")
            appendLine("Cliente: *${client.firstName} ${client.lastName}*")
            appendLine("UID: ${client.uid}")
            appendLine("Estado: *${client.status}*")
            appendLine("━━━━━━━━━━━━━━━━")
            appendLine("Servicios:")
            client.services.forEach { service ->
                appendLine("  ✅ ${service.name}: $${String.format("%.2f", service.price)}")
            }
            appendLine("Total: *$${String.format("%.2f", client.services.sumOf { it.price })}*")
        }
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(text)}")
        }
        context.startActivity(intent)
    }
}