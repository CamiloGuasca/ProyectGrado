package com.example.proyectogrado.data.repository

import android.util.Log
import com.example.proyectogrado.domain.model.Estudiante
import com.google.firebase.firestore.FieldPath
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
    fun obtenerEstudiantes(onResult: (List<Estudiante>) -> Unit) {
        Log.d("EstudianteRepo", "Intentando obtener todos los estudiantes de la colección 'estudiantes'...")
        db.collection("estudiantes") // <-- ¡VERIFICA ESTE NOMBRE DE COLECCIÓN EXACTAMENTE!
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.w("EstudianteRepo", "La colección 'estudiantes' está vacía o no existe en Firestore.")
                }
                val estudiantes = documents.mapNotNull {
                    try {
                        val estudiante = it.toObject(Estudiante::class.java)
                        Log.d("EstudianteRepo", "Mapeado estudiante: ${estudiante?.nombre} (ID: ${it.id})")
                        estudiante?.copy(id = it.id) // Asegúrate de asignar el ID del documento
                    } catch (e: Exception) {
                        Log.e("EstudianteRepo", "Error al mapear documento a Estudiante: ${e.message}", e)
                        null
                    }
                }
                Log.d("EstudianteRepo", "Total de estudiantes obtenidos del Firestore: ${estudiantes.size}")
                onResult(estudiantes)
            }
            .addOnFailureListener { e ->
                Log.e("EstudianteRepo", "Error al obtener estudiantes de Firestore: ${e.message}", e)
                onResult(emptyList()) // Devuelve una lista vacía en caso de fallo
            }
    }
    fun obtenerEstudiantesPorIds(listaIdsEstudiantes: List<String>, onResult: (List<Estudiante>) -> Unit) {
        if (listaIdsEstudiantes.isEmpty()) {
            Log.d("EstudianteRepo", "Lista de IDs de estudiantes vacía para obtener objetos Estudiante.")
            onResult(emptyList())
            return
        }

        val chunks = listaIdsEstudiantes.chunked(10) // Dividir la lista en sublistas de max 10 IDs
        val allEstudiantes = mutableListOf<Estudiante>()
        var completedQueries = 0

        chunks.forEach { chunk ->
            db.collection("estudiantes") // ¡VERIFICA ESTE NOMBRE DE COLECCIÓN EXACTAMENTE EN FIRESTORE!
                .whereIn(FieldPath.documentId(), chunk) // Busca documentos por sus IDs
                .get()
                .addOnSuccessListener { documents ->
                    val estudiantesChunk = documents.mapNotNull { document ->
                        // Convierte el documento a un objeto Estudiante, y asegura que el ID del documento
                        // de Firestore se asigne al campo 'id' de tu objeto Estudiante.
                        document.toObject(Estudiante::class.java)?.copy(id = document.id)
                    }
                    allEstudiantes.addAll(estudiantesChunk)
                    Log.d("EstudianteRepo", "Chunk procesado. Estudiantes encontrados en este chunk: ${estudiantesChunk.size}")

                    completedQueries++
                    if (completedQueries == chunks.size) {
                        Log.d("EstudianteRepo", "Todos los chunks de estudiantes procesados. Total de objetos Estudiante obtenidos: ${allEstudiantes.size}")
                        onResult(allEstudiantes) // Llama al callback con la lista completa de Estudiantes
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EstudianteRepo", "Error al obtener objetos Estudiante por IDs: ${e.message}", e)
                    // Si falla un chunk, podemos decidir cómo manejarlo.
                    // En este caso, si algún chunk falla, devolvemos una lista vacía para evitar inconsistencias.
                    if (completedQueries < chunks.size) { // Evita llamar a onResult múltiples veces
                        onResult(emptyList())
                    }
                    completedQueries = chunks.size // Asegura que no se espere más
                }
        }
    }
}
