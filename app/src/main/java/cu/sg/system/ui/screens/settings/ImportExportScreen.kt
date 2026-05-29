package cu.sg.system.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.data.local.AppDatabase
import cu.sg.system.data.local.entity.ClientEntity
import cu.sg.system.data.local.entity.ServiceEntity
import cu.sg.system.ui.theme.BlueElectric
import cu.sg.system.util.CsvManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val csvManager = remember { CsvManager(context) }
    val scope = rememberCoroutineScope()
    
    val exportClientsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val clients: List<ClientEntity> = database.clientDao().getAllClientsForExport()
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            csvManager.exportClients(clients, outputStream)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Clientes exportados correctamente", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    val exportServicesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val services: List<ServiceEntity> = database.serviceDao().getAllServicesForExport()
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            csvManager.exportServices(services, outputStream)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Servicios exportados correctamente", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    val importClientsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val clients: List<ClientEntity> = csvManager.importClients(inputStream)
                            for (client in clients) {
                                database.clientDao().insertClient(client)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "${clients.size} clientes importados", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    val importServicesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val services: List<ServiceEntity> = csvManager.importServices(inputStream)
                            for (service in services) {
                                database.serviceDao().insertService(service)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "${services.size} servicios importados", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importar/Exportar", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Clientes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { exportClientsLauncher.launch("clientes_sgc.csv") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueElectric)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar CSV", fontSize = 14.sp)
                }
                
                Button(
                    onClick = { importClientsLauncher.launch("text/*") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importar CSV", fontSize = 14.sp)
                }
            }
            
            HorizontalDivider()
            
            Text("Servicios", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { exportServicesLauncher.launch("servicios_sgc.csv") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueElectric)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exportar CSV", fontSize = 14.sp)
                }
                
                Button(
                    onClick = { importServicesLauncher.launch("text/*") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importar CSV", fontSize = 14.sp)
                }
            }
        }
    }
}