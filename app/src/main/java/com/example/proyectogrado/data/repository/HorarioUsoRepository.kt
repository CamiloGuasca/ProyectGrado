// app/src/main/java/com/example/proyectogrado/data/repository/HorarioUsoRepository.kt
package com.example.proyectogrado.data.repository

import android.util.Log
import com.example.proyectogrado.domain.model.HorarioUso
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await // Para usar await en tareas de Firestore

class HorarioUsoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val TAG = "HorarioUsoRepository"
    private val COLLECTION_NAME = "horariosUso"

    // Obtener un horario por su ID
    suspend fun getHorarioPorId(idEstudiante: String, horarioId: String): HorarioUso? {
        return try {
            val document = db.collection("estudiantes")
                .document(idEstudiante)
                .collection(COLLECTION_NAME)
                .document(horarioId)
                .get()
                .await()
            document.toObject(HorarioUso::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo horario con ID $horarioId para estudiante $idEstudiante: ${e.message}")
            null
        }
    }

    // Guardar o actualizar un horario
    suspend fun guardarHorario(idEstudiante: String, horario: HorarioUso): Boolean {
        return try {
            db.collection("estudiantes")
                .document(idEstudiante)
                .collection(COLLECTION_NAME)
                .document(horario.id) // Usamos el ID del horario como ID del documento
                .set(horario)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error guardando horario ${horario.id} para estudiante $idEstudiante: ${e.message}")
            false
        }
    }

    // Eliminar un horario
    suspend fun eliminarHorario(idEstudiante: String, horarioId: String): Boolean {
        return try {
            db.collection("estudiantes")
                .document(idEstudiante)
                .collection(COLLECTION_NAME)
                .document(horarioId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando horario $horarioId para estudiante $idEstudiante: ${e.message}")
            false
        }
    }

    // Obtener todos los horarios en tiempo real para un estudiante
    fun getTodosLosHorariosRealtime(idEstudiante: String, onUpdate: (List<HorarioUso>) -> Unit): ListenerRegistration {
        return db.collection("estudiantes")
            .document(idEstudiante)
            .collection(COLLECTION_NAME)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Error escuchando horarios para estudiante $idEstudiante: ${e.message}", e)
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val horarios = snapshots?.documents?.mapNotNull { doc ->
                    doc.toObject(HorarioUso::class.java)?.copy(id = doc.id) // Asegúrate de asignar el ID del documento al objeto
                } ?: emptyList()
                onUpdate(horarios)
            }
    }
}