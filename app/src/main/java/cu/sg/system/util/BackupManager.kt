package cu.sg.system.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context) {
    
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    fun createBackup(): Boolean {
        return try {
            val dbFile = context.getDatabasePath("sgc_database")
            val backupDir = File(context.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) backupDir.mkdirs()
            
            val backupFile = File(backupDir, "SGC_Backup_${dateFormat.format(Date())}.db")
            
            FileInputStream(dbFile).use { input ->
                FileOutputStream(backupFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            Toast.makeText(context, "Backup creado: ${backupFile.name}", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Error al crear backup: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
    
    fun restoreBackup(uri: Uri): Boolean {
        return try {
            val dbFile = context.getDatabasePath("sgc_database")
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            Toast.makeText(context, "Backup restaurado. Reinicie la app.", Toast.LENGTH_LONG).show()
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Error al restaurar: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
    
    fun getBackupFiles(): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        if (!backupDir.exists()) return emptyList()
        return backupDir.listFiles()?.filter { it.extension == "db" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
}