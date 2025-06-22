package com.example.proyectogrado.domain.model

import com.google.firebase.firestore.DocumentId

data class Curso(
    @DocumentId
    var idCurso: String = "",

    val nombreCurso: String = "",
    val profesor: String = "",
    val estudiantes: List<String> = emptyList()
)