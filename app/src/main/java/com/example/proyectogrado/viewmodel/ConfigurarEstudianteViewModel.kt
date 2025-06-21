package com.example.proyectogrado.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.services.EnvioUsoScheduler
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConfigurarEstudianteViewModel : ViewModel() {

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _exito = MutableStateFlow(false)
    val exito: StateFlow<Boolean> = _exito

    fun guardarEstudiante(context: Context, estudianteId: String) {
        if (estudianteId.isBlank()) {
            _mensaje.value = "⚠️ El ID no puede estar vacío"
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val db = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            db.collection("estudiantes")
                .document(estudianteId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _mensaje.value = "⚠️ Este ID ya está en uso"
                    } else {
                        // El ID no existe, podemos guardarlo
                        db.collection("estudiantes")
                            .document(estudianteId)
                            .set(mapOf("id" to estudianteId, "vinculadoPor" to uid))
                            .addOnSuccessListener {
                                PreferenciasEstudiante.guardarId(context, estudianteId)
                                EnvioUsoScheduler.programarEnvioDiario(context) // ✅ Aquí se programa el envío diario
                                _mensaje.value = "✅ Estudiante configurado correctamente"
                                _exito.value = true
                                FirebaseAuth.getInstance().signOut()
                            }
                            .addOnFailureListener {
                                _mensaje.value = "❌ Error al guardar"
                            }
                    }
                }
                .addOnFailureListener {
                    _mensaje.value = "❌ Error al verificar el ID"
                }
        }
    }
}
