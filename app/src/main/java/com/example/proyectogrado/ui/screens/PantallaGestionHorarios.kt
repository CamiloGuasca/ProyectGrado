package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    LaunchedEffect(idEstudiante) {
        viewModel.cargarHorarios(idEstudiante)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horarios de ${nombreEstudiante ?: "Estudiante"}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onAddHorario(idEstudiante) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Agregar Horario")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color(0xFFF0F4F8)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (horarios.isEmpty()) {
                Text(
                    text = "No hay horarios configurados para este estudiante.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 32.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(horarios, key = { it.id }) { horario ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
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
                                        text = "📆 ${horario.nombreHorario}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("🕒 Horario: ${horario.horaInicio} - ${horario.horaFin}")
                                    Text("⏱️ Máx. uso: ${horario.tiempoMaximoMinutos} min ${if (horario.tiempoMaximoMinutos == 0) "(Bloqueo Total)" else ""}")
                                    Text(
                                        text = "📱 Apps: ${if (horario.aplicacionesRestringidas.isEmpty()) "Ninguna" else horario.aplicacionesRestringidas.joinToString { it.substringAfterLast('.') }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    IconButton(onClick = { onEditHorario(idEstudiante, horario.id) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar Horario")
                                    }
                                    IconButton(onClick = {
                                        viewModel.eliminarHorario(idEstudiante, horario.id) { exito ->
                                            if (exito) {
                                                Toast.makeText(context, "✅ Horario eliminado", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "❌ Error al eliminar horario", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar Horario", tint = MaterialTheme.colorScheme.error)
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
