package com.example.proyectogrado.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun registrarUsuario(
        correo: String,
        contrasena: String,
        nombre: String,
        rol: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: return@addOnSuccessListener
                val datosUsuario = mapOf(
                    "nombre" to nombre,
                    "correo" to correo,
                    "rol" to rol
                )

                firestore.collection("usuarios")
                    .document(uid)
                    .set(datosUsuario)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        Log.e("Registro", "Error al guardar datos", it)
                        onError("Error al guardar datos: ${it.message}")
                    }
            }
            .addOnFailureListener {
                Log.e("Registro", "Error de autenticación", it)
                onError("Error de autenticación: ${it.message}")
            }
    }
}
