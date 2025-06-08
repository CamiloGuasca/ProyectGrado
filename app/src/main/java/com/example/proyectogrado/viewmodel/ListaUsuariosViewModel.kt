package com.example.proyectogrado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.domain.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListaUsuariosViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    fun cargarUsuarios() {
        viewModelScope.launch {
            firestore.collection("usuarios")
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.mapNotNull { it.toObject(Usuario::class.java) }
                    _usuarios.value = lista
                }
                .addOnFailureListener {
                    _usuarios.value = emptyList()
                }
        }
    }
}
