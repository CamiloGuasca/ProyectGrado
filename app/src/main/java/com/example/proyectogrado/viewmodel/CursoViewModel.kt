package com.example.proyectogrado.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.data.repository.CursoRepository
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.domain.model.Curso
import com.example.proyectogrado.domain.model.Estudiante
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CursoViewModel(
    private val cursoRepository: CursoRepository,
    private val estudianteRepository: EstudianteRepository = EstudianteRepository()
    ) : ViewModel() { // Asegúrate de que el constructor acepta CursoRepository

    private val _estudiantesEnCursoUI = MutableStateFlow<List<Estudiante>>(emptyList())
    val estudiantesEnCursoUI: StateFlow<List<Estudiante>> = _estudiantesEnCursoUI

    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    private val _cursoGuardadoExitoso = MutableStateFlow<Boolean?>(null)
    val cursoGuardadoExitoso: StateFlow<Boolean?> = _cursoGuardadoExitoso

    private val _cursoSeleccionado = MutableStateFlow<Curso?>(null)
    val cursoSeleccionado: StateFlow<Curso?> = _cursoSeleccionado

    // Estados para operaciones de edición/eliminación
    private val _operacionExitosa = MutableStateFlow<Boolean?>(null)
    val operacionExitosa: StateFlow<Boolean?> = _operacionExitosa

    fun guardarNuevoCurso(curso: Curso) {
        viewModelScope.launch {
            cursoRepository.CrearCurso(curso) { success, idCursoGenerado ->
                if (success) {
                    Log.d("CursoVM", "Curso '${curso.nombreCurso}' guardado con éxito. ID: $idCursoGenerado")
                    _cursoGuardadoExitoso.value = true
                    cargarCursosDelProfesor() // Recargar para actualizar la lista
                } else {
                    Log.e("CursoVM", "Error al guardar curso '${curso.nombreCurso}'.")
                    _cursoGuardadoExitoso.value = false
                }
            }
        }
    }

    fun resetearEstadoGuardado() {
        _cursoGuardadoExitoso.value = null
    }

    fun cargarCursosDelProfesor(searchTerm: String = "") {
        viewModelScope.launch {
            val profesorId = FirebaseAuth.getInstance().currentUser?.uid
            if (profesorId != null) {
                cursoRepository.obtenerCursosPorProfesor(profesorId, searchTerm) { loadedCursos ->
                    _cursos.value = loadedCursos
                    Log.d("CursoVM", "Cursos cargados para el profesor $profesorId: ${loadedCursos.size} cursos.")
                }
            } else {
                Log.e("CursoVM", "No hay usuario logueado para cargar cursos.")
                _cursos.value = emptyList()
            }
        }
    }

    fun cargarCursoPorId(idCurso: String) {
        viewModelScope.launch {
            Log.d("CursoVM", "Iniciando carga de curso con ID: $idCurso")
            cursoRepository.obtenerCursoPorId(idCurso) { cursoCargadoDesdeRepo ->
                _cursoSeleccionado.value = cursoCargadoDesdeRepo // Actualizar el StateFlow del curso básico

                if (cursoCargadoDesdeRepo != null) {
                    Log.d("CursoVM", "Curso '${cursoCargadoDesdeRepo.nombreCurso}' cargado desde repositorio. IDs de estudiantes: ${cursoCargadoDesdeRepo.estudiantes.joinToString()}")

                    if (cursoCargadoDesdeRepo.estudiantes.isNotEmpty()) {
                        // *** ¡Usar la nueva función para obtener objetos Estudiante completos! ***
                        estudianteRepository.obtenerEstudiantesPorIds(cursoCargadoDesdeRepo.estudiantes) { listaEstudiantesCompletos ->
                            Log.d("CursoVM", "Objetos Estudiante obtenidos para UI (${listaEstudiantesCompletos.size}): ${listaEstudiantesCompletos.map { it.nombre }.joinToString()}")
                            _estudiantesEnCursoUI.value = listaEstudiantesCompletos // Actualizar el StateFlow para la UI
                        }
                    } else {
                        _estudiantesEnCursoUI.value = emptyList() // No hay estudiantes, vaciar la lista para la UI
                        Log.d("CursoVM", "Curso con ID ${idCurso} cargado. No tiene estudiantes asociados.")
                    }
                } else {
                    _estudiantesEnCursoUI.value = emptyList() // Si no se encuentra el curso, vaciar la lista de UI
                    Log.e("CursoVM", "No se encontró curso con ID ${idCurso}.")
                }
            }
        }
    }



    fun eliminarCurso(cursoId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            cursoRepository.eliminarCurso(cursoId) { success ->
                _operacionExitosa.value = success
                if (success) {
                    Log.d("CursoVM", "Curso eliminado con ID: $cursoId.")
                    _cursoSeleccionado.value = null // Limpiar curso seleccionado
                    // Opcional: Recargar la lista de cursos si es necesario
                    cargarCursosDelProfesor()
                } else {
                    Log.e("CursoVM", "Fallo al eliminar curso con ID: $cursoId.")
                }
                onComplete(success) // Notificar a la UI
            }
        }
    }

    fun actualizarNombreCurso(cursoId: String, nuevoNombre: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            cursoRepository.actualizarNombreCurso(cursoId, nuevoNombre) { success ->
                _operacionExitosa.value = success
                if (success) {
                    Log.d("CursoVM", "Nombre del curso $cursoId actualizado a '$nuevoNombre'.")
                    // Actualizar el curso seleccionado localmente
                    val updatedCurso = _cursoSeleccionado.value?.copy(nombreCurso = nuevoNombre)
                    _cursoSeleccionado.value = updatedCurso
                } else {
                    Log.e("CursoVM", "Fallo al actualizar nombre del curso $cursoId.")
                }
                onComplete(success) // Notificar a la UI
            }
        }
    }

    fun agregarEstudianteACurso(cursoId: String, nuevoEstudianteNombre: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            cursoRepository.agregarEstudianteACurso(cursoId, nuevoEstudianteNombre) { success ->
                _operacionExitosa.value = success
                if (success) {
                    Log.d("CursoVM", "Estudiante '$nuevoEstudianteNombre' agregado al curso $cursoId.")
                    // Actualizar el curso seleccionado localmente
                    val currentEstudiantes = _cursoSeleccionado.value?.estudiantes?.toMutableList() ?: mutableListOf()
                    if (!currentEstudiantes.contains(nuevoEstudianteNombre)) {
                        currentEstudiantes.add(nuevoEstudianteNombre)
                        val updatedCurso = _cursoSeleccionado.value?.copy(estudiantes = currentEstudiantes)
                        _cursoSeleccionado.value = updatedCurso
                    }
                } else {
                    Log.e("CursoVM", "Fallo al agregar estudiante '$nuevoEstudianteNombre' al curso $cursoId.")
                }
                onComplete(success) // Notificar a la UI
            }
        }
    }

    fun resetOperacionExitosaEstado() {
        _operacionExitosa.value = null
    }

    init {
        cargarCursosDelProfesor()
    }
}