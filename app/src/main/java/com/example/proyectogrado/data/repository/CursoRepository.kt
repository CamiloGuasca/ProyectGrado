package com.example.proyectogrado.data.repository

import android.util.Log
import com.example.proyectogrado.domain.model.Curso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CursoRepository {
    private val db = FirebaseFirestore.getInstance()

    fun CrearCurso(curso: Curso, onResult: (Boolean, String?) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        currentUserId?.let { uid ->
            db.collection("Cursos")
                .add(curso.copy(profesor = uid))
                .addOnSuccessListener { documentReference ->
                    Log.d("CursoRepo", "Curso '${curso.nombreCurso}' agregado con ID: ${documentReference.id}")
                    onResult(true, documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.e("CursoRepo", "Error al agregar curso '${curso.nombreCurso}': $e")
                    onResult(false, null)
                }
        } ?: run {
            Log.e("CursoRepo", "Usuario no logueado al intentar crear curso.")
            onResult(false, null)
        }
    }

    fun obtenerCursosPorProfesor(profesorId: String, searchTerm: String, onResult: (List<Curso>) -> Unit) {
        if (profesorId.isBlank()) {
            Log.e("CursoRepo", "profesorId está vacío. No se pueden obtener cursos.")
            onResult(emptyList())
            return
        }

        db.collection("Cursos")
            .whereEqualTo("profesor", profesorId)
            .get()
            .addOnSuccessListener { documents ->
                val cursos = documents.mapNotNull {
                    try {
                        val curso = it.toObject(Curso::class.java)
                        // Asegúrate de que tu Curso data class tenga @DocumentId para que idCurso se rellene
                        Log.d("CursoRepo", "Curso mapeado: $curso (ID: ${curso.idCurso})")
                        curso
                    } catch (e: Exception) {
                        Log.e("CursoRepo", "Error al mapear documento a Curso: ${e.message}", e)
                        null
                    }
                }
                val filteredCursos = if (searchTerm.isNotBlank()) {
                    val lowerCaseSearchTerm = searchTerm.lowercase()
                    cursos.filter {
                        it.nombreCurso.lowercase().contains(lowerCaseSearchTerm)
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

    fun obtenerCursoPorId(cursoId: String, onResult: (Curso?) -> Unit) {
        db.collection("Cursos")
            .document(cursoId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val curso = documentSnapshot.toObject(Curso::class.java)
                    onResult(curso)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CursoRepo", "Error al obtener curso por ID: $e")
                onResult(null)
            }
    }

    // --- NUEVAS FUNCIONES PARA MODIFICAR CURSOS ---

    fun eliminarCurso(cursoId: String, onResult: (Boolean) -> Unit) {
        db.collection("Cursos")
            .document(cursoId)
            .delete()
            .addOnSuccessListener {
                Log.d("CursoRepo", "Curso con ID: $cursoId eliminado con éxito.")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("CursoRepo", "Error al eliminar curso con ID: $cursoId. Error: $e")
                onResult(false)
            }
    }

    fun actualizarNombreCurso(cursoId: String, nuevoNombre: String, onResult: (Boolean) -> Unit) {
        db.collection("Cursos")
            .document(cursoId)
            .update("nombreCurso", nuevoNombre) // Asegúrate que "NombreCurso" sea el nombre del campo en Firestore
            .addOnSuccessListener {
                Log.d("CursoRepo", "Nombre del curso con ID: $cursoId actualizado a '$nuevoNombre' con éxito.")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("CursoRepo", "Error al actualizar nombre del curso con ID: $cursoId. Error: $e")
                onResult(false)
            }
    }

    fun agregarEstudianteACurso(cursoId: String, nuevoEstudianteNombre: String, onResult: (Boolean) -> Unit) {
        db.collection("Cursos")
            .document(cursoId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val curso = documentSnapshot.toObject(Curso::class.java)
                    val estudiantesActuales = curso?.estudiantes?.toMutableList() ?: mutableListOf()

                    if (estudiantesActuales.contains(nuevoEstudianteNombre)) {
                        Log.w("CursoRepo", "Estudiante '$nuevoEstudianteNombre' ya existe en el curso $cursoId.")
                        onResult(true) // Considerar como éxito si ya existe
                        return@addOnSuccessListener
                    }

                    estudiantesActuales.add(nuevoEstudianteNombre)
                    db.collection("Cursos")
                        .document(cursoId)
                        .update("estudiantes", estudiantesActuales) // Asegúrate que "Estudiantes" sea el nombre del campo en Firestore
                        .addOnSuccessListener {
                            Log.d("CursoRepo", "Estudiante '$nuevoEstudianteNombre' agregado al curso $cursoId con éxito.")
                            onResult(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("CursoRepo", "Error al agregar estudiante al curso $cursoId. Error: $e")
                            onResult(false)
                        }
                } else {
                    Log.e("CursoRepo", "No se encontró el curso con ID: $cursoId para agregar estudiante.")
                    onResult(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CursoRepo", "Error al obtener curso para agregar estudiante. Error: $e")
                onResult(false)
            }
    }
}