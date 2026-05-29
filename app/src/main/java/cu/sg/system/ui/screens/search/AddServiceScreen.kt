package cu.sg.system.ui.screens.services

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.ui.theme.BlueElectric
import cu.sg.system.ui.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(
    navController: NavController,
    serviceViewModel: ServiceViewModel
) {
    val isLoading by serviceViewModel.isLoading.collectAsState()
    val operationSuccess by serviceViewModel.operationSuccess.collectAsState()
    val error by serviceViewModel.error.collectAsState()
    
    var serviceName by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var servicePrice by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Observar éxito de operación
    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            serviceViewModel.clearOperationSuccess()
            navController.navigateUp()
        }
    }
    
    // Observar errores
    LaunchedEffect(error) {
        error?.let {
            errorMessage = it
            showErrorDialog = true
            serviceViewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agregar Servicio",
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
            // Título de la sección
            Text(
                text = "Información del Servicio",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo Nombre del Servicio
            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text("Nombre del Servicio *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                )
            )
            
            // Campo Tipo de Servicio
            OutlinedTextField(
                value = serviceType,
                onValueChange = { serviceType = it },
                label = { Text("Tipo de Servicio *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                ),
                placeholder = { Text("Ej: Internet, Telefonía, Cable, etc.") }
            )
            
            // Campo Precio
            OutlinedTextField(
                value = servicePrice,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        servicePrice = newValue
                    }
                },
                label = { Text("Precio *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueElectric,
                    cursorColor = BlueElectric,
                    focusedLabelColor = BlueElectric
                ),
                leadingIcon = {
                    Text(
                        text = "$",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Vista previa del servicio
            if (serviceName.isNotBlank() || serviceType.isNotBlank() || servicePrice.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Vista Previa",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (serviceName.isNotBlank()) {
                            Text(
                                text = "Nombre: $serviceName",
                                fontSize = 14.sp
                            )
                        }
                        
                        if (serviceType.isNotBlank()) {
                            Text(
                                text = "Tipo: $serviceType",
                                fontSize = 14.sp
                            )
                        }
                        
                        if (servicePrice.isNotBlank()) {
                            Text(
                                text = "Precio: $${String.format("%.2f", servicePrice.toDoubleOrNull() ?: 0.0)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de Guardar
            Button(
                onClick = {
                    when {
                        serviceName.isBlank() -> {
                            errorMessage = "El nombre del servicio es obligatorio"
                            showErrorDialog = true
                        }
                        serviceType.isBlank() -> {
                            errorMessage = "El tipo de servicio es obligatorio"
                            showErrorDialog = true
                        }
                        servicePrice.isBlank() -> {
                            errorMessage = "El precio es obligatorio"
                            showErrorDialog = true
                        }
                        servicePrice.toDoubleOrNull() == null || servicePrice.toDouble() <= 0 -> {
                            errorMessage = "Ingrese un precio válido mayor a 0"
                            showErrorDialog = true
                        }
                        else -> {
                            serviceViewModel.createService(
                                name = serviceName,
                                type = serviceType,
                                price = servicePrice.toDouble()
                            )
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
                        text = "Crear Servicio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { 
                Text(
                    text = "Error",
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}