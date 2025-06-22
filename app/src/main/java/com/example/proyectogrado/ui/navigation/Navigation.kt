package com.example.proyectogrado.ui.navigation


import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.data.repository.CursoRepository // Asegúrate de importar el CursoRepository
import com.example.proyectogrado.ui.screens.*
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.example.proyectogrado.viewmodel.ProfesorMenuViewModel
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
    object misCursos : Screen("misCursos")
    object CrearCurso : Screen("crearCurso")
    object DetalleCurso : Screen("detalleCurso/{cursoId}") { // Consistentemente "cursoId"
        fun createRoute(idCurso: String) = "detalleCurso/$idCurso" // Usa "idCurso" como parámetro para la función
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel, // Asumo que estos ViewModels se manejan a un nivel superior
    vinculacionViewModel: VinculacionViewModel // Y se pasan aquí
) {
    val context = LocalContext.current
    val estudianteRepository = remember { EstudianteRepository() }
    val cursoRepository = remember { CursoRepository() } // <--- Asegúrate de que CursoRepository esté instanciado aquí

    // Factory para VinculacionViewModel
    val vinculacionViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VinculacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VinculacionViewModel(estudianteRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for VinculacionViewModel")
        }
    }}

    // Factory para CursoViewModel
    val cursoViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CursoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CursoViewModel(cursoRepository) as T // <--- Pasa el cursoRepository aquí
            }
            throw IllegalArgumentException("Unknown ViewModel class for CursoViewModel")
        }
    }}

    // Factory para ProfesorMenuViewModel (si lo necesitas y tiene dependencias)
    val profesorMenuViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfesorMenuViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfesorMenuViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for ProfesorMenuViewModel")
        }
    }}

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
                    Log.d("ROLUSU", "Rol seleccionado: $rol")
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
            val registroViewModel: RegistroViewModel = viewModel() // Asumo que no necesita factory
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
            val profesorMenuViewModel: ProfesorMenuViewModel = viewModel(factory = profesorMenuViewModelFactory) // Usar factory
            PantallaProfesorMenu(
                CrearCurso = { navController.navigate(Screen.CrearCurso.route) },
                MisCursos = { navController.navigate(Screen.misCursos.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                },
                viewModel = profesorMenuViewModel // Pasa el viewModel
            )
        }

        composable(Screen.Vincular.route) {
            VincularEstudianteScreen(
                onBack = { navController.popBackStack() },
                viewModel = vinculacionViewModel
            )
        }

        composable(Screen.Uso.route) {
            // Asumo que PantallaMonitoreoUso y PantallaEnviarUso no necesitan ViewModels con Factory
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

        composable(Screen.misCursos.route){
            // *** CORRECCIÓN CRÍTICA AQUÍ: USAR LA FACTORY PARA CURSOVIEWMODEL ***
            val cursoViewModel: CursoViewModel = viewModel(factory = cursoViewModelFactory)
            PantallaMisCursos(
                cursoViewModel = cursoViewModel, // Pasa la instancia con su repo
                onBack = { navController.popBackStack() },
                DetalleCurso = { cursoId ->
                    navController.navigate(Screen.DetalleCurso.createRoute(cursoId)) // Pasa el ID del curso
                },
                CrearCurso  = { navController.navigate(Screen.CrearCurso.route) }
            )
        }

        composable(Screen.CrearCurso.route){
            val cursoViewModel: CursoViewModel = viewModel(factory = cursoViewModelFactory)
            val vinculacionViewModel: VinculacionViewModel = viewModel(factory = vinculacionViewModelFactory)
            CursoFormulario(
                onCursoCreado = { curso ->
                    Log.d("AppNav", "Curso Creado en Formulario: ${curso.nombreCurso}, Estudiantes: ${curso.estudiantes}")
                    // La lógica de guardado directo en el formulario ahora se encarga de esto.
                    // Si el formulario no navega, la AppNavigation debería hacer el popBackStack
                    navController.popBackStack() // Asumo que quieres volver a MisCursos
                    Toast.makeText(context, "Curso creado con éxito!", Toast.LENGTH_SHORT).show()
                },
                onBack = { navController.popBackStack() },
                vinculacionViewModel = vinculacionViewModel,
                cursoViewModel = cursoViewModel // Pasa el cursoViewModel
            )
        }

        composable(
            route = Screen.DetalleCurso.route,
            arguments = listOf(navArgument("cursoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val idCurso = backStackEntry.arguments?.getString("cursoId")

            if (idCurso != null) {
                val cursoViewModel: CursoViewModel = viewModel(factory = cursoViewModelFactory)
                val vinculacionViewModel: VinculacionViewModel = viewModel(factory = vinculacionViewModelFactory)

                PantallaDetalleCurso(
                    idCurso = idCurso,
                    cursoViewModel = cursoViewModel,
                    vinculacionViewModel = vinculacionViewModel,
                    onBack = { navController.popBackStack() },
                    onConsultarEstudiante = { estudianteId ->
                        Toast.makeText(context, "Consultando estudiante: $estudianteId", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Text("Error: ID del curso no proporcionado.")
            }
        }
    }
}