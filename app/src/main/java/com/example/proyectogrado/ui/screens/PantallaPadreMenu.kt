package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaPadreMenu(
    onVincular: () -> Unit,
    onMonitorear: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // ✅ Importante: para usar `by` con `remember { mutableStateOf(...) }`
    var rolUsuario by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                rolUsuario = doc.getString("rol")
                if (rolUsuario != "padre") {
                    Toast.makeText(context, "Acceso denegado", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al validar rol", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }
    }

    // Solo muestra el menú si el usuario es padre
    if (rolUsuario == "padre") {
        Column(Modifier.padding(16.dp)) {
            Text("Bienvenido padre", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVincular) {
                Text("Vincular estudiante")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onMonitorear) {
                Text("Monitorear uso de apps")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout) {
                Text("Cerrar sesión")
            }
        }
    }
}
