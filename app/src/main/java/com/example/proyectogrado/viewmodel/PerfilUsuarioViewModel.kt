package com.example.proyectogrado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilUsuarioViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid ?: ""

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre

    private val _usuario = MutableStateFlow("")
    val usuario: StateFlow<String> = _usuario

    private val _documento = MutableStateFlow("")
    val documento: StateFlow<String> = _documento

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _rol = MutableStateFlow("")
    val rol: StateFlow<String> = _rol

    private val _fechaNacimiento = MutableStateFlow("")
    val fechaNacimiento: StateFlow<String> = _fechaNacimiento

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje



    init {
        cargarDatosUsuario()
    }

    fun cargarDatosUsuario() {
        if (userId.isNotEmpty()) {
            firestore.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    _nombre.value = document.getString("nombre") ?: ""
                    _usuario.value = document.getString("usuario") ?: ""
                    _documento.value = document.getString("documento") ?: ""
                    _correo.value = document.getString("correo") ?: ""
                    _rol.value = document.getString("rol") ?: ""
                    _fechaNacimiento.value = document.getString("fechaNacimiento") ?: ""
                }
                .addOnFailureListener {
                    _mensaje.value = "Error al cargar datos: ${it.message}"
                }
        }
    }

    fun actualizarDatosUsuario(nuevoNombre: String, nuevoUsuario: String, nuevoDocumento: String) {
        viewModelScope.launch {
            if (userId.isNotEmpty()) {
                val datosActualizados = mapOf(
                    "nombre" to nuevoNombre,
                    "usuario" to nuevoUsuario,
                    "documento" to nuevoDocumento
                )
                firestore.collection("usuarios").document(userId)
                    .update(datosActualizados)
                    .addOnSuccessListener {
                        _mensaje.value = "Datos actualizados correctamente."
                        cargarDatosUsuario()
                    }
                    .addOnFailureListener {
                        _mensaje.value = "Error al actualizar: ${it.message}"
                    }
            }
        }
    }
}
