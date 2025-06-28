package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.viewmodel.VinculacionViewModel
import com.example.proyectogrado.domain.model.Estudiante
import com.example.proyectogrado.domain.model.AppUso // Asegúrate de importar AppUso
import com.google.firebase.firestore.FirebaseFirestore // Importar Firestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleEstudiante(
    estudianteId: String,
    vinculacionViewModel: VinculacionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Estado para la fecha seleccionada y la lista de uso de apps
    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var listaApps by remember { mutableStateOf<List<AppUso>>(emptyList()) }
    var mensajeErrorUso by remember { mutableStateOf<String?>(null) }

    // Observa el estudiante desde VinculacionViewModel
    val estudiante by vinculacionViewModel.estudianteSeleccionado.collectAsState()

    // Función para mapear nombres de paquete a nombres amigables
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

    LaunchedEffect(estudianteId) {
        // Carga los datos del estudiante usando la nueva función en VinculacionViewModel
        vinculacionViewModel.cargarEstudiantePorId(estudianteId)
        Log.d("DETALLE_ESTUDIANTE", "Cargando estudiante con ID: $estudianteId")
    }

    // Cargar datos de apps del estudiante seleccionado y la fecha
    LaunchedEffect(fechaSeleccionada, estudianteId) {
        if (estudianteId.isNotBlank()) {
            FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(estudianteId)
                .collection(fechaSeleccionada)
                .get()
                .addOnSuccessListener { result ->
                    val lista = result.mapNotNull { it.toObject(AppUso::class.java) }
                    listaApps = lista
                    mensajeErrorUso = null // Limpiar cualquier error previo
                    Log.d("DETALLE_ESTUDIANTE", "📦 Apps cargadas para $estudianteId el $fechaSeleccionada: ${lista.size}")
                }
                .addOnFailureListener { e ->
                    mensajeErrorUso = "❌ Error al cargar uso de apps: ${e.message}"
                    Log.e("DETALLE_ESTUDIANTE", "Error al cargar uso de apps", e)
                    listaApps = emptyList()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(estudiante?.nombre ?: "Cargando Estudiante...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            estudiante?.let { currentEstudiante ->
                Text(
                    text = "Detalles del Estudiante:",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Nombre: ${currentEstudiante.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "ID: ${currentEstudiante.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Vinculado por: ${currentEstudiante.vinculadoPor ?: "N/A"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Sección de Monitoreo de Uso de Aplicaciones
                Text(
                    text = "Monitoreo de Uso de Aplicaciones",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                )

                // Botón para seleccionar fecha
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

                if (mensajeErrorUso != null) {
                    Text(mensajeErrorUso!!, color = Color.Red)
                }

                if (listaApps.isEmpty() && mensajeErrorUso == null) {
                    Text("⚠️ No hay datos de uso para esta fecha.", color = Color.Gray)
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

            } ?: run {
                Text("Estudiante no encontrado o cargando...", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}