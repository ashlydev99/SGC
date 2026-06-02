package cu.thunder.ai.ui.screens.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.*

@Composable
fun MessageActions(
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onCopyClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copiar",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }

        IconButton(
            onClick = onRegenerateClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Regenerar",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}