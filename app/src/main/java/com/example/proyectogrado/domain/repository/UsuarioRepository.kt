package com.example.proyectogrado.domain.repository

import com.example.proyectogrado.data.local.UsuarioDao
import com.example.proyectogrado.data.local.UsuarioEntity
import com.example.proyectogrado.domain.model.Usuario

interface UsuarioRepository {
    suspend fun registrarUsuario(usuario: Usuario): Boolean
    suspend fun login(correo: String, password: String): Usuario?
}
