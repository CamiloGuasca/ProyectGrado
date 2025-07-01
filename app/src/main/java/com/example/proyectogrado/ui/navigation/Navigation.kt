// app/src/main/java/com/example/proyectogrado/ui/navigation/Navigation.kt
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
import com.example.proyectogrado.data.repository.CursoRepository
import com.example.proyectogrado.ui.screens.*
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.example.proyectogrado.viewmodel.ProfesorMenuViewModel
import com.example.proyectogrado.viewmodel.RegistroViewModel
import com.example.proyectogrado.viewmodel.UsuarioViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel // Importa VinculacionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.example.proyectogrado.viewmodel.PerfilUsuarioViewModel

// Definición de todas las rutas de navegación.
// ¡Esta clase contiene todas tus rutas! No hay archivo Screen.kt separado.
sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Login : Screen("login")
    object Register : Screen("register")
    object PadreMenu : Screen("padreMenu")
    object Profe : Screen("profe")
    object Vincular : Screen("vincular")
    object PerfilUsuario : Screen("perfil_usuario")
    object Uso : Screen("uso")
    object ConfigurarEstudiante : Screen("configurar_estudiante_screen")
    object DetalleEstudiante : Screen("detalleEstudiante/{estudianteId}") {
        fun createRoute(estudianteId: String) = "detalleEstudiante/$estudianteId"
    }
    object Bienvenida : Screen("pantalla_bienvenida")
    object HomeScreen : Screen("home_screen")


    // Rutas con argumentos
    object Enviar : Screen("enviar_uso_activo/{idEstudiante}") {
        fun createRoute(idEstudiante: String) = "enviar_uso_activo/$idEstudiante"
    }

    // Rutas para módulos de profesor
    object misCursos : Screen("misCursos")
    object CrearCurso : Screen("crearCurso")
    object DetalleCurso : Screen("detalleCurso/{cursoId}") {
        fun createRoute(idCurso: String) = "detalleCurso/$idCurso"
    }

    // Rutas para el módulo de horarios (¡CRÍTICO, DEBEN ESTAR AQUÍ!)
    object ListaEstudiantesParaHorario : Screen("lista_estudiantes_para_horario")
    object GestionHorariosEstudiante : Screen("gestion_horarios_estudiante/{idEstudiante}") {
        fun createRoute(idEstudiante: String) = "gestion_horarios_estudiante/$idEstudiante"
    }
    object CrearEditarHorario : Screen("crear_editar_horario/{idEstudiante}?horarioId={horarioId}") {
        fun createRoute(idEstudiante: String, horarioId: String? = null) =
            "crear_editar_horario/$idEstudiante" + (horarioId?.let { "?horarioId=$it" } ?: "")
    }

    object PantallaInicioModo : Screen("pantalla_inicio_modo")
    object PantallaEnviarUso : Screen("pantalla_enviar_uso/{idEstudiante}") {
        fun createRoute(idEstudiante: String) = "pantalla_enviar_uso/$idEstudiante"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    vinculacionViewModel: VinculacionViewModel
) {
    val context = LocalContext.current
    val studentIdFromPrefs = PreferenciasEstudiante.obtenerId(context)
    val startDestination = if (studentIdFromPrefs.isNotBlank()) {
        Log.d("AppNavigation", "Estudiante ID '${studentIdFromPrefs}' encontrado en preferencias. Navegando a HomeScreen.")
        Screen.HomeScreen.route
    } else {
        Log.d("AppNavigation", "No se encontró ID de estudiante en preferencias. Navegando a PantallaInicioModo.")
        Screen.Inicio.route
    }

    val estudianteRepository = remember { EstudianteRepository() }
    val cursoRepository = remember { CursoRepository() }

    val vinculacionViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VinculacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VinculacionViewModel(estudianteRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for VinculacionViewModel")
        }
    }}

    val cursoViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CursoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CursoViewModel(cursoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for CursoViewModel")
        }
    }}

    val profesorMenuViewModelFactory = remember { object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfesorMenuViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfesorMenuViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for ProfesorMenuViewModel")
        }
    }}

    NavHost(navController = navController, startDestination = startDestination) {
        // --- Comienzo de tus rutas existentes (sin cambios aquí) ---
        composable(Screen.Inicio.route) {
            PantallaInicioModo(
                navController = navController,
                onSeleccionarPadre = { navController.navigate(Screen.Login.route) },
                onSeleccionarEstudiante = { navController.navigate(Screen.ConfigurarEstudiante.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { rol ->
                    Log.d("ROLUSU", "Rol seleccionado: $rol")
                    when (rol.lowercase()) {
                        "padre" -> navController.navigate(Screen.PadreMenu.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        "profesor" -> navController.navigate(Screen.Profe.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        "estudiante" -> navController.navigate(Screen.ConfigurarEstudiante.route) { popUpTo(Screen.Login.route) { inclusive = true } }
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
                navController = navController,
                onVincular = { navController.navigate(Screen.Vincular.route) },
                onMonitorear = { navController.navigate(Screen.Uso.route) },
                onConfigurarHorarioEstudiante = { navController.navigate(Screen.ListaEstudiantesParaHorario.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.PadreMenu.route) { inclusive = true }
                    }
                }
            )
        }


        composable(Screen.Profe.route) {
            val profesorMenuViewModel: ProfesorMenuViewModel = viewModel(factory = profesorMenuViewModelFactory)
            PantallaProfesorMenu(
                navController = navController, // ✅ NUEVO
                CrearCurso = { navController.navigate(Screen.CrearCurso.route) },
                MisCursos = { navController.navigate(Screen.misCursos.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Inicio.route) { inclusive = true }
                    }
                },
                viewModel = profesorMenuViewModel
            )
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
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.Uso.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ConfigurarEstudiante.route) {
            ConfigurarEstudianteScreen(navController = navController)
        }

        composable(
            route = Screen.PantallaEnviarUso.route,
            arguments = listOf(navArgument("idEstudiante") { type = NavType.StringType })
        ) { backStackEntry ->
            val idEstudiante = backStackEntry.arguments?.getString("idEstudiante")
            if (idEstudiante != null) {
                PantallaEnviarUso(
                    navController = navController,
                    estudianteId = idEstudiante,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(Screen.PantallaEnviarUso.route) { inclusive = true }
                        }
                    }
                )
            } else {
                Text("Error: ID de estudiante no proporcionado para enviar uso.")
            }
        }

        composable(Screen.misCursos.route){
            val cursoViewModel: CursoViewModel = viewModel(factory = cursoViewModelFactory)
            PantallaMisCursos(
                cursoViewModel = cursoViewModel,
                onBack = { navController.popBackStack() },
                DetalleCurso = { cursoId ->
                    navController.navigate(Screen.DetalleCurso.createRoute(cursoId))
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
                    navController.popBackStack()
                    Toast.makeText(context, "Curso creado con éxito!", Toast.LENGTH_SHORT).show()
                },
                onBack = { navController.popBackStack() },
                vinculacionViewModel = vinculacionViewModel,
                cursoViewModel = cursoViewModel
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
                    DetalleEstudiante = { estudianteId ->
                        Log.d("Navigation", "Navegando a DetalleEstudiante con ID: $estudianteId")
                        navController.navigate(Screen.DetalleEstudiante.createRoute(estudianteId))
                    }
                )
            } else {
                Text("Error: ID del curso no proporcionado.")
            }
        }

        // --- ¡AÑADE ESTE BLOQUE EXACTAMENTE AQUÍ DENTRO DEL NavHost! ---
        composable(
            route = Screen.DetalleEstudiante.route,
            arguments = listOf(navArgument("estudianteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val estudianteId = backStackEntry.arguments?.getString("estudianteId")

            if (estudianteId != null) {
                // Asegúrate de usar la instancia de vinculacionViewModel que ya se está pasando a AppNavigation
                val vinculacionVM: VinculacionViewModel = viewModel(factory = vinculacionViewModelFactory)

                PantallaDetalleEstudiante(
                    estudianteId = estudianteId,
                    vinculacionViewModel = vinculacionVM, // Pasa la instancia del ViewModel
                    onBack = { navController.popBackStack() }
                )
            } else {
                Text("Error: ID del estudiante no proporcionado para la vista de detalle.")
            }
        }
        // ----------------------------------------------------------------------

        // --- Rutas de Horarios del Compañero (¡Con la PantallaListaEstudiantesParaHorario CORRECTA!) ---
        composable(Screen.ListaEstudiantesParaHorario.route) {
            val vinculacionVM: VinculacionViewModel = viewModel(factory = vinculacionViewModelFactory) // Obtén la instancia de VinculacionViewModel
            PantallaListaEstudiantesParaHorario(
                viewModel = vinculacionVM, // Pasa el VinculacionViewModel
                onGestionarHorarios = { estudianteId, nombreEstudiante ->
                    val route = Screen.GestionHorariosEstudiante.createRoute(estudianteId)
                    navController.navigate(route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.GestionHorariosEstudiante.route,
            arguments = listOf(navArgument("idEstudiante") { type = NavType.StringType })
        ) { backStackEntry ->
            val idEstudiante = backStackEntry.arguments?.getString("idEstudiante")
            val nombreEstudiante = "Estudiante ${idEstudiante?.take(8)}..." // Placeholder si no tienes el nombre real

            if (idEstudiante != null) {
                PantallaGestionHorarios(
                    idEstudiante = idEstudiante,
                    nombreEstudiante = nombreEstudiante,
                    onAddHorario = { estId -> navController.navigate(Screen.CrearEditarHorario.createRoute(estId)) },
                    onEditHorario = { estId, horarioId -> navController.navigate(Screen.CrearEditarHorario.createRoute(estId, horarioId)) },
                    onBack = { navController.popBackStack() }
                )
            } else {
                Text("Error: ID de estudiante no proporcionado.")
            }
        }

        composable(
            route = Screen.CrearEditarHorario.route,
            arguments = listOf(
                navArgument("idEstudiante") { type = NavType.StringType },
                navArgument("horarioId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val idEstudiante = backStackEntry.arguments?.getString("idEstudiante")
            val horarioId = backStackEntry.arguments?.getString("horarioId")

            if (idEstudiante != null) {
                PantallaConfigurarHorario(
                    idEstudiante = idEstudiante,
                    horarioId = horarioId,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text("Error: ID de estudiante no proporcionado para configurar horario.")
            }
        }

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Inicio.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PerfilUsuario.route) {
            PantallaPerfilUsuario(
                onBack = { navController.popBackStack() }
            )
        }



    } // Fin del NavHost
}