package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.ui.navigation.Screen
import androidx.navigation.NavController

@Composable
fun PantallaInicioModo(navController: NavController,
                       onSeleccionarPadre: () -> Unit,
                       onSeleccionarEstudiante: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¿Cómo deseas ingresar?", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Login.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soy padre o profesor")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Screen.ConfigurarEstudiante.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soy estudiante")
        }
    }
}
