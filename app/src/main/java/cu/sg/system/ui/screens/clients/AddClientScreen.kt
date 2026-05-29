package cu.sg.system.ui.screens.clients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.domain.model.Service
import cu.sg.system.ui.theme.BlueElectric
import cu.sg.system.ui.viewmodel.ClientViewModel
import cu.sg.system.ui.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(
    navController: NavController,
    clientViewModel: ClientViewModel,
    serviceViewModel: ServiceViewModel
) {
    val services by serviceViewModel.services.collectAsState()
    val isLoading by clientViewModel.isLoading.collectAsState()
    val error by clientViewModel.error.collectAsState()
    
    var firstName by remember { mutableStateOf("") }
    var secondName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var ci by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var selectedServices by remember { mutableStateOf<List<Service>>(emptyList()) }
    var showServiceDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Observar errores
    LaunchedEffect(error) {
        error?.let {
            errorMessage = it
            showErrorDialog = true
            clientViewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Cliente",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo Segundo Nombre (Opcional)
            OutlinedTextField(
                value = secondName,
                onValueChange = { secondName = it },
                label = { Text("Segundo Nombre (Opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo Apellidos
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellidos *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo CI
            OutlinedTextField(
                value = ci,
                onValueChange = { 
                    if (it.length <= 11 && it.all { char -> char.isDigit() }) {
                        ci = it
                    }
                },
                label = { Text("CI (11 dígitos) *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo Dirección (Opcional)
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección (Opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo Contacto
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contacto *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Selector de Servicios
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Servicios Seleccionados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (selectedServices.isEmpty()) {
                        Text(
                            text = "No hay servicios seleccionados",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    } else {
                        selectedServices.forEach { service ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = service.name,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$${String.format("%.2f", service.price)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { showServiceDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueElectric
                        )
                    ) {
                        Text("Seleccionar Servicios")
                    }
                }
            }
            
            // Precio Total
            if (selectedServices.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Precio Total:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "$${String.format("%.2f", selectedServices.sumOf { it.price })}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de Guardar
            Button(
                onClick = {
                    // Validaciones
                    when {
                        firstName.isBlank() -> {
                            errorMessage = "El nombre es obligatorio"
                            showErrorDialog = true
                        }
                        lastName.isBlank() -> {
                            errorMessage = "Los apellidos son obligatorios"
                            showErrorDialog = true
                        }
                        ci.length != 11 -> {
                            errorMessage = "El CI debe tener 11 dígitos"
                            showErrorDialog = true
                        }
                        contact.isBlank() -> {
                            errorMessage = "El contacto es obligatorio"
                            showErrorDialog = true
                        }
                        selectedServices.isEmpty() -> {
                            errorMessage = "Debe seleccionar al menos un servicio"
                            showErrorDialog = true
                        }
                        else -> {
                            clientViewModel.createClient(
                                firstName = firstName,
                                secondName = secondName.ifBlank { null },
                                lastName = lastName,
                                ci = ci,
                                address = address.ifBlank { null },
                                contact = contact,
                                services = selectedServices
                            )
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueElectric
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Crear Cliente",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Diálogo de selección de servicios
    if (showServiceDialog) {
        ServiceSelectionDialog(
            services = services,
            selectedServices = selectedServices,
            onDismiss = { showServiceDialog = false },
            onConfirm = { selected ->
                selectedServices = selected
                showServiceDialog = false
            }
        )
    }
    
    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSelectionDialog(
    services: List<Service>,
    selectedServices: List<Service>,
    onDismiss: () -> Unit,
    onConfirm: (List<Service>) -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedServices.toSet()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Servicios",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (services.isEmpty()) {
                Text("No hay servicios disponibles")
            } else {
                Column {
                    services.forEach { service ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                            Checkbox(
                                checked = tempSelected.contains(service),
                                onCheckedChange = { checked ->
                                    tempSelected = if (checked) {
                                        tempSelected + service
                                    } else {
                                        tempSelected - service
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BlueElectric
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempSelected.toList()) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}