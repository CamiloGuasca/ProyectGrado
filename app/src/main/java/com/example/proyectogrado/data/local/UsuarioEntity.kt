package com.example.proyectogrado.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val documento: String,
    val nombre: String,
    val correo: String,
    val fechaNacimiento: String,
    val usuario: String,
    val password: String,
    val rol: String
)
