// app/src/main/java/com/example/proyectogrado/domain/model/HorarioUso.kt
package com.example.proyectogrado.domain.model

data class HorarioUso(
    val id: String = "", // ID único del horario (ej. UUID)
    val nombreHorario: String = "",
    val horaInicio: String = "", // Formato "HH:MM"
    val horaFin: String = "",   // Formato "HH:MM"
    val tiempoMaximoMinutos: Int = 0, // 0 = bloqueo total, >0 = minutos permitidos
    val aplicacionesRestringidas: List<String> = emptyList() // Lista de package names de apps
)