package cu.sg.system.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import cu.sg.system.domain.model.Client
import java.io.File
import java.io.FileOutputStream

class PdfManager(private val context: Context) {
    
    fun generateClientPdf(client: Client): File? {
        return try {
            val pdfDir = File(context.getExternalFilesDir(null), "pdfs")
            if (!pdfDir.exists()) pdfDir.mkdirs()
            
            val pdfFile = File(pdfDir, "Cliente_${client.uid}.pdf")
            
            val writer = PdfWriter(FileOutputStream(pdfFile))
            val document = Document(writer)
            
            document.add(Paragraph("FICHA DE CLIENTE").setBold().setFontSize(18f))
            document.add(Paragraph("\n"))
            document.add(Paragraph("UID: ${client.uid}"))
            document.add(Paragraph("Nombre: ${client.firstName} ${client.secondName ?: ""}"))
            document.add(Paragraph("Apellidos: ${client.lastName}"))
            document.add(Paragraph("CI: ${client.ci}"))
            document.add(Paragraph("Dirección: ${client.address ?: "No especificada"}"))
            document.add(Paragraph("Contacto: ${client.contact}"))
            document.add(Paragraph("Estado: ${client.status}"))
            document.add(Paragraph("\n"))
            document.add(Paragraph("SERVICIOS:").setBold())
            
            client.services.forEach { service ->
                document.add(Paragraph("  - ${service.name} (${service.type}): $${String.format("%.2f", service.price)}"))
            }
            
            document.add(Paragraph("\n"))
            document.add(Paragraph("Total: $${String.format("%.2f", client.services.sumOf { it.price })}").setBold())
            
            document.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}