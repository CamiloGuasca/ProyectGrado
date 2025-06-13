package com.example.proyectogrado.data.repository

import com.example.proyectogrado.domain.model.AppUso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsoAppRepository {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun guardarAppUso(fecha: String, app: AppUso) {
        userId?.let {
            db.collection("usoApps")
                .document(it)
                .collection(fecha)
                .add(app)
        }
    }

    fun obtenerAppsPorFecha(fecha: String, onResult: (List<AppUso>) -> Unit) {
        if (userId == null) {
            onResult(emptyList())
            return
        }

        db.collection("usoApps")
            .document(userId)
            .collection(fecha)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject(AppUso::class.java) }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
