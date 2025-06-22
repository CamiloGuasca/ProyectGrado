import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate // Asegúrate de que esta importación exista
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.Curso


// IMPORTA TU VIEWMODEL Y DATA CLASS DESDE SUS UBICACIONES REALES
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.example.proyectogrado.domain.model.Estudiante // Ajusta esta ruta si es diferente
import com.example.proyectogrado.viewmodel.CursoViewModel
import com.example.proyectogrado.viewmodel.RegistroViewModel
import com.google.firebase.auth.FirebaseAuth

// Tu data class Curso (asumiendo que está definida así y no debe cambiar)
/*
data class Curso(
    val NombreCurso: String = "",
    val Profesor: String = "",
    val Estudiantes: List<String> = emptyList()
)*/

// NO NECESITAS REDEFINIR Estudiante AQUÍ, YA QUE SE IMPORTA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoFormulario(
    onCursoCreado: (Curso) -> Unit,
    // Inyecta tu VinculacionViewModel
    vinculacionViewModel: VinculacionViewModel = viewModel(),
    cursoViewModel: CursoViewModel = viewModel()
) {
    var nombreCurso by remember { mutableStateOf("") }
    var estudianteBusqueda by remember { mutableStateOf("") }
    val estudiantesSeleccionados = remember { mutableStateListOf<String>() }

    // Accede a 'listaEstudiantes' directamente desde tu ViewModel
    val todosLosEstudiantesDesdeVM = vinculacionViewModel.listaEstudiantes



    val estudiantesFiltrados = remember(estudianteBusqueda, todosLosEstudiantesDesdeVM, estudiantesSeleccionados) {
        if (estudianteBusqueda.isBlank()) {
            emptyList()
        } else {
            val query = estudianteBusqueda.lowercase()

            todosLosEstudiantesDesdeVM
                .filter { estudiante ->
                    (estudiante.nombre.lowercase().contains(query) ||
                            estudiante.id.lowercase().contains(query)) &&
                            !estudiantesSeleccionados.contains(estudiante.nombre)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Crear Nuevo Curso",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                                    estudiantesSeleccionados.add(estudiante.nombre)
                                    estudianteBusqueda = ""
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "Añadir Estudiante")
                                }
                            },
                            modifier = Modifier.clickable {
                                estudiantesSeleccionados.add(estudiante.nombre)
                                estudianteBusqueda = ""
                            }
                        )
                        Divider()
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Estudiantes en el Curso:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (estudiantesSeleccionados.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(estudiantesSeleccionados) { estudianteNombre ->
                    ListItem(
                        headlineContent = { Text(estudianteNombre) },
                        trailingContent = {
                            IconButton(onClick = { estudiantesSeleccionados.remove(estudianteNombre) }) {
                                Icon(Icons.Default.Add, contentDescription = "Eliminar Estudiante", modifier = Modifier.rotate(45f))
                            }
                        }
                    )
                    Divider()
                }
            }
        } else {
            Text("Aún no se han añadido estudiantes a este curso.")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val profesorUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (nombreCurso.isNotBlank()) {
                    val nuevoCurso = Curso(
                        nombreCurso = nombreCurso,
                        estudiantes = estudiantesSeleccionados.toList(),
                        profesor = profesorUid
                    )
                    cursoViewModel.guardarNuevoCurso(nuevoCurso)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombreCurso.isNotBlank()
        ) {
            Text("Crear Curso")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCursoFormulario() {
    MaterialTheme {
        CursoFormulario(onCursoCreado = { curso ->
            println("Curso Creado: ${curso.nombreCurso}, Estudiantes: ${curso.estudiantes}")
        })
    }
}