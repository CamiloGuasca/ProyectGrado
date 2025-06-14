package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaInicioModo(onSeleccionarPadre: () -> Unit, onSeleccionarEstudiante: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¿Quién está usando la app?", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onSeleccionarPadre,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soy padre o docente")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSeleccionarEstudiante,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soy estudiante")
        }
    }
}
