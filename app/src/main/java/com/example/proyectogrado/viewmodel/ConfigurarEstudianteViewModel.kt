// app/src/main/java/com/example/proyectogrado/ui/viewmodel/ConfigurarEstudianteViewModel.kt
package com.example.proyectogrado.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Extiende AndroidViewModel para acceso al Context
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proyectogrado.data.repository.EstudianteRepository // Tu EstudianteRepository fusionado
import com.example.proyectogrado.services.EnvioUsoScheduler // De tu compañero, si lo usa
import com.example.proyectogrado.services.EnvioUsoWorker // Tu Worker
import com.example.proyectogrado.utils.PreferenciasEstudiante // Tu PreferenciasEstudiante fusionada
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

// Este ViewModel ahora maneja lógicas tanto del estudiante como del padre.
class ConfigurarEstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val estudianteRepository = EstudianteRepository() // Tu repositorio fusionado

    // --- Estados para la UI (fusionados de ambas lógicas) ---

    // De tu compañero (mensajes generales y estado de éxito para operaciones de padre)
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _exito = MutableStateFlow(false)
    val exito: StateFlow<Boolean> = _exito

    // Tuyos (estados de validación y configuración específica del estudiante)
    private val _isValidating = MutableStateFlow(false)
    val isValidating: StateFlow<Boolean> = _isValidating

    private val _validationMessage = MutableStateFlow("")
    val validationMessage: StateFlow<String> = _validationMessage

    private val _isConfigured = MutableStateFlow(false)
    val isConfigured: StateFlow<Boolean> = _isConfigured


    // --- Lógica del Estudiante (Tu implementación original) ---

    /**
     * Verifica si el ID de estudiante existe en Firebase y lo configura localmente.
     * Esta función es llamada desde la `ConfigurarEstudianteScreen` (lado del estudiante).
     * @param idEstudiante ID ingresado por el estudiante.
     */
    fun onEstudianteConfigurarId(idEstudiante: String) {
        // Reiniciar estados para una nueva operación del estudiante
        _isValidating.value = true
        _validationMessage.value = ""
        _isConfigured.value = false
        // También reiniciar los estados del compañero para no mostrar mensajes antiguos
        _mensaje.value = ""
        _exito.value = false

        if (idEstudiante.isBlank()) {
            _validationMessage.value = "⚠️ El ID de estudiante no puede estar vacío."
            _isValidating.value = false
            return
        }

        Log.d("ConfigEstudianteVM", "Iniciando verificación y configuración para ID de ESTUDIANTE: $idEstudiante")

        viewModelScope.launch {
            try {
                // Verificar si el estudiante existe en Firebase usando tu repositorio fusionado
                val estudianteExiste = verificarEstudianteExiste(idEstudiante) // Función local en este VM

                if (estudianteExiste) {
                    Log.d("ConfigEstudianteVM", "Estudiante '$idEstudiante' existe en Firebase. Procediendo a guardar ID localmente.")
                    // Paso 2: Guardar el ID del estudiante en las preferencias locales (usa tu API saveIdEstudiante)
                    PreferenciasEstudiante.saveIdEstudiante(getApplication(), idEstudiante)

                    // Paso 3: Programar la tarea periódica para enviar el uso de la aplicación (tu lógica)
                    programarEnvioUso(getApplication())

                    _validationMessage.value = "Configuración exitosa para el estudiante $idEstudiante."
                    _isConfigured.value = true // Tu estado de éxito
                    Log.d("ConfigEstudianteVM", "Configuración completa y exitosa para ID: $idEstudiante")

                } else {
                    _validationMessage.value = "El ID de estudiante '$idEstudiante' no se encontró en Firebase."
                    _isConfigured.value = false
                    Log.w("ConfigEstudianteVM", "Estudiante '$idEstudiante' NO encontrado en Firebase.")
                }
            } catch (e: Exception) {
                _validationMessage.value = "Error al configurar estudiante: ${e.message}"
                _isConfigured.value = false
                Log.e("ConfigEstudianteVM", "Error en onEstudianteConfigurarId: ${e.message}", e)
            } finally {
                _isValidating.value = false
            }
        }
    }

    /**
     * Programa el trabajo periódico para enviar el uso de la aplicación.
     * (Tu lógica original para el Worker).
     */
    private fun programarEnvioUso(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<EnvioUsoWorker>(
            15, TimeUnit.MINUTES, // Repetir cada 15 minutos
            5, TimeUnit.MINUTES // Flexibilidad para ejecutar en los últimos 5 minutos del intervalo
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "EnvioUsoDiario",
            ExistingPeriodicWorkPolicy.UPDATE, // Actualiza la tarea si ya existe
            workRequest
        )
        Log.d("ConfigEstudianteVM", "Tarea de envío de uso programada.")
    }

    // --- Lógica del Padre (Implementación de tu compañero) ---

    /**
     * Guarda un nuevo estudiante y lo vincula al padre.
     * Esta función es llamada desde la UI del padre (por ejemplo, VincularEstudianteScreen).
     * @param context Contexto de la aplicación.
     * @param estudianteId ID del estudiante a guardar.
     */
    fun guardarEstudianteComoPadre(context: Context, estudianteId: String) {
        // Reinicia los estados relevantes para esta operación del padre
        _mensaje.value = ""
        _exito.value = false
        // No tocar los estados de estudiante (isValidating, isConfigured, etc.) aquí

        if (estudianteId.isBlank()) {
            _mensaje.value = "⚠️ El ID no puede estar vacío"
            return
        }

        val uid = auth.currentUser?.uid ?: "" // Se asume que el padre está autenticado
        if (uid.isBlank()) {
            _mensaje.value = "Error: Usuario padre no autenticado."
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("estudiantes")
                    .document(estudianteId)
                    .get()
                    .await()

                if (document.exists()) {
                    _mensaje.value = "⚠️ Este ID ya está en uso por otro estudiante."
                } else {
                    // El ID no existe, podemos guardarlo
                    firestore.collection("estudiantes")
                        .document(estudianteId)
                        .set(mapOf("id" to estudianteId, "vinculadoPor" to uid))
                        .await()

                    // Luego, vincularlo en la subcolección del padre
                    firestore.collection("vinculaciones")
                        .document(uid)
                        .collection("estudiantes")
                        .document(estudianteId)
                        .set(mapOf("id" to estudianteId, "nombre" to estudianteId)) // Puedes añadir más campos
                        .await()

                    EnvioUsoScheduler.programarEnvioDiario(context) // Usa el scheduler de tu compañero
                    _mensaje.value = "✅ Estudiante ${estudianteId} configurado y vinculado correctamente."
                    _exito.value = true
                    // FirebaseAuth.getInstance().signOut() // Cuidado con signOut si el padre debe seguir logueado.
                }
            } catch (e: Exception) {
                _mensaje.value = "❌ Error al guardar o verificar el ID: ${e.message}"
                Log.e("ConfigEstudianteVM", "Error en guardarEstudianteComoPadre: ${e.message}", e)
            }
        }
    }


    /**
     * Verifica la existencia del estudiante en Firestore (función auxiliar compartida).
     * @param estudianteId ID del estudiante a verificar.
     * @return `true` si el estudiante existe, `false` en caso contrario.
     */
    private suspend fun verificarEstudianteExiste(estudianteId: String): Boolean {
        // Usa directamente Firestore aquí, o si tu EstudianteRepository tiene una función similar
        // que use suspend, podrías usarla. Por ahora, mantenemos la lógica directa de tu compañero.
        val db = FirebaseFirestore.getInstance()
        return try {
            Log.d("EstudianteRepository", "Iniciando verificación de existencia para estudiante ID: '$estudianteId'")
            val documentSnapshot = db.collection("estudiantes")
                .document(estudianteId)
                .get()
                .await()
            val exists = documentSnapshot.exists()
            Log.d("EstudianteRepository", "Documento 'estudiantes/$estudianteId' existe: $exists")
            exists
        } catch (e: Exception) {
            Log.e("EstudianteRepository", "Error al verificar la existencia del estudiante '$estudianteId': ${e.message}", e)
            false
        }
    }
}
