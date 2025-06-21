    package com.example.proyectogrado.domain.model

    data class Usuario(
        val documento: String = "",
        val nombre: String = "",
        val correo: String = "",
        val fechaNacimiento: String = "",
        val usuario: String = "",
        val password: String = "",
        val rol: String = ""
    )