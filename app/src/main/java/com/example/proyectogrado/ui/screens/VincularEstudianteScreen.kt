package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VincularEstudianteScreen(
    onBack: () -> Unit,
    viewModel: VinculacionViewModel = viewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Vincular Estudiante", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID del estudiante (único)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val padreUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (padreUid.isEmpty()) {
                Toast.makeText(context, "❌ Sesión no válida", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (nombre.isBlank() || id.isBlank()) {
                Toast.makeText(context, "⚠️ Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@Button
            }

            // Verificar que el estudiante exista
            db.collection("estudiantes").document(id).get()
                .addOnSuccessListener { documento ->
                    if (documento.exists()) {
                        // Verificar si ya está vinculado
                        db.collection("vinculaciones")
                            .document(padreUid)
                            .collection("estudiantes")
                            .document(id)
                            .get()
                            .addOnSuccessListener { vinculo ->
                                if (vinculo.exists()) {
                                    Toast.makeText(context, "⚠️ Este estudiante ya está vinculado", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Vincular estudiante
                                    viewModel.vincular(nombre, id, padreUid)
                                    Toast.makeText(context, "✅ Estudiante vinculado", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            }
                    } else {
                        Toast.makeText(context, "❌ El ID del estudiante no está registrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "❌ Error al verificar estudiante", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Vincular")
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

    }
}
