package com.example.proyectogrado.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import com.example.proyectogrado.domain.model.AppUso
import com.example.proyectogrado.viewmodel.MonitoreoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaMonitoreoUso(
    onLogout: () -> Unit,
    viewModel: MonitoreoViewModel = viewModel()
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var fechaSeleccionada by remember { mutableStateOf(dateFormat.format(calendar.time)) }

    var nombreApp by remember { mutableStateOf("") }
    var tiempoApp by remember { mutableStateOf("") }

    // Cargar datos desde Firebase cuando cambia la fecha
    LaunchedEffect(fechaSeleccionada) {
        viewModel.cargarDatos(fechaSeleccionada)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Monitoreo de Uso de Aplicaciones", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Botón de filtro por fecha
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

        // FORMULARIO: nombre + tiempo
        OutlinedTextField(
            value = nombreApp,
            onValueChange = { nombreApp = it },
            label = { Text("Nombre de la aplicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tiempoApp,
            onValueChange = { tiempoApp = it },
            label = { Text("Tiempo en minutos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val tiempo = tiempoApp.toIntOrNull()
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null && nombreApp.isNotBlank() && tiempo != null) {
                    val app = AppUso(nombre = nombreApp, tiempoMin = tiempo)
                    FirebaseFirestore.getInstance()
                        .collection("usoApps")
                        .document(userId)
                        .collection(fechaSeleccionada)
                        .add(app)
                        .addOnSuccessListener {
                            Toast.makeText(context, "✅ Guardado exitosamente", Toast.LENGTH_SHORT).show()
                            nombreApp = ""
                            tiempoApp = ""
                            viewModel.cargarDatos(fechaSeleccionada)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "❌ Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "⚠️ Ingresa los datos correctamente", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar aplicación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LISTADO de apps
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.listaApps) { app ->
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
                            Text(text = app.nombre, fontWeight = FontWeight.Medium)
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
