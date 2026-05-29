package cu.sg.system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cu.sg.system.data.repository.ClientRepository
import cu.sg.system.data.repository.ServiceRepository
import cu.sg.system.ui.navigation.SGCNavigation
import cu.sg.system.ui.theme.SGCTheme
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val application = application as SGCApplication
        val clientRepository = ClientRepository(
            application.database.clientDao()
        )
        val serviceRepository = ServiceRepository(
            application.database.serviceDao()
        )
        val userPreferences = application.userPreferences
        
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            LaunchedEffect(Unit) {
                userPreferences.isDarkMode.collect { dark ->
                    isDarkMode = dark
                }
            }
            
            SGCTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SGCNavigation(
                        clientRepository = clientRepository,
                        serviceRepository = serviceRepository,
                        userPreferences = userPreferences
                    )
                }
            }
        }
    }
}