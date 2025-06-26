// app/src/main/java/com/example/proyectogrado/viewmodel/HorarioUsoViewModel.kt
package com.example.proyectogrado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.data.repository.HorarioUsoRepository // Asegúrate de esta importación
import com.example.proyectogrado.domain.model.HorarioUso
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class HorarioUsoViewModel : ViewModel() {

    private val horarioRepository = HorarioUsoRepository()
    private val TAG = "HorarioUsoViewModel"

    private val _horarios = MutableStateFlow<List<HorarioUso>>(emptyList())
    val horarios: StateFlow<List<HorarioUso>> = _horarios

    private val _horarioEnEdicion = MutableStateFlow<HorarioUso?>(null)
    val horarioEnEdicion: StateFlow<HorarioUso?> = _horarioEnEdicion

    private var horariosListener: ListenerRegistration? = null

    fun cargarHorarios(idEstudiante: String) {
        horariosListener?.remove()

        horariosListener = horarioRepository.getTodosLosHorariosRealtime(idEstudiante) { listaHorarios ->
            _horarios.value = listaHorarios
            Log.d(TAG, "Horarios cargados para estudiante $idEstudiante: ${listaHorarios.size} horarios")
        }
    }

    fun cargarHorarioParaEdicion(idEstudiante: String, horarioId: String) {
        viewModelScope.launch {
            // Asegúrate de que getHorarioPorId en HorarioUsoRepository sea una función suspendida
            val horario = horarioRepository.getHorarioPorId(idEstudiante, horarioId)
            _horarioEnEdicion.value = horario
            if (horario == null) {
                Log.w(TAG, "No se encontró el horario con ID $horarioId para edición.")
            }
        }
    }

    fun limpiarHorarioEnEdicion() {
        _horarioEnEdicion.value = null
    }

    fun guardarHorario(idEstudiante: String, horario: HorarioUso, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = horarioRepository.guardarHorario(idEstudiante, horario) // HorarioUsoRepository.guardarHorario devuelve Boolean
            onResult(success) // Pasa el resultado Boolean a la lambda onResult
            if (success) {
                Log.d(TAG, "Horario ${horario.id} guardado/actualizado exitosamente.")
            } else {
                Log.e(TAG, "Fallo al guardar/actualizar el horario ${horario.id}.")
            }
        }
    }

    fun eliminarHorario(idEstudiante: String, horarioId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = horarioRepository.eliminarHorario(idEstudiante, horarioId) // HorarioUsoRepository.eliminarHorario devuelve Boolean
            onResult(success) // Pasa el resultado Boolean a la lambda onResult
            if (success) {
                Log.d(TAG, "Horario $horarioId eliminado exitosamente.")
            } else {
                Log.e(TAG, "Fallo al eliminar el horario $horarioId.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        horariosListener?.remove()
        Log.d(TAG, "HorarioUsoViewModel onCleared: Listener removido.")
    }
}