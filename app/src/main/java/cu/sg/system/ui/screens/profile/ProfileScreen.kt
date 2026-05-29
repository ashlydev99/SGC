package cu.sg.system.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cu.sg.system.R
import cu.sg.system.ui.navigation.Screen
import cu.sg.system.ui.theme.BlueElectric
import cu.sg.system.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val userName by profileViewModel.userName.collectAsState()
    val isEditing = remember { mutableStateOf(false) }
    val editedName = remember { mutableStateOf(userName) }
    val context = LocalContext.current
    
    LaunchedEffect(userName) {
        editedName.value = userName
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfíl",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes"
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Foto de perfil circular - MÁS PEQUEÑA
            Image(
                painter = painterResource(id = R.drawable.ic_default_profile),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Nombre de usuario editable - MÁS COMPACTO
            if (isEditing.value) {
                OutlinedTextField(
                    value = editedName.value,
                    onValueChange = { editedName.value = it },
                    label = { Text("Nombre de usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueElectric,
                        cursorColor = BlueElectric
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            profileViewModel.updateUserName(editedName.value)
                            profileViewModel.saveUserName()
                            isEditing.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueElectric
                        )
                    ) {
                        Text("Guardar")
                    }
                    OutlinedButton(
                        onClick = {
                            editedName.value = userName
                            isEditing.value = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Nombre de usuario",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { isEditing.value = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar nombre",
                                tint = BlueElectric
                            )
                        }
                    }
                }
            }
            
            // Espacio flexible para empujar el botón hacia abajo
            Spacer(modifier = Modifier.weight(1f))
            
            // Botón de salir
            Button(
                onClick = {
                    (context as Activity).finishAffinity()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "SALIR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}