package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VincularEstudianteScreen(
    onBack: () -> Unit,
    viewModel: VinculacionViewModel = viewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF102027)) // Fondo azul oscuro
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F)) // Gris azulado oscuro
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Vincular Estudiante",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Vincular Estudiante",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del estudiante", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID único del estudiante", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0288D1),
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val padreUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (padreUid.isEmpty()) {
                            Toast.makeText(context, "❌ Sesión no válida", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (nombre.isBlank() || id.isBlank()) {
                            Toast.makeText(context, "⚠️ Completa todos los campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        db.collection("estudiantes").document(id).get()
                            .addOnSuccessListener { documento ->
                                if (documento.exists()) {
                                    db.collection("vinculaciones")
                                        .document(padreUid)
                                        .collection("estudiantes")
                                        .document(id)
                                        .get()
                                        .addOnSuccessListener { vinculo ->
                                            if (vinculo.exists()) {
                                                Toast.makeText(context, "⚠️ Este estudiante ya está vinculado", Toast.LENGTH_SHORT).show()
                                            } else {
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
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text("Vincular", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFB0BEC5),
                        contentColor = Color.Black
                    )
                ) {
                    Text("⬅ Volver", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
