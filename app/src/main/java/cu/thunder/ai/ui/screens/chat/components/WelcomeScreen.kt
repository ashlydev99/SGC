package cu.thunder.ai.ui.screens.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cu.thunder.ai.ui.theme.*

@Composable
fun WelcomeScreen(
    onSuggestionClick: (String) -> Unit,
    userName: String = "Usuario",
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        "💻 Escribe una función en Python",
        "📝 Redacta un correo profesional",
        "🎨 Explícame qué es la inteligencia artificial",
        "📊 Crea una lista de verificación para un proyecto"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "Hola $userName",
                style = MaterialTheme.typography.headlineLarge,
                color = ElectricBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "¿En qué puedo ayudarte hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        itemsIndexed(suggestions) { index, suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSuggestionClick(suggestion) },
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceMedium.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}