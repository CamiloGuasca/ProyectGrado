package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.domain.model.Estudiante

@Composable
fun SeleccionarEstudianteUsoScreen(
    onEstudianteSeleccionado: (String) -> Unit,
    onBack: () -> Unit
) {
    var estudiantes by remember { mutableStateOf<List<Estudiante>>(emptyList()) }
    val repo = remember { EstudianteRepository() }

    // UID del padre (actual) desde Firebase Auth
    val uidPadre = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uidPadre) {
        uidPadre?.let {
            repo.obtenerEstudiantesPorPadre(it) { lista ->
                estudiantes = lista
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Selecciona un estudiante", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (estudiantes.isEmpty()) {
            Text("No hay estudiantes vinculados.")
        } else {
            estudiantes.forEach { estudiante ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onEstudianteSeleccionado(estudiante.id) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nombre: ${estudiante.nombre}")
                        Text("ID: ${estudiante.id.take(8)}...")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
