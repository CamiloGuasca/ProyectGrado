package com.example.proyectogrado.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectogrado.ui.screens.*
import com.example.proyectogrado.viewmodel.RegistroViewModel
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Login : Screen("login")
    object Register : Screen("register")
    object PadreMenu : Screen("padreMenu")
    object Profe : Screen("profe")
    object Vincular : Screen("vincular")
    object Uso : Screen("uso")
    object ConfigurarEstudiante : Screen("configurar_estudiante")
    object ConfigEstudiante : Screen("configEstudiante")
    object Enviar : Screen("enviar")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    vinculacionViewModel: VinculacionViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Inicio.route) {

        composable(Screen.Inicio.route) {
            PantallaInicioModo(
                navController = navController,
                onSeleccionarPadre = { navController.navigate(Screen.Login.route) },
                onSeleccionarEstudiante = { navController.navigate(Screen.ConfigEstudiante.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { rol ->
                    when (rol.lowercase()) {
                        "padre" -> navController.navigate(Screen.PadreMenu.route)
                        "profesor" -> navController.navigate(Screen.Profe.route)
                        "estudiante" -> navController.navigate(Screen.ConfigEstudiante.route)
                    }
                },
                viewModel = usuarioViewModel
            )
        }

        composable(Screen.Register.route) {
            val registroViewModel: RegistroViewModel = viewModel()
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegistroExitoso = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                },
                viewModel = registroViewModel
            )
        }



        composable(Screen.PadreMenu.route) {
            PantallaPadreMenu(
                onVincular = { navController.navigate(Screen.Vincular.route) },
                onMonitorear = { navController.navigate(Screen.Uso.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Profe.route) {
            ListaUsuariosScreen()
        }

        composable(Screen.Vincular.route) {
            VincularEstudianteScreen(
                onBack = { navController.popBackStack() },
                viewModel = vinculacionViewModel
            )
        }

        composable(Screen.Uso.route) {
            PantallaMonitoreoUso(
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ConfigurarEstudiante.route) {
            ConfigurarEstudianteScreen(
                navController = navController,
                onConfigurado = {
                    navController.navigate(Screen.Enviar.route)
                }
            )
        }


        composable(Screen.Enviar.route) {
            PantallaEnviarUso()
        }

    }
}
