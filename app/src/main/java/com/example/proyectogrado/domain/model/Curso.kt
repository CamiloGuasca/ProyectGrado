package com.example.proyectogrado.domain.model

data class Curso(
    val nombreCurso: String = "",
    val profesor: String = "",
    val estudiantes: List<String> = emptyList()
)