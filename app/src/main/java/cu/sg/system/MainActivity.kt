package cu.sg.system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cu.sg.system.data.local.UserPreferences
import cu.sg.system.ui.navigation.SGCNavigation
import cu.sg.system.ui.theme.SGCTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Controlar el tiempo del splash
        splashScreen.setKeepOnScreenCondition {
            // Mantener el splash hasta que pasen 2 segundos
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 2000) {
                // Esperar
            }
            false
        }
        
        val application = application as SGCApplication
        val clientRepository = cu.sg.system.data.repository.ClientRepository(
            application.database.clientDao()
        )
        val serviceRepository = cu.sg.system.data.repository.ServiceRepository(
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