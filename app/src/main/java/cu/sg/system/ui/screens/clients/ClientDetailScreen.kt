package cu.sg.system.ui.screens.clients

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import cu.sg.system.R
import cu.sg.system.domain.model.Client
import cu.sg.system.domain.model.Service
import cu.sg.system.ui.theme.*
import cu.sg.system.ui.viewmodel.ClientViewModel
import cu.sg.system.ui.viewmodel.ServiceViewModel
import cu.sg.system.util.PdfManager
import cu.sg.system.util.ShareManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    navController: NavController,
    clientViewModel: ClientViewModel,
    serviceViewModel: ServiceViewModel,
    uid: String
) {
    val client by clientViewModel.selectedClient.collectAsState()
    val allServices by serviceViewModel.services.collectAsState()
    val notes by clientViewModel.clientNotes.collectAsState()
    val documents by clientViewModel.clientDocuments.collectAsState()
    val isLoading by clientViewModel.isLoading.collectAsState()
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showPdfDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val shareManager = remember { ShareManager(context) }
    val pdfManager = remember { PdfManager(context) }
    
    // Launcher para seleccionar PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            clientViewModel.addDocument(uid, it, context)
        }
    }
    
    LaunchedEffect(uid) {
        clientViewModel.loadClientByUid(uid)
        clientViewModel.loadClientNotes(uid)
        clientViewModel.loadClientDocuments(uid)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle del Cliente",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Compartir
                    IconButton(onClick = { client?.let { shareManager.shareClientInfo(it) } }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    // WhatsApp
                    IconButton(onClick = { client?.let { shareManager.shareViaWhatsApp(it) } }) {
                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp")
                    }
                    // Eliminar
                    IconButton(onClick = {
                        clientViewModel.deleteClient(uid)
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar cliente",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueElectric)
            }
        } else {
            client?.let { clientData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // UID Compacto
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(BlueElectric.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("UID:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(clientData.uid, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlueElectric)
                        }
                    }
                    
                    // Información Personal
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Información Personal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            DetailRow("Nombre", clientData.firstName)
                            if (!clientData.secondName.isNullOrBlank()) DetailRow("Segundo Nombre", clientData.secondName)
                            DetailRow("Apellidos", clientData.lastName)
                            DetailRow("CI", clientData.ci)
                            if (!clientData.address.isNullOrBlank()) DetailRow("Dirección", clientData.address)
                            DetailRow("Contacto", clientData.contact)
                        }
                    }
                    
                    // Servicios
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Servicios", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Row {
                                    // Generar PDF
                                    IconButton(onClick = {
                                        client?.let { pdfManager.generateClientPdf(it) }
                                        Toast.makeText(context, "PDF generado", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Generar PDF", tint = BlueElectric)
                                    }
                                    // Agregar servicio
                                    IconButton(onClick = { showAddServiceDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Agregar servicio", tint = BlueElectric)
                                    }
                                }
                            }
                            
                            if (clientData.services.isEmpty()) {
                                Text("Cliente sin servicios activos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            } else {
                                clientData.services.forEach { service ->
                                    ServiceCardWithActions(
                                        service = service,
                                        clientStatus = clientData.status,
                                        clientUid = uid,
                                        clientViewModel = clientViewModel
                                    )
                                }
                            }
                            
                            if (clientData.services.isNotEmpty()) {
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(
                                        "$${String.format("%.2f", clientData.services.sumOf { it.price })}",
                                        fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueElectric
                                    )
                                }
                            }
                        }
                    }
                    
                    // Notas
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Notas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                IconButton(onClick = { showAddNoteDialog = true }) {
                                    Icon(Icons.Default.NoteAdd, contentDescription = "Agregar nota", tint = BlueElectric)
                                }
                            }
                            
                            if (notes.isEmpty()) {
                                Text("Sin notas", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            } else {
                                notes.forEach { note ->
                                    NoteCard(note = note, onDelete = { clientViewModel.deleteNote(note.id) })
                                }
                            }
                        }
                    }
                    
                    // Documentos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Documentos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                IconButton(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                                    Icon(Icons.Default.UploadFile, contentDescription = "Subir PDF", tint = BlueElectric)
                                }
                            }
                            
                            if (documents.isEmpty()) {
                                Text("Sin documentos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            } else {
                                documents.forEach { doc ->
                                    DocumentCard(document = doc, onDelete = { clientViewModel.deleteDocument(doc.id) })
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Cliente no encontrado")
                }
            }
        }
    }
    
    // Diálogo para agregar servicio
    if (showAddServiceDialog) {
        val availableServices = allServices.filter { service ->
            client?.services?.none { it.id == service.id } ?: true
        }
        AddServiceToClientDialog(
            services = availableServices,
            onDismiss = { showAddServiceDialog = false },
            onAdd = { service ->
                client?.let {
                    val updatedServices = it.services + service
                    clientViewModel.updateClientServices(uid, updatedServices)
                }
                showAddServiceDialog = false
            }
        )
    }
    
    // Diálogo para agregar nota
    if (showAddNoteDialog) {
        var noteContent by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Agregar Nota") },
            text = {
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Contenido de la nota") },
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    maxLines = 10
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (noteContent.isNotBlank()) {
                        clientViewModel.addNote(uid, noteContent)
                        showAddNoteDialog = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showAddNoteDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun ServiceCardWithActions(
    service: Service,
    clientStatus: String,
    clientUid: String,
    clientViewModel: ClientViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val serviceStatus = when (clientStatus) {
        "En trámite" -> "En trámite"
        "Pendiente a pagar" -> "Pendiente a pagar"
        "Trámite finalizado y Entregado" -> "Entregado"
        else -> clientStatus
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Tipo: ${service.type}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Estado:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ServiceStatusBadge(status = serviceStatus)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${String.format("%.2f", service.price)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (clientStatus) {
                    "En trámite" -> {
                        SmallButton("Resuelto", StatusPendiente) {
                            clientViewModel.updateClientStatus(clientUid, "Pendiente a pagar")
                        }
                        SmallButton("Eliminar", MaterialTheme.colorScheme.error) { showDeleteDialog = true }
                    }
                    "Pendiente a pagar" -> {
                        SmallButton("Pagado", StatusSolucionado) {
                            clientViewModel.updateClientStatus(clientUid, "Trámite finalizado y Entregado")
                        }
                        SmallButton("Eliminar", MaterialTheme.colorScheme.error) { showDeleteDialog = true }
                    }
                    "Trámite finalizado y Entregado" -> {
                        SmallButton("Eliminar", MaterialTheme.colorScheme.error) { showDeleteDialog = true }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Servicio") },
            text = { Text("¿Estás seguro de eliminar este servicio del cliente?") },
            confirmButton = {
                TextButton(onClick = {
                    clientViewModel.removeServiceFromClient(clientUid, service.id)
                    showDeleteDialog = false
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun NoteCard(note: cu.sg.system.data.local.entity.ClientNoteEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.content, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(note.createdAt),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun DocumentCard(document: cu.sg.system.data.local.entity.ClientDocumentEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Column {
                    Text(document.fileName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text(
                        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(document.createdAt),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun ServiceStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "En trámite" -> Pair(StatusTramite, androidx.compose.ui.graphics.Color.White)
        "Pendiente a pagar" -> Pair(StatusPendiente, androidx.compose.ui.graphics.Color.White)
        "Entregado" -> Pair(StatusSolucionado, androidx.compose.ui.graphics.Color.White)
        "Trámite finalizado y Entregado" -> Pair(StatusSolucionado, androidx.compose.ui.graphics.Color.White)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    Surface(color = backgroundColor, shape = MaterialTheme.shapes.small) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SmallButton(text: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AddServiceToClientDialog(services: List<Service>, onDismiss: () -> Unit, onAdd: (Service) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Servicio") },
        text = {
            if (services.isEmpty()) {
                Text("No hay servicios disponibles")
            } else {
                Column {
                    services.forEach { service ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(service.name, fontWeight = FontWeight.Medium)
                                    Text("$${String.format("%.2f", service.price)}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { onAdd(service) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = BlueElectric)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.4f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.6f))
    }
}