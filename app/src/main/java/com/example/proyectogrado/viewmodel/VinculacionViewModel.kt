package com.example.proyectogrado.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.domain.model.Estudiante
import com.example.proyectogrado.services.EnvioUsoScheduler
import com.example.proyectogrado.services.EnvioUsoWorker
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VinculacionViewModel(
    private val EstudianteRepository: EstudianteRepository = EstudianteRepository()
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _estudiantes = MutableStateFlow<List<Estudiante>>(emptyList())
    val estudiantes: StateFlow<List<Estudiante>> = _estudiantes

    // --- NUEVO: Para el estudiante seleccionado ---
    private val _estudianteSeleccionado = MutableStateFlow<Estudiante?>(null)
    val estudianteSeleccionado: StateFlow<Estudiante?> = _estudianteSeleccionado
    // ---------------------------------------------

    var listaEstudiantes = mutableStateListOf<Estudiante>()
        private set

    init {
        obtenerEstudiantes()
    }

    fun cargarEstudiantesActuales(padreUid: String) {
        db.collection("vinculaciones")
            .document(padreUid)
            .collection("estudiantes")
            .get()
            .addOnSuccessListener { documentos ->
                val ids = documentos.map { it.id }

                if (ids.isEmpty()) {
                    listaEstudiantes.clear()
                    return@addOnSuccessListener
                }

                db.collection("estudiantes")
                    .whereIn(FieldPath.documentId(), ids)
                    .get()
                    .addOnSuccessListener { validos ->
                        val lista = validos.mapNotNull { it.toObject(Estudiante::class.java) }
                        listaEstudiantes.clear()
                        listaEstudiantes.addAll(lista)
                    }
            }
    }

    fun vincular(nombre: String, id: String, padreUid: String) {
        val estudiante = Estudiante(id = id, nombre = nombre, vinculadoPor = padreUid)

        viewModelScope.launch {
            FirebaseFirestore.getInstance().collection("estudiantes")
                .document(id)
                .set(estudiante)
                .addOnSuccessListener {
                    FirebaseFirestore.getInstance()
                        .collection("vinculaciones")
                        .document(padreUid)
                        .collection("estudiantes")
                        .document(id)
                        .set(estudiante)
                        .addOnSuccessListener {
                            listaEstudiantes.add(estudiante)
                        }
                }
        }
    }

    fun cargarEstudiantes(padreUid: String) {
        db.collection("vinculaciones")
            .document(padreUid)
            .collection("estudiantes")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(Estudiante::class.java) }
                listaEstudiantes.clear()
                listaEstudiantes.addAll(lista)
            }
    }

    fun obtenerEstudiantes() {
        Log.d("VinculacionVM", "Llamando a obtenerEstudiantes desde ViewModel...")
        viewModelScope.launch {
            EstudianteRepository.obtenerEstudiantes { loadedEstudiantes ->
                _estudiantes.value = loadedEstudiantes
                Log.d("VinculacionVM", "Estudiantes recibidos del repositorio: ${loadedEstudiantes.size}")
                if (loadedEstudiantes.isEmpty()) {
                    Log.w("VinculacionVM", "El repositorio devolvió una lista vacía de estudiantes.")
                }
            }
        }
    }

    // --- NUEVO: Función para cargar un estudiante por su ID ---
    fun cargarEstudiantePorId(estudianteId: String) {
        viewModelScope.launch {
            try {
                // Aquí usamos el repositorio que ya tienes
                EstudianteRepository.obtenerEstudiantePorId(estudianteId) { estudiante ->
                    _estudianteSeleccionado.value = estudiante
                    if (estudiante == null) {
                        Log.e("VinculacionVM", "Estudiante con ID $estudianteId no encontrado.")
                    }
                }
            } catch (e: Exception) {
                _estudianteSeleccionado.value = null
                Log.e("VinculacionVM", "Error al cargar estudiante $estudianteId: ${e.message}")
            }
        }
    }
    // --------------------------------------------------------

    fun programarEnvioDiario(context: Context) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
        }

        val currentTime = Calendar.getInstance().timeInMillis
        val delay = calendar.timeInMillis - currentTime
        val repeatInterval = TimeUnit.DAYS.toMillis(1)

        val workRequest = PeriodicWorkRequestBuilder<EnvioUsoWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "envioUsoDiario",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}