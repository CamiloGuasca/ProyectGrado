// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaListaEstudiantesParaHorario.kt
package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.Estudiante // Asegúrate de que este modelo exista
import com.example.proyectogrado.viewmodel.VinculacionViewModel // Asegúrate de que este ViewModel exista y sea el correcto
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import kotlinx.coroutines.flow.StateFlow // Esta importación puede no ser necesaria si listaEstudiantes es StateFlow, pero no hace daño
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person // Para el icono del estudiante
import androidx.compose.ui.Alignment // Para Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaEstudiantesParaHorario(
    viewModel: VinculacionViewModel = viewModel(), // Usa el ViewModel proporcionado
    onGestionarHorarios: (String, String?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val padreUid = FirebaseAuth.getInstance().currentUser?.uid

    // Recopilar el estado de la lista de estudiantes del ViewModel
    // **** CORRECCIÓN CLAVE: Eliminar 'initial = emptyList()' si listaEstudiantes es StateFlow ****
    val estudiantes = viewModel.listaEstudiantes

    LaunchedEffect(padreUid) {
        if (padreUid != null) {
            viewModel.cargarEstudiantes(padreUid)
        } else {
            Toast.makeText(context, "No se pudo obtener el UID del padre.", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Estudiante") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Asegura la alineación
        ) {
            Text(
                text = "Gestionar Horarios de Uso",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (estudiantes.isEmpty()) {
                Text("No tienes estudiantes vinculados.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Volver al menú")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(estudiantes, key = { it.id }) { estudiante ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Pasa el ID y el nombre (que puede ser nulo) a la función de gestionar
                                    onGestionarHorarios(estudiante.id, estudiante.nombre)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row( // Usa un Row para el icono y el texto
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon( // Icono de persona para cada estudiante
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Icono de Estudiante",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = "Nombre: ${estudiante.nombre ?: "Desconocido"}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "ID: ${estudiante.id}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver al Menú Principal")
                }
            }
        }
    }
}
