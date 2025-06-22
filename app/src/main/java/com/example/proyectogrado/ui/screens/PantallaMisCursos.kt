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
    onVisualizarCurso: (String) -> Unit
) {
    val cursos by cursoViewModel.cursos.collectAsState() // Observa la lista de cursos
    var searchTerm by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Cargar cursos al entrar en la pantalla o cuando el término de búsqueda cambia
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cursos) { curso ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onVisualizarCurso(curso.nombreCurso) } // Puedes pasar el ID del curso
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Nombre: ${curso.nombreCurso}", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Estudiantes: ${curso.estudiantes.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onVisualizarCurso(curso.nombreCurso) }, // Reemplaza con el ID real del curso si lo obtienes
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Visualizar Curso")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewPantallaMisCursos() {
    MaterialTheme {
        val mockCursos = listOf(
            Curso(NombreCurso = "Matemáticas I", Profesor = "prof123", Estudiantes = listOf("Ana", "Pedro")),
            Curso(NombreCurso = "Física Básica", Profesor = "prof123", Estudiantes = listOf("María", "Juan")),
            Curso(NombreCurso = "Química Avanzada", Profesor = "prof456", Estudiantes = listOf("Luis"))
        )
        // Puedes crear un ViewModel de prueba o pasar una lista mock directamente si el preview no usa el ViewModel
        // Para este preview simple, simulamos la lista de cursos.
        // En una app real, el ViewModel se inyectaría.
        PantallaMisCursos(
            cursoViewModel = CursoViewModel(), // ViewModel de ejemplo para preview, en real se inyecta
            onBack = {},
            onVisualizarCurso = { cursoId -> println("Visualizar $cursoId") }
        )
    }
}*/