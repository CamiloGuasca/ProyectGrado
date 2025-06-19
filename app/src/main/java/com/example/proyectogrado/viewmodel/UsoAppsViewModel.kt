package com.example.proyectogrado.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.services.ServicioUsoApps
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsoAppsViewModel : ViewModel() {

    private val _estado = MutableStateFlow("Analizando uso de apps...")
    val estado: StateFlow<String> = _estado

    private val _cerrarApp = MutableStateFlow(false)
    val cerrarApp: StateFlow<Boolean> = _cerrarApp

    fun enviarUso(context: Context) {
        viewModelScope.launch {
            val servicio = ServicioUsoApps(context)
            val fecha = servicio.obtenerFechaActual()
            val idEstudiante = PreferenciasEstudiante.obtenerId(context)

            if (idEstudiante.isBlank()) {
                _estado.value = "⚠️ No se ha configurado el ID del estudiante"
                return@launch
            }

            val appsUsadas = servicio.obtenerUsoDeHoy()
            if (appsUsadas.isEmpty()) {
                _estado.value = "No se detectó uso de apps hoy."
                return@launch
            }

            val firestore = FirebaseFirestore.getInstance()
            val coleccion = firestore.collection("usoApps")
                .document(idEstudiante)
                .collection(fecha)

            appsUsadas.forEach { app ->
                coleccion.add(app)
            }

            _estado.value = "✅ Uso registrado correctamente"

            Handler(Looper.getMainLooper()).postDelayed({
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Sesión finalizada", Toast.LENGTH_SHORT).show()
                _cerrarApp.value = true
            }, 2000)
        }
    }
}
