package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.ui.viewmodel.PadreMenuViewModel

@Composable
fun PantallaPadreMenu(
    onVincular: () -> Unit,
    onMonitorear: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PadreMenuViewModel = viewModel()
) {
    val context = LocalContext.current
    val rolValido by viewModel.rolValido.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.validarRol(context, onLogout)
    }

    if (rolValido) {
        Column(Modifier.padding(16.dp)) {
            Text("Bienvenido padre", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVincular) {
                Text("Vincular estudiante")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onMonitorear) {
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
