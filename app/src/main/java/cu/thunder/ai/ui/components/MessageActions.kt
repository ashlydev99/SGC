package cu.thunder.ai.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.ElectricBlue

@Composable
fun MessageActions(
    content: String,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Row(modifier = modifier) {
        // Copiar
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("ThunderAI", content)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copiado al portapapeles", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copiar",
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        
        // Compartir
        IconButton(
            onClick = {
                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, content)
                    type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir con..."))
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir",
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        
        // Regenerar
        IconButton(
            onClick = onRegenerate,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Regenerar",
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}