// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaGestionHorarios.kt
package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ArrowBack // <--- ¡Asegúrate de que esta línea esté presente!
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.HorarioUso
import com.example.proyectogrado.viewmodel.HorarioUsoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionHorarios(
    idEstudiante: String,
    nombreEstudiante: String?,
    viewModel: HorarioUsoViewModel = viewModel(),
    onAddHorario: (String) -> Unit,
    onEditHorario: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val horarios by viewModel.horarios.collectAsState()

    // Este es el cambio clave: llamar a cargarHorarios en lugar de iniciarEscuchaHorarios
    LaunchedEffect(idEstudiante) {
        viewModel.cargarHorarios(idEstudiante) // <-- CORRECCIÓN AQUÍ
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horarios de ${nombreEstudiante ?: "Estudiante"}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onAddHorario(idEstudiante) }) {
                        Icon(Icons.Filled.Add, "Agregar Horario")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (horarios.isEmpty()) {
                Text(
                    text = "No hay horarios configurados para este estudiante.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(horarios, key = { it.id }) { horario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = horario.nombreHorario,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Horario: ${horario.horaInicio} - ${horario.horaFin}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Max. Uso: ${horario.tiempoMaximoMinutos} min (${if (horario.tiempoMaximoMinutos == 0) "Bloqueo Total" else ""})",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (horario.aplicacionesRestringidas.isNotEmpty()) {
                                        Text(
                                            text = "Apps: ${horario.aplicacionesRestringidas.joinToString { it.substringAfterLast('.') }}", // Muestra solo el nombre de la app
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            text = "Apps: Ninguna",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Column {
                                    IconButton(onClick = { onEditHorario(idEstudiante, horario.id) }) {
                                        Icon(Icons.Filled.Edit, "Editar Horario")
                                    }
                                    IconButton(onClick = {
                                        viewModel.eliminarHorario(idEstudiante, horario.id) { exito ->
                                            if (exito) {
                                                Toast.makeText(context, "Horario eliminado", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Error al eliminar horario", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.Delete, "Eliminar Horario", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}