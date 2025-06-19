package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectogrado.ui.navigation.Screen
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConfigurarEstudianteScreen(
    navController: NavController,
    onConfigurado: () -> Unit
) {
    var estudianteId by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Configura tu ID de estudiante", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estudianteId,
            onValueChange = { estudianteId = it },
            label = { Text("ID del estudiante") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (estudianteId.isNotBlank()) {
                    val db = FirebaseFirestore.getInstance()
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                    // Guardar localmente el ID del estudiante
                    PreferenciasEstudiante.guardarId(context, estudianteId)

                    db.collection("estudiantes")
                        .document(estudianteId)
                        .set(mapOf("id" to estudianteId, "vinculadoPor" to uid))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Estudiante configurado", Toast.LENGTH_SHORT).show()

                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(Screen.Inicio.route) {
                                popUpTo(Screen.Inicio.route) { inclusive = true }
                            }

                            onConfigurado()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "El ID no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
