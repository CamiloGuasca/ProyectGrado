package com.example.proyectogrado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogrado.data.repository.UsuarioRepositoryImpl
import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.domain.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val repository: UsuarioRepository = UsuarioRepositoryImpl()

    fun registrarUsuario(usuario: Usuario, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.registrarUsuario(usuario)
            onResult(success)
        }
    }

    fun login(correo: String, password: String, onResult: (Usuario?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(correo, password)
            onResult(result)
        }
    }
}
