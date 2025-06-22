package com.example.proyectogrado.data.repository

import com.example.proyectogrado.domain.model.Usuario
import com.example.proyectogrado.domain.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepositoryImpl : UsuarioRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun registrarUsuario(usuario: Usuario): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(usuario.correo, usuario.password).await()
            firestore.collection("usuarios")
                .document(auth.currentUser!!.uid)
                .set(usuario.copy(password = ""))
                .await()
            true
        } catch (e: FirebaseAuthUserCollisionException) {
            println("⚠️ Correo ya registrado: ${e.message}")
            false
        } catch (e: Exception) {
            println("❌ Error al registrar usuario: ${e.message}")
            false
        }
    }


    override suspend fun login(correo: String, password: String): Usuario? {
        return try {
            val result = auth.signInWithEmailAndPassword(correo, password).await()
            val uid = result.user?.uid
            uid?.let {
                val doc = firestore.collection("usuarios").document(uid).get().await()
                doc.toObject(Usuario::class.java)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
