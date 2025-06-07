package com.example.proyectogrado.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.proyectogrado.data.local.AppDatabase
import com.example.proyectogrado.data.local.UsuarioEntity
import com.example.proyectogrado.data.repository.UsuarioRepositoryImpl
import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.domain.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository

    init {
        val db = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "usuarios_db"
        ).build()
        repository = UsuarioRepositoryImpl(db.usuarioDao())
    }

    fun registrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.registrarUsuario(usuario)
        }
    }

    fun login(usuario: String, password: String, onResult: (Usuario?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(usuario, password)
            onResult(result)
        }
    }
}


