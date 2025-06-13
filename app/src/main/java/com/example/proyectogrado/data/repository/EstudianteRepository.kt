package com.example.proyectogrado.data.repository

import com.example.proyectogrado.domain.model.Estudiante
import com.google.firebase.firestore.FirebaseFirestore

class EstudianteRepository {

    private val db = FirebaseFirestore.getInstance()

    fun vincularEstudiante(estudiante: Estudiante, onResult: (Boolean) -> Unit) {
        db.collection("estudiantes")
            .document(estudiante.id)
            .set(estudiante)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun obtenerEstudiantesPorPadre(uid: String, onResult: (List<Estudiante>) -> Unit) {
        db.collection("estudiantes")
            .whereEqualTo("vinculadoPor", uid)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(Estudiante::class.java) }
                onResult(lista)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }
}
