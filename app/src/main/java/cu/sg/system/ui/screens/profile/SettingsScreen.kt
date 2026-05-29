package cu.sg.system.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.ui.theme.BlueElectric
import cu.sg.system.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val currency by profileViewModel.currency.collectAsState()
    val isDarkMode by profileViewModel.isDarkMode.collectAsState()
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
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
                .padding(16.dp)
        ) {
            // Configuración de Moneda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCurrencyDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = BlueElectric
                        )
                        Column {
                            Text(
                                text = "Moneda",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (currency == "USD") "Dólar Estadounidense" else "Peso Cubano",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = currency,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueElectric
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Configuración de Tema
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = BlueElectric
                        )
                        Column {
                            Text(
                                text = "Tema",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isDarkMode) "Modo Oscuro" else "Modo Claro",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { profileViewModel.saveDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BlueElectric,
                            checkedTrackColor = BlueElectric.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información de la App
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showInfoDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BlueElectric
                        )
                        Text(
                            text = "Información de la App",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    // Diálogo de selección de moneda
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Text(
                    text = "Seleccionar Moneda",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = currency == "USD",
                                onClick = {
                                    profileViewModel.saveCurrency("USD")
                                    showCurrencyDialog = false
                                },
                                role = Role.RadioButton
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = currency == "USD",
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BlueElectric
                            )
                        )
                        Text(
                            text = "USD - Dólar Estadounidense",
                            fontSize = 16.sp
                        )
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = currency == "CUP",
                                onClick = {
                                    profileViewModel.saveCurrency("CUP")
                                    showCurrencyDialog = false
                                },
                                role = Role.RadioButton
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = currency == "CUP",
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BlueElectric
                            )
                        )
                        Text(
                            text = "CUP - Peso Cubano",
                            fontSize = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
    
    // Diálogo de información - CORREGIDO
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "SGC",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BlueElectric
                )
            },
            text = {
                Column {
                    Text(
                        text = "Sistema de Gestión de Clientes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow("Versión", "1.0.0")
                    InfoRow("Desarrollador", "Ashly Castell")
                    InfoRow("Paquete", "cu.sg.system")
                    InfoRow("Android Mínimo", "7.0 (API 24)")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "© 2026 SGC. Todos los derechos reservados.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}