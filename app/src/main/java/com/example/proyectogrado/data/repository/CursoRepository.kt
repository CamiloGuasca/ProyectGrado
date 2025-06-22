package com.example.proyectogrado.data.repository

import android.util.Log
import com.example.proyectogrado.domain.model.Curso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CursoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun CrearCurso(curso: Curso, onResult: (Boolean) -> Unit){
        userId?.let {
            db.collection("Cursos")
                .add(curso)
        }
    }
    fun obtenerCursosPorProfesor(profesorId: String, searchTerm: String, onResult: (List<Curso>) -> Unit) {
        if (profesorId.isBlank()) {
            Log.e("CursoRepo", "profesorId está vacío. No se pueden obtener cursos.")
            onResult(emptyList())
            return
        }

        var query = db.collection("Cursos").whereEqualTo("profesor", profesorId)

        if (searchTerm.isNotBlank()) {
            val lowerCaseSearchTerm = searchTerm.lowercase()
            // Firestore no permite búsquedas "contains" directas en subcadenas o "like %query%"
            // Para un buscador que funcione bien, necesitarías indexación de texto completo (ej. Algolia, ElasticSearch)
            // o un enfoque más complejo de consulta.
            // Para una búsqueda simple por "empieza con" o coincidencia exacta (si el nombre es el término de búsqueda),
            // podríamos intentar:
            query = query.orderBy("nombreCurso") // Necesario para range queries
                .startAt(lowerCaseSearchTerm)
                .endAt(lowerCaseSearchTerm + "\uf8ff") // \uf8ff es un carácter Unicode muy alto

            // Si necesitas buscar en cualquier parte del nombre, es mejor filtrar en el cliente
            // después de obtener una lista más amplia, o usar soluciones de terceros.
            Log.w("CursoRepo", "La búsqueda avanzada por searchTerm '$searchTerm' es limitada en Firestore. Se obtendrán y filtrarán localmente si es compleja.")
        }

        query.get()
            .addOnSuccessListener { documents ->
                val cursos = documents.mapNotNull { it.toObject(Curso::class.java) }
                val filteredCursos = if (searchTerm.isNotBlank()) {
                    val lowerCaseSearchTerm = searchTerm.lowercase()
                    cursos.filter {
                        it.nombreCurso.lowercase().contains(lowerCaseSearchTerm) // Filtramos en el cliente para "contains"
                    }
                } else {
                    cursos
                }
                onResult(filteredCursos)
            }
            .addOnFailureListener { e ->
                Log.e("CursoRepo", "Error al obtener cursos por profesor: $e")
                onResult(emptyList())
            }
    }
}