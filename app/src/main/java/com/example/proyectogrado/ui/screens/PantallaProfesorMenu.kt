package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Importa todos los componentes de Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectogrado.ui.navigation.Screen
import com.example.proyectogrado.viewmodel.ProfesorMenuViewModel

@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun PantallaProfesorMenu(
    navController: NavHostController,
    CrearCurso: () -> Unit,
    MisCursos: () -> Unit, // Este callback ya estaba, lo renombramos por claridad en la UI
    onLogout: () -> Unit,
    viewModel: ProfesorMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val rolValido by viewModel.rolValido.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.validarRol(context, onLogout)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menú del Profesor") } // Título de la barra superior
            )
        }
    ) { paddingValues ->
        if (rolValido) {
            Column(
                modifier = Modifier
                    .fillMaxSize() // Ocupa todo el espacio disponible
                    .padding(paddingValues) // Aplica el padding del Scaffold
                    .padding(16.dp), // Padding adicional para el contenido
                verticalArrangement = Arrangement.Center, // Centra los elementos verticalmente
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally // Centra los elementos horizontalmente
            ) {
                Text(
                    text = "Bienvenido Profesor",
                    style = MaterialTheme.typography.headlineLarge, // Un título más grande y prominente
                    modifier = Modifier.padding(bottom = 24.dp) // Más espacio debajo del título
                )
                /*
                Button(
                    onClick = CrearCurso, // Este botón debería ser para "Crear Nuevo Curso" según tu uso anterior
                    modifier = Modifier.fillMaxWidth(0.8f) // Ocupa el 80% del ancho
                ) {
                    Text("Crear Nuevo Curso")
                }*/
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre botones

                Button(
                    onClick = MisCursos, // Este botón es para "Mis Cursos"
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Ver Mis Cursos")
                }
                Button(
                    onClick = { navController.navigate(Screen.PerfilUsuario.route) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("Mi Perfil")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aquí podrías añadir un botón para "Monitorear uso de apps" si lo necesitas,
                // usando el callback que tenías antes.
                // Button(onClick = { /* onMonitorear() */ }) {
                //    Text("Monitorear Uso de Apps")
                // }
                // Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Botón de cerrar sesión en rojo
                ) {
                    Text("Cerrar Sesión")
                }
            }
        } else if (mensaje.isNotEmpty()) {
            // Muestra el mensaje de error o cargando
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(mensaje, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}