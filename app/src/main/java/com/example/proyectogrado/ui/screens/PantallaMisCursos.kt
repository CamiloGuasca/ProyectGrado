package com.example.proyectogrado.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import com.example.proyectogrado.domain.model.Curso
import com.example.proyectogrado.viewmodel.CursoViewModel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

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
                title = { Text("Mis Cursos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
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
        ) {
            // Buscador
            OutlinedTextField(
                value = searchTerm,
                onValueChange = { newValue ->
                    searchTerm = newValue
                    // No necesitas llamar cargarCursosDelProfesor aquí directamente,
                    // el LaunchedEffect de arriba lo hará cuando searchTerm cambie.
                },
                label = { Text("Buscar cursos") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (cursos.isEmpty()) {
                Text(text = "No tienes cursos registrados o no se encontraron cursos con ese nombre.",
                    modifier = Modifier.padding(top = 16.dp))
                // Spacer para empujar el botón si no hay cursos
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f), // <--- Añadir weight para que el botón vaya al final
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cursos) { curso ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { DetalleCurso(curso.nombreCurso) } // Aquí se usa NombreCurso
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Nombre: ${curso.nombreCurso}", style = MaterialTheme.typography.titleLarge) // Aquí se usa NombreCurso
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Estudiantes: ${curso.estudiantes.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium) // Aquí se usa Estudiantes
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { DetalleCurso(curso.idCurso) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Visualizar Curso")
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Espacio antes del botón

            Button(
                onClick = CrearCurso,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar Nuevo Curso")
            }
        }
    }
}

