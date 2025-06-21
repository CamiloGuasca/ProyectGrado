package com.example.proyectogrado.ui.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PadreMenuViewModel : ViewModel() {

    private val _rolValido = MutableStateFlow(false)
    val rolValido: StateFlow<Boolean> = _rolValido

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun validarRol(context: Context, onLogout: () -> Unit) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val rol = doc.getString("rol")
                    if (rol == "padre") {
                        _rolValido.value = true
                    } else {
                        _mensaje.value = "Acceso denegado"
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }
                }
                .addOnFailureListener {
                    _mensaje.value = "Error al validar rol"
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                }
        }
    }
}
