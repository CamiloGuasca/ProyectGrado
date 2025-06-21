package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectogrado.ui.navigation.Screen
import com.example.proyectogrado.ui.viewmodel.ConfigurarEstudianteViewModel

@Composable
fun ConfigurarEstudianteScreen(
    navController: NavController,
    onConfigurado: () -> Unit,
    viewModel: ConfigurarEstudianteViewModel = viewModel()
) {
    var estudianteId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val mensaje by viewModel.mensaje.collectAsState()
    val exito by viewModel.exito.collectAsState()

    LaunchedEffect(exito) {
        if (exito) {
            navController.navigate(Screen.Inicio.route) {
                popUpTo(Screen.Inicio.route) { inclusive = true }
            }
            onConfigurado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Configura tu ID de estudiante", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estudianteId,
            onValueChange = { estudianteId = it },
            label = { Text("ID del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.guardarEstudiante(context, estudianteId)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(mensaje)
        }
    }
}
