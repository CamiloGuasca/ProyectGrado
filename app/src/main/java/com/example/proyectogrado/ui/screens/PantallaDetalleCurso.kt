package com.example.proyectogrado.ui.screens

import android.util.Log
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.domain.model.Curso
import com.example.proyectogrado.domain.model.Estudiante // ¡IMPORTANTE! Asegúrate de importar Estudiante aquí
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel // Asumiendo que usas este ViewModel para obtener estudiantes disponibles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleCurso(
    idCurso: String,
    cursoViewModel: CursoViewModel,
    vinculacionViewModel: VinculacionViewModel, // Para la lista de estudiantes disponibles
    onBack: () -> Unit,
    DetalleEstudiante: (String) -> Unit // Recibe el ID del estudiante
) {
    val context = LocalContext.current
    val curso by cursoViewModel.cursoSeleccionado.collectAsState() // Esto solo para mostrar detalles básicos del curso
    val estudiantesDelCursoUI by cursoViewModel.estudiantesEnCursoUI.collectAsState() // ¡NUEVO! Lista de objetos Estudiante completos
    val operacionExitosa by cursoViewModel.operacionExitosa.collectAsState()

    var searchText by remember { mutableStateOf("") }

    // Filtra la lista de Estudiante (que ahora contiene ID y Nombre)
    val filteredEstudiantesInCourse = remember(estudiantesDelCursoUI, searchText) {
        if (searchText.isBlank()) {
            estudiantesDelCursoUI
        } else {
            val lowerCaseSearchText = searchText.lowercase()
            estudiantesDelCursoUI.filter { estudiante ->
                estudiante.nombre.lowercase().contains(lowerCaseSearchText) ||
                        estudiante.id.lowercase().contains(lowerCaseSearchText)
            }
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var nuevoNombreCurso by remember { mutableStateOf("") }

    var addStudentSearchText by remember { mutableStateOf("") }
    val availableStudents by vinculacionViewModel.estudiantes.collectAsState() // Lista de todos los estudiantes del sistema

    val filteredAvailableStudents = remember(availableStudents, addStudentSearchText, estudiantesDelCursoUI) {
        val lowerCaseSearchText = addStudentSearchText.lowercase()
        availableStudents.filter { estudiante ->
            // Verifica si el estudiante NO está ya en la lista de estudiantes del curso (por ID)
            val isAlreadyInCourse = estudiantesDelCursoUI.any { it.id == estudiante.id }
            (!isAlreadyInCourse) && (estudiante.nombre.lowercase().contains(lowerCaseSearchText) || estudiante.id.lowercase().contains(lowerCaseSearchText))
        }
    }

    LaunchedEffect(operacionExitosa) {
        operacionExitosa?.let { success ->
            if (success) {
                Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
                // El VM ya se encarga de recargar si es necesario.
            } else {
                Toast.makeText(context, "Operación fallida", Toast.LENGTH_SHORT).show()
            }
            cursoViewModel.resetOperacionExitosaEstado()
        }
    }

    LaunchedEffect(idCurso) {
        // Al cargar el curso, el ViewModel también preparará la lista de estudiantes para la UI
        cursoViewModel.cargarCursoPorId(idCurso)
        Log.d("DETALLE_CURSO", "Cargando curso con ID: $idCurso")
    }

    LaunchedEffect(Unit) {
        vinculacionViewModel.obtenerEstudiantes() // Necesario para el diálogo de añadir estudiantes
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(curso?.nombreCurso ?: "Cargando Curso...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showEditNameDialog = true
                        nuevoNombreCurso = curso?.nombreCurso ?: ""
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Cambiar nombre del curso")
                    }
                    IconButton(onClick = {
                        showAddStudentDialog = true
                        addStudentSearchText = ""
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Añadir estudiante")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar curso")
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
            curso?.let { currentCurso ->
                Text(
                    text = "Detalles del Curso: ${currentCurso.nombreCurso}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "ID del Curso: ${currentCurso.idCurso}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Profesor ID: ${currentCurso.profesor}", // Podrías convertir también este ID a nombre si lo necesitas
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar estudiante en curso") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Estudiantes Inscritos (${filteredEstudiantesInCourse.size} de ${estudiantesDelCursoUI.size}):",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (filteredEstudiantesInCourse.isEmpty() && searchText.isNotBlank()) {
                    Text("No se encontraron estudiantes con ese nombre o ID en este curso.", style = MaterialTheme.typography.bodyMedium)
                } else if (filteredEstudiantesInCourse.isEmpty() && searchText.isBlank()) {
                    Text("Este curso no tiene estudiantes inscritos aún.", style = MaterialTheme.typography.bodyMedium)
                }
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredEstudiantesInCourse) { estudiante -> // ¡Ahora 'estudiante' es un objeto Estudiante completo!
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = estudiante.nombre, style = MaterialTheme.typography.titleMedium) // Mostramos el nombre
                                    Text(text = "ID: ${estudiante.id}", style = MaterialTheme.typography.bodySmall) // También podemos mostrar el ID
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            DetalleEstudiante(estudiante.id) // ¡Pasamos el ID correcto!
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Consultar Estudiante")
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Text("Curso no encontrado o cargando...", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    // --- DIÁLOGOS DE EDICIÓN ---
    if (showDeleteDialog && curso != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Curso") },
            text = { Text("¿Estás seguro de que quieres eliminar el curso '${curso?.nombreCurso}'?") },
            confirmButton = {
                Button(onClick = {
                    cursoViewModel.eliminarCurso(idCurso) { success ->
                        if (success) {
                            Toast.makeText(context, "Curso eliminado.", Toast.LENGTH_SHORT).show()
                            onBack() // Volver a la pantalla anterior si se elimina con éxito
                        } else {
                            Toast.makeText(context, "Error al eliminar curso.", Toast.LENGTH_SHORT).show()
                        }
                        showDeleteDialog = false
                    }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showEditNameDialog && curso != null) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Cambiar Nombre del Curso") },
            text = {
                OutlinedTextField(
                    value = nuevoNombreCurso,
                    onValueChange = { nuevoNombreCurso = it },
                    label = { Text("Nuevo nombre del curso") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (nuevoNombreCurso.isNotBlank()) {
                        cursoViewModel.actualizarNombreCurso(idCurso, nuevoNombreCurso) { success ->
                            if (success) {
                                Toast.makeText(context, "Nombre del curso actualizado.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar el nombre.", Toast.LENGTH_SHORT).show()
                            }
                            showEditNameDialog = false
                        }
                    } else {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showEditNameDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIÁLOGO PARA AGREGAR ESTUDIANTE ---
    if (showAddStudentDialog && curso != null) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Agregar Estudiante al Curso") },
            text = {
                Column {
                    OutlinedTextField(
                        value = addStudentSearchText,
                        onValueChange = { addStudentSearchText = it },
                        label = { Text("Buscar estudiante disponible") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredAvailableStudents.isEmpty() && addStudentSearchText.isNotBlank()) {
                        Text("No se encontraron estudiantes con ese nombre o ya están en el curso.", style = MaterialTheme.typography.bodySmall)
                    } else if (filteredAvailableStudents.isEmpty() && addStudentSearchText.isBlank()) {
                        Text("Empieza a escribir para buscar estudiantes.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(filteredAvailableStudents) { estudiante -> // 'estudiante' es un objeto Estudiante completo
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            // *** AQUÍ PASAS estudiante.id al ViewModel para añadirlo al curso ***
                                            cursoViewModel.agregarEstudianteACurso(idCurso, estudiante.id) { success ->
                                                if (success) {
                                                    Toast.makeText(context, "Estudiante '${estudiante.nombre}' agregado.", Toast.LENGTH_SHORT).show()
                                                    // Después de agregar, recargar los estudiantes disponibles para el diálogo
                                                    // para que el estudiante recién añadido no aparezca más.
                                                    vinculacionViewModel.obtenerEstudiantes()
                                                } else {
                                                    Toast.makeText(context, "Error al agregar estudiante.", Toast.LENGTH_SHORT).show()
                                                }
                                                showAddStudentDialog = false
                                                addStudentSearchText = ""
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${estudiante.nombre} (ID: ${estudiante.id})", // Muestras nombre e ID
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(Icons.Filled.Add, contentDescription = "Seleccionar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { /* El botón de confirmación puede ser innecesario si se agrega al hacer clic en el estudiante */ },
            dismissButton = {
                Button(onClick = {
                    showAddStudentDialog = false
                    addStudentSearchText = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}