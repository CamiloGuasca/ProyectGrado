package com.example.proyectogrado.ui.screens

import android.util.Log // Importar para logs
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // Importar para el icono de añadir/eliminar
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close // Nuevo icono para eliminar (una 'X' o '+' rotada)
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect // Importar LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment // Importar para alinear
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.Curso
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoFormulario(
    onCursoCreado: (Curso) -> Unit, // Este callback podría no ser necesario si el ViewModel maneja la navegación.
    vinculacionViewModel: VinculacionViewModel = viewModel(),
    cursoViewModel: CursoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var nombreCurso by remember { mutableStateOf("") }
    var estudianteBusqueda by remember { mutableStateOf("") }
    // *** CAMBIO CLAVE: estudiantesSeleccionados ahora guarda IDs (String) ***
    val estudiantesSeleccionados = remember { mutableStateListOf<String>() }

    // Usamos el StateFlow 'estudiantes' del VinculacionViewModel, no 'listaEstudiantes' directamente.
    // Esto asegura que la UI reaccione a los cambios del ViewModel.
    val todosLosEstudiantesDesdeVM by vinculacionViewModel.estudiantes.collectAsState()

    // *** CAMBIO CLAVE: Llamar a obtenerEstudiantes cuando el Composable se inicializa ***
    LaunchedEffect(Unit) {
        Log.d("CursoFormulario", "Cargando todos los estudiantes disponibles...")
        vinculacionViewModel.obtenerEstudiantes()
    }

    val estudiantesFiltrados = remember(estudianteBusqueda, todosLosEstudiantesDesdeVM, estudiantesSeleccionados) {
        if (estudianteBusqueda.isBlank()) {
            emptyList()
        } else {
            val query = estudianteBusqueda.lowercase()
            todosLosEstudiantesDesdeVM
                .filter { estudiante ->
                    // Filtra por nombre o ID y asegura que no esté ya seleccionado (por ID)
                    (estudiante.nombre.lowercase().contains(query) ||
                            estudiante.id.lowercase().contains(query)) &&
                            !estudiantesSeleccionados.contains(estudiante.id) // *** Comparar por ID ***
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Curso") },
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
            OutlinedTextField(
                value = nombreCurso,
                onValueChange = { nombreCurso = it },
                label = { Text("Nombre del Curso") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Agregar Estudiantes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = estudianteBusqueda,
                onValueChange = { estudianteBusqueda = it },
                label = { Text("Buscar Estudiante (Nombre o ID)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (estudiantesFiltrados.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                    LazyColumn {
                        items(estudiantesFiltrados) { estudiante ->
                            ListItem(
                                headlineContent = { Text(estudiante.nombre) },
                                supportingContent = { Text("ID: ${estudiante.id}") },
                                trailingContent = {
                                    IconButton(onClick = {
                                        estudiantesSeleccionados.add(estudiante.id) // *** Añadir ID ***
                                        estudianteBusqueda = ""
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = "Añadir Estudiante")
                                    }
                                },
                                modifier = Modifier.clickable {
                                    estudiantesSeleccionados.add(estudiante.id) // *** Añadir ID ***
                                    estudianteBusqueda = ""
                                }
                            )
                            Divider()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (estudianteBusqueda.isNotBlank() && todosLosEstudiantesDesdeVM.isNotEmpty()) {
                // Mensaje si no se encontraron resultados para la búsqueda
                Text(
                    text = "No se encontraron estudiantes con ese nombre o ID.",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (todosLosEstudiantesDesdeVM.isEmpty() && estudianteBusqueda.isBlank()){
                // Mensaje si no hay estudiantes disponibles en el sistema
                Text(
                    text = "No hay estudiantes disponibles en el sistema.",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }


            Text(
                text = "Estudiantes en el Curso:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (estudiantesSeleccionados.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(estudiantesSeleccionados) { estudianteId -> // Ahora iteramos sobre IDs
                        // *** CAMBIO CLAVE: Buscar el nombre por ID para mostrarlo ***
                        val estudianteMostrar = todosLosEstudiantesDesdeVM.find { it.id == estudianteId }
                        ListItem(
                            headlineContent = { Text(estudianteMostrar?.nombre ?: "Estudiante desconocido") },
                            supportingContent = { Text("ID: $estudianteId") },
                            trailingContent = {
                                IconButton(onClick = { estudiantesSeleccionados.remove(estudianteId) }) {
                                    // Usamos Icons.Default.Close para una 'X' de eliminar
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar Estudiante")
                                }
                            }
                        )
                        Divider()
                    }
                }
            } else {
                Text("Aún no se han añadido estudiantes a este curso.")
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val profesorUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (nombreCurso.isNotBlank()) {
                        val nuevoCurso = Curso(
                            nombreCurso = nombreCurso, // Asegúrate de que 'nombreCurso' es el campo en tu data class Curso
                            estudiantes = estudiantesSeleccionados.toList(), // Ahora contendrá IDs
                            profesor = profesorUid
                        )
                        cursoViewModel.guardarNuevoCurso(nuevoCurso)
                        Toast.makeText(context, "Curso '${nombreCurso}' creado.", Toast.LENGTH_SHORT).show()
                        onBack() // Volver a la pantalla anterior después de crear
                    } else {
                        Toast.makeText(context, "El nombre del curso no puede estar vacío.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombreCurso.isNotBlank()
            ) {
                Text("Crear Curso")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCursoFormulario() {
    MaterialTheme {
        CursoFormulario(
            onCursoCreado = { curso ->
                println("Curso Creado: ${curso.nombreCurso}, Estudiantes: ${curso.estudiantes}")
            },
            onBack = {}
        )
    }
}