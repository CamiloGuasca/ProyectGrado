package com.example.proyectogrado.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.domain.model.Estudiante
import kotlinx.coroutines.launch

class VinculacionViewModel : ViewModel() {
    private val repo = EstudianteRepository()
    var listaEstudiantes = mutableStateListOf<Estudiante>()
        private set

    fun vincular(nombre: String, id: String, padreUid: String) {
        val estudiante = Estudiante(id = id, nombre = nombre, vinculadoPor = padreUid)
        viewModelScope.launch {
            repo.vincularEstudiante(estudiante) { success ->
                if (success) {
                    listaEstudiantes.add(estudiante)
                }
            }
        }
    }

    fun cargarEstudiantes(padreUid: String) {
        repo.obtenerEstudiantesPorPadre(padreUid) {
            listaEstudiantes.clear()
            listaEstudiantes.addAll(it)
        }
    }
}
