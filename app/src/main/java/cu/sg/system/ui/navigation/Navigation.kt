package cu.sg.system.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Clients : Screen("clients", "Usuarios", Icons.Default.Person)
    object Services : Screen("services", "Servicios", Icons.Default.MiscellaneousServices)
    object Profile : Screen("profile", "Perfíl", Icons.Default.Person)
    
    object AddClient : Screen("add_client", "Agregar Cliente", Icons.Default.Person)
    object ClientDetail : Screen("client_detail/{uid}", "Detalle Cliente", Icons.Default.Person) {
        fun createRoute(uid: String) = "client_detail/$uid"
    }
    object AddService : Screen("add_service", "Agregar Servicio", Icons.Default.MiscellaneousServices)
    object Search : Screen("search", "Buscar", Icons.Default.Person)
    object Settings : Screen("settings", "Ajustes", Icons.Default.Settings)
    object BackupRestore : Screen("backup_restore", "Backup y Restauración", Icons.Default.Backup)
    object ImportExport : Screen("import_export", "Importar/Exportar", Icons.Default.SwapHoriz)
}