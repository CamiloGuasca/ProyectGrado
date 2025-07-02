package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.util.Log // Asegúrate de tener esta importación
import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMonitoreoUso(
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val TAG_PANTALLA_MONITOREO = "MonitoreoUsoScreen" // Definir un TAG para esta pantalla

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

    LaunchedEffect(Unit) {
        val padreUid = FirebaseAuth.getInstance().currentUser?.uid
        if (padreUid != null) {
            FirebaseFirestore.getInstance()
                .collection("estudiantes")
                .whereEqualTo("vinculadoPor", padreUid)
                .get()
                .addOnSuccessListener { docs ->
                    if (!docs.isEmpty) {
                        // APLICA .trim() AQUÍ para cada ID obtenido de Firebase
                        val ids = docs.mapNotNull { it.getString("id")?.trim() }
                        estudiantesVinculados = ids
                        estudianteSeleccionado = ids.firstOrNull()
                        Log.d(TAG_PANTALLA_MONITOREO, "Estudiantes vinculados cargados (trimed): $estudiantesVinculados. Seleccionado: '$estudianteSeleccionado'")
                    } else {
                        mensajeError = "❌ No hay estudiantes vinculados."
                        Log.d(TAG_PANTALLA_MONITOREO, "No hay estudiantes vinculados para padreUid: $padreUid")
                    }
                }
                .addOnFailureListener { e ->
                    mensajeError = "❌ Error al obtener estudiantes vinculados."
                    Log.e(TAG_PANTALLA_MONITOREO, "Error al obtener estudiantes vinculados: ${e.message}", e)
                }
        } else {
            Log.d(TAG_PANTALLA_MONITOREO, "Padre UID es nulo, no se pueden cargar estudiantes vinculados.")
        }
    }

    LaunchedEffect(fechaSeleccionada, estudianteSeleccionado) {
        if (!estudianteSeleccionado.isNullOrBlank()) {
            // Aquí ya estudianteSeleccionado debería estar limpio si se cargó del LaunchedEffect(Unit)
            Log.d(TAG_PANTALLA_MONITOREO, "Llamando a cargarDatos con Estudiante: '${estudianteSeleccionado}', Fecha: '${fechaSeleccionada}'")
            FirebaseFirestore.getInstance()
                .collection("usoApps")
                .document(estudianteSeleccionado!!) // Usa el estudianteSeleccionado ya trimed
                .collection(fechaSeleccionada)
                .get()
                .addOnSuccessListener { result ->
                    listaApps = result.mapNotNull { it.toObject(AppUso::class.java) }
                    if (listaApps.isEmpty()) {
                        Log.d(TAG_PANTALLA_MONITOREO, "No hay datos de uso para esta fecha y estudiante.")
                    }
                }
                .addOnFailureListener { e ->
                    mensajeError = "❌ Error al cargar uso de apps: ${e.message}"
                    Log.e(TAG_PANTALLA_MONITOREO, "Error al cargar uso de apps: ${e.message}", e)
                }
        } else {
            Log.d(TAG_PANTALLA_MONITOREO, "No se puede cargar datos: Estudiante seleccionado es nulo o vacío.")
            listaApps = emptyList() // Limpiar la lista si no hay estudiante válido
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF1))
            .padding(16.dp)
    ) {
        Text(
            "📊 Monitoreo de Uso de Aplicaciones",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF263238)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (estudiantesVinculados.size > 1) {
            var expanded by remember { mutableStateOf(false) }

            Text("👦 Selecciona el estudiante:", color = Color(0xFF37474F))
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
                ) {
                    Text(estudianteSeleccionado ?: "Seleccionar", color = Color.White)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    estudiantesVinculados.forEach { id ->
                        DropdownMenuItem(
                            text = { Text(id) },
                            onClick = {
                                // APLICA .trim() AQUÍ también para IDs seleccionados del menú, por si acaso
                                estudianteSeleccionado = id.trim()
                                expanded = false
                                Log.d(TAG_PANTALLA_MONITOREO, "Estudiante seleccionado por Dropdown (trimed): '$estudianteSeleccionado'")
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else if (estudiantesVinculados.size == 1) {
            Text("Estudiante vinculado: ${estudianteSeleccionado ?: "Cargando..."}", color = Color(0xFF37474F))
            Spacer(modifier = Modifier.height(16.dp))
        }


        Button(
            onClick = {
                // Usa el calendario actual para el DatePickerDialog para que se abra en la fecha ya seleccionada
                // o en el día actual si no se ha seleccionado nada.
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val fecha = Calendar.getInstance().apply {
                            set(year, month, day)
                        }
                        fechaSeleccionada = dateFormat.format(fecha.time)
                        // Log para verificar el formato de la fecha
                        Log.d(TAG_PANTALLA_MONITOREO, "Fecha seleccionada del DatePicker: '$fechaSeleccionada'")
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
        ) {
            Text("📅 Filtrar por fecha", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("📆 Fecha seleccionada: $fechaSeleccionada", fontSize = 14.sp, color = Color(0xFF546E7A))
        Spacer(modifier = Modifier.height(16.dp))

        mensajeError?.let {
            Text(it, color = Color.Red)
        }

        // Mensaje más claro si no hay datos
        if (listaApps.isEmpty() && mensajeError == null && !estudianteSeleccionado.isNullOrBlank()) {
            Text("⚠️ No hay datos de uso para esta fecha y estudiante. Verifique su selección o los datos en Firebase.", color = Color.Gray)
        }


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(listaApps) { app ->
                val nombreBonito = mapearNombre(app.nombre)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFCFD8DC))
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
                            tint = Color(0xFF37474F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = nombreBonito, fontWeight = FontWeight.SemiBold, color = Color(0xFF263238))
                            Text(text = "Tiempo usado: ${app.tiempoMin} min", fontSize = 12.sp, color = Color(0xFF546E7A))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A))
        ) {
            Text("🔙 Volver", color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
            Text("🔒 Cerrar sesión", color = Color.White)
        }
    }
}