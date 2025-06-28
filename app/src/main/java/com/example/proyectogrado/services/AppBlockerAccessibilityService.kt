// app/src/main/java/com/example/proyectogrado/services/AppBlockerAccessibilityService.kt
package com.example.proyectogrado.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.example.proyectogrado.data.repository.EstudianteRepository
import com.example.proyectogrado.utils.PreferenciasEstudiante // TU PreferenciasEstudiante (fusionado)
import com.example.proyectogrado.domain.model.HorarioUso // Asegúrate de importar HorarioUso si es necesario para el for-loop
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot // Asegúrate de importar QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime // Si minSdk >= 26, usa java.time.
import java.time.Duration // Si minSdk >= 26, usa java.time.Duration.
import java.time.format.DateTimeParseException // Si minSdk >= 26, usa java.time.format.DateTimeParseException
import java.util.concurrent.CopyOnWriteArraySet // Asegúrate de importar CopyOnWriteArraySet

class AppBlockerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val estudianteRepository = EstudianteRepository() // Tu EstudianteRepository (fusionado)

    private val _serviceConnected = MutableStateFlow(false)
    val serviceConnected: StateFlow<Boolean> = _serviceConnected

    private val blockedPackageNames = CopyOnWriteArraySet<String>()

    private var lastAppPackageName: String? = null
    private var appSwitchTimestamp: Long = 0L

    // Si tu servicio usa HorarioUsoRepository para los listeners, asegúrate de tenerlo aquí
    // private val horarioUsoRepository = HorarioUsoRepository() // Podría ser necesario si los horarios vienen de aquí

    companion object {
        private const val TAG = "AppBlockerService"
        var isRunning = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        _serviceConnected.value = true
        isRunning = true
        Log.d(TAG, "Servicio de Accesibilidad Conectado (Opción B)")

        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        info.notificationTimeout = 100

        this.serviceInfo = info

        serviceScope.launch {
            Log.d(TAG, "Lanzando corrutina para cargar horarios.")
            cargarHorariosDeBloqueo()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            val className = event.className?.toString()

            if (packageName != null && className != null) {
                Log.d(TAG, "Ventana cambiada a: $packageName")
                registrarUsoApp(packageName)
                evaluarBloqueoApp(packageName)
            }
        }
    }

    private fun registrarUsoApp(currentAppPackageName: String) {
        if (lastAppPackageName != null && lastAppPackageName != currentAppPackageName) {
            val duration = System.currentTimeMillis() - appSwitchTimestamp
            Log.d(TAG, "App Anterior: $lastAppPackageName usada por ${duration / 1000} segundos.")
        }
        lastAppPackageName = currentAppPackageName
        appSwitchTimestamp = System.currentTimeMillis()
    }

    private fun evaluarBloqueoApp(packageName: String) {
        val debeBloquear = blockedPackageNames.contains(packageName)
        Log.d(TAG, "Evaluando bloqueo para $packageName: ¿Debe bloquear? $debeBloquear (Lista: $blockedPackageNames)")

        if (debeBloquear) {
            Log.w(TAG, "Bloqueando aplicación: $packageName")
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    private suspend fun cargarHorariosDeBloqueo() {
        // CORRECCIÓN CLAVE: Pasa 'applicationContext' a getIdEstudiante()
        val studentId = PreferenciasEstudiante.getIdEstudiante(applicationContext)
        Log.d(TAG, "ID de estudiante recuperado de preferencias: '$studentId'")

        // CORRECCIÓN: Usar isEmpty() o isBlank() ya que studentId ya no es String?
        if (studentId.isEmpty()) {
            Log.e(TAG, "ID de estudiante no encontrado en preferencias. No se cargarán los horarios.")
            return
        }

        try {
            Log.d(TAG, "Intentando cargar horarios para estudiante: $studentId")
            // estudianteRepository.getHorariosUso(studentId) - Esto ya está en tu repositorio fusionado
            val horariosSnapshot: QuerySnapshot = estudianteRepository.getHorariosUso(studentId)

            if (horariosSnapshot.isEmpty) {
                Log.d(TAG, "No se encontraron documentos en la subcolección 'horariosUso' para $studentId.")
                blockedPackageNames.clear()
                return
            }

            blockedPackageNames.clear()
            Log.d(TAG, "Horarios cargados. Procesando ${horariosSnapshot.documents.size} documentos.")

            for (document in horariosSnapshot.documents) {
                val restrictedApps = document.get("aplicacionesRestringidas") as? List<String>
                val horarioId = document.id
                val nombreHorario = document.getString("nombreHorario") ?: "N/A"

                Log.d(TAG, "Procesando documento de horario: ID='${horarioId}', Nombre: '$nombreHorario'")

                if (restrictedApps != null) {
                    if (restrictedApps.isNotEmpty()) {
                        for (appPackage in restrictedApps) {
                            blockedPackageNames.add(appPackage)
                            Log.d(TAG, "App '${appPackage}' añadida a la lista de bloqueo desde horario '$nombreHorario'.")
                        }
                    } else {
                        Log.d(TAG, "Horario '$nombreHorario' (ID: $horarioId) tiene una lista vacía de 'aplicacionesRestringidas'.")
                    }
                } else {
                    Log.w(TAG, "Documento de horario '$horarioId' no tiene 'aplicacionesRestringidas' o no es un array de Strings.")
                }
            }
            Log.d(TAG, "Carga de horarios completada. Total de apps bloqueadas: ${blockedPackageNames.size} -> $blockedPackageNames")

        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar horarios de bloqueo: ${e.message}", e)
            blockedPackageNames.clear()
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "Servicio de Accesibilidad Interrumpido.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        _serviceConnected.value = false
        isRunning = false
        serviceScope.coroutineContext.cancel()
        Log.d(TAG, "Servicio de Accesibilidad Desconectado (onUnbind).")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        _serviceConnected.value = false
        isRunning = false
        serviceScope.coroutineContext.cancel()
        Log.d(TAG, "Servicio de Accesibilidad Destruido.")
        super.onDestroy()
    }
}
