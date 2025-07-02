package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.Estudiante
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaEstudiantesParaHorario(
    viewModel: VinculacionViewModel = viewModel(),
    onGestionarHorarios: (String, String?) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val padreUid = FirebaseAuth.getInstance().currentUser?.uid
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF263238), // Azul grisáceo oscuro
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECEFF1)) // Gris claro
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👥 Gestionar Horarios de Uso",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF263238), // Texto oscuro
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (estudiantes.isEmpty()) {
                Text(
                    "⚠️ No tienes estudiantes vinculados.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
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
                                .clickable {
                                    onGestionarHorarios(estudiante.id, estudiante.nombre)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0E0E0) // Gris medio claro
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Estudiante",
                                    tint = Color(0xFF37474F), // Azul oscuro
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Nombre: ${estudiante.nombre ?: "Desconocido"}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF263238)
                                    )
                                    Text(
                                        text = "ID: ${estudiante.id}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.DarkGray
                                    )
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
