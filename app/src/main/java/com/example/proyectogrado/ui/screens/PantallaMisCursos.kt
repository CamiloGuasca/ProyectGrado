package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.viewmodel.CursoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisCursos(
    cursoViewModel: CursoViewModel,
    onBack: () -> Unit,
    DetalleCurso: (String) -> Unit,
    CrearCurso: () -> Unit
) {
    val cursos by cursoViewModel.cursos.collectAsState()
    var searchTerm by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(searchTerm) {
        cursoViewModel.cargarCursosDelProfesor(searchTerm)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 Mis Cursos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF263238), // Azul oscuro grisáceo
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFECEFF1)) // Gris claro de fondo
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchTerm,
                onValueChange = { searchTerm = it },
                label = { Text("Buscar cursos") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Gray,
                    focusedBorderColor = Color(0xFF0288D1),
                    cursorColor = Color(0xFF0288D1)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (cursos.isEmpty()) {
                Text(
                    text = "⚠️ No tienes cursos registrados o no se encontraron cursos con ese nombre.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cursos) { curso ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "📘 ${curso.nombreCurso}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF263238)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "👥 Estudiantes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { DetalleCurso(curso.idCurso) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                                ) {
                                    Text("Visualizar Curso", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = CrearCurso,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
            ) {
                Text("➕ Agregar Nuevo Curso", color = Color.White)
            }
        }
    }
}
