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

class EnviarUsoViewModel : ViewModel() {

    private val _resultado = MutableStateFlow("Analizando uso de apps...")
    val resultado: StateFlow<String> = _resultado

    private val _finalizar = MutableStateFlow(false)
    val finalizar: StateFlow<Boolean> = _finalizar

    fun enviarUso(context: Context) {
        viewModelScope.launch {
            val servicio = ServicioUsoApps(context)
            val fecha = servicio.obtenerFechaActual()
            val idEstudiante = PreferenciasEstudiante.obtenerId(context)

            if (idEstudiante.isBlank()) {
                _resultado.value = "⚠️ No se ha configurado el ID del estudiante"
                return@launch
            }

            val appsUsadas = servicio.obtenerUsoDeHoy()
            if (appsUsadas.isEmpty()) {
                _resultado.value = "No se detectó uso de apps hoy."
                return@launch
            }

            val coleccion = FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(idEstudiante)
                .collection(fecha)

            appsUsadas.forEach { app ->
                coleccion.add(app)
            }

            _resultado.value = "✅ Uso registrado correctamente"

            Handler(Looper.getMainLooper()).postDelayed({
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Sesión finalizada", Toast.LENGTH_SHORT).show()
                (context as? Activity)?.finishAffinity()
                _finalizar.value = true
            }, 2000)
        }
    }
}
