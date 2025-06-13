package com.example.proyectogrado.domain.model

data class Estudiante(
    val id: String = "",
    val nombre: String = "",
    val vinculadoPor: String = "" // UID del padre o docente
)
