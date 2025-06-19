package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    var rolUsuario by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(true) }

    // Validación de rol al iniciar la pantalla
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            onLogout()
            return@LaunchedEffect
        }

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
                cargando = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al validar rol", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (rolUsuario == "padre") {
        Column(Modifier.padding(16.dp)) {
            Text("Bienvenido padre", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onVincular, modifier = Modifier.fillMaxWidth()) {
                Text("Vincular estudiante")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onMonitorear, modifier = Modifier.fillMaxWidth()) {
                Text("Monitorear uso de apps")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}
