package cu.sg.system.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cu.sg.system.data.local.UserPreferences
import cu.sg.system.data.repository.ClientRepository
import cu.sg.system.data.repository.ServiceRepository
import cu.sg.system.ui.screens.clients.AddClientScreen
import cu.sg.system.ui.screens.clients.ClientDetailScreen
import cu.sg.system.ui.screens.clients.ClientsScreen
import cu.sg.system.ui.screens.home.HomeScreen
import cu.sg.system.ui.screens.profile.ProfileScreen
import cu.sg.system.ui.screens.profile.SettingsScreen
import cu.sg.system.ui.screens.search.SearchScreen
import cu.sg.system.ui.screens.services.AddServiceScreen
import cu.sg.system.ui.screens.services.ServicesScreen
import cu.sg.system.ui.viewmodel.ClientViewModel
import cu.sg.system.ui.viewmodel.ProfileViewModel
import cu.sg.system.ui.viewmodel.ServiceViewModel

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SGCNavigation(
    clientRepository: ClientRepository,
    serviceRepository: ServiceRepository,
    userPreferences: UserPreferences
) {
    val navController = rememberNavController()
    
    val clientViewModel: ClientViewModel = viewModel(
        factory = ClientViewModel.Factory(clientRepository)
    )
    val serviceViewModel: ServiceViewModel = viewModel(
        factory = ServiceViewModel.Factory(serviceRepository)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(userPreferences)
    )
    
    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, Icons.Default.Home, "Inicio"),
        BottomNavItem(Screen.Clients, Icons.Default.Person, "Usuarios"),
        BottomNavItem(Screen.Services, Icons.Default.Star, "Servicios"),
        BottomNavItem(Screen.Profile, Icons.Default.Settings, "Perfíl")
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { 
                                it.route == item.screen.route 
                            } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
            
            composable(Screen.Clients.route) {
                ClientsScreen(
                    navController = navController,
                    clientViewModel = clientViewModel
                )
            }
            
            composable(Screen.Services.route) {
                ServicesScreen(
                    navController = navController,
                    serviceViewModel = serviceViewModel
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
            
            composable(Screen.AddClient.route) {
                AddClientScreen(
                    navController = navController,
                    clientViewModel = clientViewModel,
                    serviceViewModel = serviceViewModel
                )
            }
            
            composable(
                Screen.ClientDetail.route,
                arguments = listOf(
                    navArgument("uid") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: ""
                ClientDetailScreen(
                    navController = navController,
                    clientViewModel = clientViewModel,
                    uid = uid
                )
            }
            
            composable(Screen.AddService.route) {
                AddServiceScreen(
                    navController = navController,
                    serviceViewModel = serviceViewModel
                )
            }
            
            composable(Screen.Search.route) {
                SearchScreen(
                    navController = navController,
                    clientViewModel = clientViewModel
                )
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}