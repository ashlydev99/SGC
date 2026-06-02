package cu.thunder.ai.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cu.thunder.ai.ui.screens.chat.ChatViewModel
import cu.thunder.ai.ui.theme.*
import cu.thunder.ai.util.ModelLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    
    // Obtener valores del ViewModel (que ya tiene SharedPreferences)
    val userName by chatViewModel.userName.collectAsState()
    val temperature by chatViewModel.temperature.collectAsState()
    val maxTokens by chatViewModel.maxTokens.collectAsState()
    val modelPath by chatViewModel.modelPath.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isModelLoaded = modelPath != null

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = uri.lastPathSegment ?: "model.bin"
            chatViewModel.updateModelPath(fileName)
            // Copiar modelo a almacenamiento interno
            ModelLoader.loadModel(context, uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Configuracion", color = ElectricBlue)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BattleNetDark
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BattleNetDark,
                            SurfaceDark,
                            SurfaceMedium.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Seccion de Perfil
            Text("Perfil", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceMedium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.first().uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = BattleNetDark
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = userName, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(text = "Usuario", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ElectricBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seccion de Modelo
            Text("Modelo de IA", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceMedium)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Estado", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(
                                if (isModelLoaded) "Cargado" else "No cargado",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isModelLoaded) SuccessGreen else ErrorRed
                            )
                        }
                        Icon(
                            if (isModelLoaded) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isModelLoaded) SuccessGreen else ErrorRed
                        )
                    }

                    if (modelPath != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = modelPath?.takeLast(50) ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar modelo")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seccion de Parametros
            Text("Parametros de generacion", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceMedium)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Temperatura: $temperature", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Slider(
                        value = temperature,
                        onValueChange = { chatViewModel.updateTemperature(it) },
                        valueRange = 0.1f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = ElectricBlue,
                            inactiveTrackColor = SurfaceMedium
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Maximo de tokens: $maxTokens", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Slider(
                        value = maxTokens.toFloat(),
                        onValueChange = { chatViewModel.updateMaxTokens(it.toInt()) },
                        valueRange = 256f..4096f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = ElectricBlue,
                            inactiveTrackColor = SurfaceMedium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seccion de Ayuda
            Text("Ayuda", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceMedium)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = """
                            Formatos soportados:
                            • .gguf - Modelos para llama.cpp
                            • .task - Modelos para MediaPipe
                            
                            Puedes descargar modelos desde:
                            • Hugging Face (huggingface.co)
                            • Busca "GGUF" o "MediaPipe"
                            
                            Recomendaciones:
                            • Modelos pequeños: 1-3 GB
                            • Temperatura: 0.7 (creativo)
                            • Tokens: 2048 (equilibrado)
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Limpiar historial actual")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("© 2025 AshlyDev", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                Text("Ashly Castell", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                Text("Version 1.0.0", style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
            }
        }
    }

    // Dialogo para editar nombre
    if (showEditDialog) {
        var newName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = { Text("Ingresa tu nombre") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = ElectricBlue,
                        focusedBorderColor = ElectricBlue
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    chatViewModel.updateUserName(newName)
                    showEditDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialogo para confirmar eliminacion
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Limpiar historial") },
            text = { Text("¿Estas seguro de que deseas eliminar todo el historial de conversacion?") },
            confirmButton = {
                TextButton(onClick = {
                    chatViewModel.deleteAllConversations()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}