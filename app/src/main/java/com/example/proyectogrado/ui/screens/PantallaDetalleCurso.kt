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
import com.example.proyectogrado.domain.model.Estudiante
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleCurso(
    idCurso: String,
    cursoViewModel: CursoViewModel,
    vinculacionViewModel: VinculacionViewModel,
    onBack: () -> Unit,
    DetalleEstudiante: (String) -> Unit
) {
    val context = LocalContext.current
    val curso by cursoViewModel.cursoSeleccionado.collectAsState()
    val estudiantesDelCursoUI by cursoViewModel.estudiantesEnCursoUI.collectAsState()
    val operacionExitosa by cursoViewModel.operacionExitosa.collectAsState()

    var searchText by remember { mutableStateOf("") }

    val filteredEstudiantesInCourse = remember(estudiantesDelCursoUI, searchText) {
        if (searchText.isBlank()) estudiantesDelCursoUI
        else estudiantesDelCursoUI.filter {
            it.nombre.contains(searchText, ignoreCase = true) ||
                    it.id.contains(searchText, ignoreCase = true)
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var nuevoNombreCurso by remember { mutableStateOf("") }
    var addStudentSearchText by remember { mutableStateOf("") }
    val availableStudents by vinculacionViewModel.estudiantes.collectAsState()

    val filteredAvailableStudents = remember(availableStudents, addStudentSearchText, estudiantesDelCursoUI) {
        availableStudents.filter {
            !estudiantesDelCursoUI.any { e -> e.id == it.id } &&
                    (it.nombre.contains(addStudentSearchText, ignoreCase = true) ||
                            it.id.contains(addStudentSearchText, ignoreCase = true))
        }
    }

    LaunchedEffect(operacionExitosa) {
        operacionExitosa?.let { success ->
            Toast.makeText(context, if (success) "Operación exitosa" else "Operación fallida", Toast.LENGTH_SHORT).show()
            cursoViewModel.resetOperacionExitosaEstado()
        }
    }

    LaunchedEffect(idCurso) {
        cursoViewModel.cargarCursoPorId(idCurso)
    }

    LaunchedEffect(Unit) {
        vinculacionViewModel.obtenerEstudiantes()
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
                Text("Detalles del Curso: ${currentCurso.nombreCurso}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ID del Curso: ${currentCurso.idCurso}", style = MaterialTheme.typography.bodyLarge)
                Text("Profesor ID: ${currentCurso.profesor}", style = MaterialTheme.typography.bodyLarge)

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Buscar estudiante en curso") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredEstudiantesInCourse.isEmpty()) {
                    Text("No hay estudiantes en el curso.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredEstudiantesInCourse) { estudiante ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(estudiante.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text("ID: ${estudiante.id}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { DetalleEstudiante(estudiante.id) }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Consultar Estudiante")
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: Text("Cargando curso...")
        }
    }

    if (showDeleteDialog && curso != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Curso") },
            text = { Text("Confirma eliminar el curso '\${curso?.nombreCurso}'?") },
            confirmButton = {
                Button(onClick = {
                    cursoViewModel.eliminarCurso(idCurso) {
                        if (it) onBack()
                    }
                    showDeleteDialog = false
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
            title = { Text("Cambiar nombre del curso") },
            text = {
                OutlinedTextField(
                    value = nuevoNombreCurso,
                    onValueChange = { nuevoNombreCurso = it },
                    label = { Text("Nuevo nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (nuevoNombreCurso.isNotBlank()) {
                        cursoViewModel.actualizarNombreCurso(idCurso, nuevoNombreCurso) {}
                        showEditNameDialog = false
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

    if (showAddStudentDialog && curso != null) {
        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = { Text("Agregar Estudiante al Curso") },
            text = {
                Column {
                    OutlinedTextField(
                        value = addStudentSearchText,
                        onValueChange = { addStudentSearchText = it },
                        label = { Text("Buscar estudiante") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(filteredAvailableStudents) { estudiante ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        cursoViewModel.agregarEstudianteACurso(idCurso, estudiante.id) {}
                                        showAddStudentDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${estudiante.nombre} (ID: ${estudiante.id})", modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.Add, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAddStudentDialog = false
                    addStudentSearchText = ""
                }) {
                    Text("Cancelar")
                }
            },
            confirmButton = {}
        )
    }
}
