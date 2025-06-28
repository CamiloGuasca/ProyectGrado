package com.example.proyectogrado.data.repository

import com.example.proyectogrado.domain.model.Estudiante
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import android.util.Log

// Repositorio para operaciones relacionadas con la entidad Estudiante en Firestore.
// Este archivo fusiona tus funciones y las de tu compañero.
class EstudianteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "EstudianteRepository"

    // --- Funciones de tu compañero (vincularEstudiante, obtenerEstudiantesPorPadre) ---

    /**
     * Vincula un estudiante en Firestore.
     * Usado por la lógica del padre (compañero).
     */
    fun vincularEstudiante(estudiante: Estudiante, onResult: (Boolean) -> Unit) {
        db.collection("estudiantes")
            .document(estudiante.id)
            .set(estudiante)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Obtiene la lista de estudiantes vinculados a un padre específico.
     * Usado por la lógica del padre (compañero).
     */
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

    // --- Tu función (getHorariosUso) ---

    /**
     * Obtiene los horarios de uso de un estudiante desde Firestore.
     * Usado por tu lógica de bloqueo de apps (AppBlockerAccessibilityService).
     * @param estudianteId El ID del estudiante.
     * @return Un QuerySnapshot que contiene los documentos de los horarios.
     */
    suspend fun getHorariosUso(estudianteId: String): QuerySnapshot {
        return db.collection("estudiantes")
            .document(estudianteId)
            .collection("horariosUso")
            .get()
            .await()
    }

    /**
     * Obtiene una lista de objetos Estudiante completos dado una lista de sus IDs.
     * Usado por CursoViewModel para mostrar los nombres de los estudiantes en la UI.
     * @param estudianteIds La lista de IDs de estudiantes.
     * @param onResult Una función lambda que recibe la lista de Estudiante.
     */
    fun obtenerEstudiantesPorIds(estudianteIds: List<String>, onResult: (List<Estudiante>) -> Unit) {
        if (estudianteIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("estudiantes")
            .whereIn("id", estudianteIds)
            .get()
            .addOnSuccessListener { result ->
                val estudiantes = result.mapNotNull { document ->
                    document.toObject(Estudiante::class.java)
                }
                Log.d(TAG, "Estudiantes obtenidos por IDs: ${estudiantes.size} encontrados.")
                onResult(estudiantes)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener estudiantes por IDs: ${e.message}", e)
                onResult(emptyList())
            }
    }

    // Función global obtenerEstudiantes, si es necesaria para otros ViewModels.
    fun obtenerEstudiantes(onResult: (List<Estudiante>) -> Unit) {
        db.collection("estudiantes")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(Estudiante::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Obtiene un solo objeto Estudiante por su ID.
     * @param estudianteId El ID del estudiante a buscar.
     * @param onResult Una función lambda que recibe el objeto Estudiante? (null si no se encuentra o hay un error).
     */
    fun obtenerEstudiantePorId(estudianteId: String, onResult: (Estudiante?) -> Unit) {
        db.collection("estudiantes")
            .document(estudianteId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val estudiante = documentSnapshot.toObject(Estudiante::class.java)
                    Log.d(TAG, "Estudiante con ID $estudianteId encontrado: ${estudiante?.nombre}")
                    onResult(estudiante)
                } else {
                    Log.d(TAG, "Estudiante con ID $estudianteId no encontrado.")
                    onResult(null) // No se encontró el documento
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al obtener estudiante por ID $estudianteId: ${e.message}", e)
                onResult(null) // Error al obtener el documento
            }
    }
}