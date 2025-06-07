package com.example.proyectogrado.data.local

import androidx.room.*

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND password = :password")
    suspend fun login(usuario: String, password: String): UsuarioEntity?
}


