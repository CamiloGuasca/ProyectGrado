package com.example.proyectogrado.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.ui.viewmodel.PadreMenuViewModel
import com.example.proyectogrado.viewmodel.ProfesorMenuViewModel

@Composable
fun PantallaProfesorMenu(
    //onVincular: () -> Unit,
    //onMonitorear: () -> Unit,
    CrearCurso: () -> Unit,
    MisCursos: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfesorMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val rolValido by viewModel.rolValido.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.validarRol(context, onLogout)
    }

    if (rolValido) {
        Column(Modifier.padding(16.dp)) {
            Text("Bienvenido Profesor", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = CrearCurso) {
                Text("Mis Cursos")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = MisCursos){
                Text("Monitorear uso de apps")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout) {
                Text("Cerrar sesión")
            }
        }
    } else if (mensaje.isNotEmpty()) {
        Text(mensaje, color = MaterialTheme.colorScheme.error)
    }
}