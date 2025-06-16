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
import androidx.navigation.NavController
import com.example.proyectogrado.domain.model.AppUso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PantallaMonitoreoUso(
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var listaApps by remember { mutableStateOf<List<AppUso>>(emptyList()) }
    var estudiantesVinculados by remember { mutableStateOf<List<String>>(emptyList()) }
    var estudianteSeleccionado by remember { mutableStateOf<String?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun mapearNombre(nombrePaquete: String): String {
        val mapa = mapOf(
            "com.google.android.youtube" to "YouTube",
            "com.whatsapp" to "WhatsApp",
            "com.instagram.android" to "Instagram",
            "com.google.android.gm" to "Gmail",
            "com.android.chrome" to "Chrome",
            "com.tiktok.android" to "TikTok",
            "com.clarocolombia.miclaro" to "Claro",
            "com.example.proyectogrado" to "Proyecto de Grado"
        )
        return mapa[nombrePaquete] ?: nombrePaquete.substringAfterLast('.').replaceFirstChar { it.uppercase() }
    }

    // 🔍 Cargar estudiantes vinculados al padre
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
                        val ids = docs.mapNotNull { it.getString("id") }
                        estudiantesVinculados = ids
                        estudianteSeleccionado = ids.firstOrNull()
                        Log.d("DEBUG", "👦 Estudiantes vinculados: $ids")
                    } else {
                        mensajeError = "❌ No hay estudiantes vinculados."
                    }
                }
                .addOnFailureListener {
                    mensajeError = "❌ Error al obtener estudiantes vinculados."
                }
        }
    }

    // 🔁 Cargar datos de apps del estudiante seleccionado
    LaunchedEffect(fechaSeleccionada, estudianteSeleccionado) {
        if (!estudianteSeleccionado.isNullOrBlank()) {
            FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(estudianteSeleccionado!!)
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

        // 🔽 Menú de selección de estudiante
        if (estudiantesVinculados.size > 1) {
            var expanded by remember { mutableStateOf(false) }

            Text("Selecciona el estudiante:")
            Box {
                Button(onClick = { expanded = true }) {
                    Text(estudianteSeleccionado ?: "Seleccionar")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    estudiantesVinculados.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) },
                            onClick = {
                                estudianteSeleccionado = id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 🗓️ Botón para seleccionar fecha
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
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(8.dp))

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
