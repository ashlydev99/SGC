package cu.sg.system.ui.screens.clients

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.domain.model.Client
import cu.sg.system.domain.model.Service
import cu.sg.system.ui.theme.*
import cu.sg.system.ui.viewmodel.ClientViewModel
import cu.sg.system.ui.viewmodel.ServiceViewModel

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
    val isLoading by clientViewModel.isLoading.collectAsState()
    var showAddServiceDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uid) {
        clientViewModel.loadClientByUid(uid)
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
                    IconButton(
                        onClick = {
                            clientViewModel.deleteClient(uid)
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                    // UID - Tarjeta compacta
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = BlueElectric.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "UID:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = clientData.uid,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueElectric
                            )
                        }
                    }
                    
                    // Información Personal
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Información Personal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            
                            DetailRow("Nombre", clientData.firstName)
                            
                            if (!clientData.secondName.isNullOrBlank()) {
                                DetailRow("Segundo Nombre", clientData.secondName)
                            }
                            
                            DetailRow("Apellidos", clientData.lastName)
                            DetailRow("CI", clientData.ci)
                            
                            if (!clientData.address.isNullOrBlank()) {
                                DetailRow("Dirección", clientData.address)
                            }
                            
                            DetailRow("Contacto", clientData.contact)
                        }
                    }
                    
                    // Servicios
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Servicios",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                IconButton(
                                    onClick = { showAddServiceDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar servicio",
                                        tint = BlueElectric
                                    )
                                }
                            }
                            
                            if (clientData.services.isEmpty()) {
                                Text(
                                    text = "Cliente sin servicios activos",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
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
                            
                            // Total
                            if (clientData.services.isNotEmpty()) {
                                HorizontalDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", clientData.services.sumOf { it.price })}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = BlueElectric
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
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
}

@Composable
fun ServiceCardWithActions(
    service: Service,
    clientStatus: String,
    clientUid: String,
    clientViewModel: ClientViewModel
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Determinar el estado del servicio basado en el estado del cliente
    val serviceStatus = when (clientStatus) {
        "En trámite" -> "En trámite"
        "Pendiente a pagar" -> "Pendiente a pagar"
        "Trámite finalizado y Entregado" -> "Entregado"
        else -> clientStatus
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Tipo: ${service.type}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // ESTADO DEL SERVICIO
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Estado:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ServiceStatusBadge(status = serviceStatus)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%.2f", service.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botones de acción según el estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (clientStatus) {
                    "En trámite" -> {
                        // Botón Resuelto
                        SmallButton(
                            text = "Resuelto",
                            color = StatusPendiente,
                            onClick = {
                                clientViewModel.updateClientStatus(clientUid, "Pendiente a pagar")
                            }
                        )
                        // Botón Eliminar
                        SmallButton(
                            text = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            onClick = { showDeleteDialog = true }
                        )
                    }
                    "Pendiente a pagar" -> {
                        // Botón Pagado
                        SmallButton(
                            text = "Pagado",
                            color = StatusSolucionado,
                            onClick = {
                                clientViewModel.updateClientStatus(clientUid, "Trámite finalizado y Entregado")
                            }
                        )
                        // Botón Eliminar
                        SmallButton(
                            text = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            onClick = { showDeleteDialog = true }
                        )
                    }
                    "Trámite finalizado y Entregado" -> {
                        // Solo mostrar Eliminar
                        SmallButton(
                            text = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            onClick = { showDeleteDialog = true }
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar servicio
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Servicio") },
            text = { Text("¿Estás seguro de eliminar este servicio del cliente?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        clientViewModel.removeServiceFromClient(clientUid, service.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
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

@Composable
fun ServiceStatusBadge(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "En trámite" -> Pair(StatusTramite, androidx.compose.ui.graphics.Color.White)
        "Pendiente a pagar" -> Pair(StatusPendiente, androidx.compose.ui.graphics.Color.White)
        "Entregado" -> Pair(StatusSolucionado, androidx.compose.ui.graphics.Color.White)
        "Trámite finalizado y Entregado" -> Pair(StatusSolucionado, androidx.compose.ui.graphics.Color.White)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SmallButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddServiceToClientDialog(
    services: List<Service>,
    onDismiss: () -> Unit,
    onAdd: (Service) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Servicio") },
        text = {
            if (services.isEmpty()) {
                Text("No hay servicios disponibles para agregar")
            } else {
                Column {
                    services.forEach { service ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", service.price)}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { onAdd(service) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar",
                                        tint = BlueElectric
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}