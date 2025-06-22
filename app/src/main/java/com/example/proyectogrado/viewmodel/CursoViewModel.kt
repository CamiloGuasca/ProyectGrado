package com.example.proyectogrado.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.data.repository.CursoRepository
import com.example.proyectogrado.domain.model.Curso
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CursoViewModel: ViewModel() {
    private val cursoRepository = CursoRepository()

    // StateFlow para la lista de cursos que la UI observará
    private val _cursos = MutableStateFlow<List<Curso>>(emptyList())
    val cursos: StateFlow<List<Curso>> = _cursos

    // StateFlow para indicar si el guardado fue exitoso (opcional, para feedback de UI)
    private val _cursoGuardadoExitoso = MutableStateFlow<Boolean?>(null)
    val cursoGuardadoExitoso: StateFlow<Boolean?> = _cursoGuardadoExitoso

    fun guardarNuevoCurso(curso: Curso) {
        viewModelScope.launch {
            cursoRepository.CrearCurso(curso) { success ->
                if (success) {
                    Log.d("CursoVM", "Curso '${curso.nombreCurso}' guardado con éxito a través del ViewModel.")
                    //_cursoGuardadoExitoso.value = true // Actualizar estado de éxito
                } else {
                    Log.e("CursoVM", "Error al guardar curso '${curso.nombreCurso}' a través del ViewModel.")
                    //_cursoGuardadoExitoso.value = false // Actualizar estado de error
                }
            }
        }
    }

    fun resetearEstadoGuardado() {
        _cursoGuardadoExitoso.value = null
    }


    fun cargarCursosDelProfesor(searchTerm: String = "") {
        viewModelScope.launch {
            val profesorId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (profesorId != null) {
                cursoRepository.obtenerCursosPorProfesor(profesorId, searchTerm) { loadedCursos ->
                    _cursos.value = loadedCursos
                    Log.d("CursoVM", "Cursos cargados para el profesor $profesorId: ${loadedCursos.size} cursos.")
                }
            } else {
                Log.e("CursoVM", "No hay usuario logueado para cargar cursos.")
                _cursos.value = emptyList() // Limpiar la lista si no hay profesor
            }
        }
    }

    // Inicializar la carga de cursos cuando el ViewModel se crea
    init {
        cargarCursosDelProfesor()
    }
}