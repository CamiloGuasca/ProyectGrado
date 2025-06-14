package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectogrado.domain.model.AppUso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaMonitoreoUso(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var listaApps by remember { mutableStateOf<List<AppUso>>(emptyList()) }
    var estudianteId by remember { mutableStateOf<String?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun mapearNombre(nombrePaquete: String): String {
        return when (nombrePaquete) {
            "com.google.android.youtube" -> "YouTube"
            "com.whatsapp" -> "WhatsApp"
            "com.instagram.android" -> "Instagram"
            "com.google.android.gm" -> "Gmail"
            "com.android.chrome" -> "Chrome"
            "com.tiktok.android" -> "TikTok"
            else -> nombrePaquete
        }
    }

    // 🔍 Obtener estudiante vinculado al padre
    LaunchedEffect(Unit) {
        val padreUid = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("DEBUG", "🔑 Padre actual: $padreUid")
        if (padreUid != null) {
            FirebaseFirestore.getInstance()
                .collection("estudiantes")
                .whereEqualTo("vinculadoPor", padreUid)
                .get()
                .addOnSuccessListener { docs ->
                    if (!docs.isEmpty) {
                        estudianteId = docs.documents.first().getString("id")
                        Log.d("DEBUG", "👦 Estudiante vinculado: $estudianteId")
                    } else {
                        mensajeError = "❌ No hay estudiante vinculado."
                    }
                }
                .addOnFailureListener {
                    mensajeError = "❌ Error al obtener estudiante vinculado."
                }
        }
    }

    // 🔁 Cargar apps cada vez que cambie fecha o estudiante
    LaunchedEffect(fechaSeleccionada, estudianteId) {
        if (!estudianteId.isNullOrBlank()) {
            FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(estudianteId!!)
                .collection(fechaSeleccionada)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.mapNotNull { it.toObject(AppUso::class.java) }
                    listaApps = lista
                    Log.d("DEBUG", "📦 Apps cargadas: ${lista.size}")
                }
                .addOnFailureListener {
                    mensajeError = "❌ Error al cargar uso de apps"
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Monitoreo de Uso de Aplicaciones", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val fecha = Calendar.getInstance().apply {
                        set(year, month, day)
                    }
                    fechaSeleccionada = dateFormat.format(fecha.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Filtrar por fecha")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Fecha seleccionada: $fechaSeleccionada", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (mensajeError != null) {
            Text(mensajeError!!, color = Color.Red)
        }

        if (listaApps.isEmpty() && mensajeError == null) {
            Text("⚠️ No hay datos de uso para esta fecha", color = Color.Gray)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(listaApps) { app ->
                val nombreBonito = mapearNombre(app.nombre)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = nombreBonito, fontWeight = FontWeight.Medium)
                            Text(text = "Tiempo usado: ${app.tiempoMin} min", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Cerrar sesión", color = Color.White)
        }
    }
}
