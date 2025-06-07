package com.example.proyectogrado.data.repository

import com.example.proyectogrado.data.local.UsuarioDao
import com.example.proyectogrado.data.local.UsuarioEntity
import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.domain.repository.UsuarioRepository

class UsuarioRepositoryImpl(private val dao: UsuarioDao) : UsuarioRepository {
    override suspend fun registrarUsuario(usuario: Usuario) {
        val entity = UsuarioEntity(
            documento = usuario.documento,
            nombre = usuario.nombre,
            correo = usuario.correo,
            fechaNacimiento = usuario.fechaNacimiento,
            usuario = usuario.usuario,
            password = usuario.password,
            rol = usuario.rol
        )
        dao.insertar(entity)
    }

    override suspend fun login(usuario: String, password: String): Usuario? {
        val entity = dao.login(usuario, password)
        return entity?.let {
            Usuario(
                documento = it.documento,
                nombre = it.nombre,
                correo = it.correo,
                fechaNacimiento = it.fechaNacimiento,
                usuario = it.usuario,
                password = it.password,
                rol = it.rol
            )
        }
    }
}


