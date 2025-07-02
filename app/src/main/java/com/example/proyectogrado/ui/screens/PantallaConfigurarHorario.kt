// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaConfigurarHorario.kt
package com.example.proyectogrado.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogrado.domain.model.HorarioUso
import com.example.proyectogrado.viewmodel.HorarioUsoViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.UUID

// Importaciones adicionales para el scroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Necesario para el TopAppBar si lo usas
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfigurarHorario(
    idEstudiante: String,
    horarioId: String?,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HorarioUsoViewModel = viewModel()

    var nombreHorario by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var tiempoMaximo by remember { mutableStateOf("0") }
    val aplicacionesSeleccionadas = remember { mutableStateListOf<String>() }

    val horarioEnEdicion by viewModel.horarioEnEdicion.collectAsState()
    val scrollState = rememberScrollState()

    val aplicacionesDisponiblesConNombres = remember {
        listOf(
            "com.whatsapp" to "WhatsApp",
            "com.facebook.katana" to "Facebook",
            "com.google.android.youtube" to "YouTube",
            "com.instagram.android" to "Instagram",
            "com.tiktok.android" to "TikTok",
            "com.twitter.android" to "X (Twitter)",
            "com.snapchat.android" to "Snapchat",
            "com.netflix.mediaclient" to "Netflix",
            "com.rovio.angrybirds" to "Angry Birds",
            "com.supercell.clashofclans" to "Clash of Clans"
        )
    }

    LaunchedEffect(horarioId) {
        if (horarioId != null) viewModel.cargarHorarioParaEdicion(idEstudiante, horarioId)
        else viewModel.limpiarHorarioEnEdicion()
    }

    LaunchedEffect(horarioEnEdicion) {
        horarioEnEdicion?.let {
            nombreHorario = it.nombreHorario
            horaInicio = it.horaInicio
            horaFin = it.horaFin
            tiempoMaximo = it.tiempoMaximoMinutos.toString()
            aplicacionesSeleccionadas.clear()
            aplicacionesSeleccionadas.addAll(it.aplicacionesRestringidas)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (horarioId == null) "Crear Nuevo Horario" else "Editar Horario",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D47A1)
                )

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F4F8))
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (horarioId == null) "Configuración para ${idEstudiante.take(8)}..." else "Edición de horario",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF0D47A1)
            )

            OutlinedTextField(
                value = nombreHorario,
                onValueChange = { nombreHorario = it },
                label = { Text("Nombre del Horario") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = horaInicio,
                onValueChange = { horaInicio = it },
                label = { Text("Hora de inicio (ej: 08:00)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                value = horaFin,
                onValueChange = { horaFin = it },
                label = { Text("Hora de fin (ej: 21:00)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                value = tiempoMaximo,
                onValueChange = { tiempoMaximo = it.filter { it.isDigit() } },
                label = { Text("Tiempo máximo (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text("Aplicaciones restringidas:", style = MaterialTheme.typography.titleMedium)

            aplicacionesDisponiblesConNombres.forEach { (packageName, appName) ->
                val checked = aplicacionesSeleccionadas.contains(packageName)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (checked) aplicacionesSeleccionadas.remove(packageName)
                            else aplicacionesSeleccionadas.add(packageName)
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            if (it) aplicacionesSeleccionadas.add(packageName)
                            else aplicacionesSeleccionadas.remove(packageName)
                        }
                    )
                    Text(appName)
                }
            }

            Button(
                onClick = {
                    if (nombreHorario.isBlank() || horaInicio.isBlank() || horaFin.isBlank() || tiempoMaximo.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val horaRegex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
                    if (!horaInicio.matches(horaRegex) || !horaFin.matches(horaRegex)) {
                        Toast.makeText(context, "Formato de hora inválido (usa HH:mm)", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val tiempo = tiempoMaximo.toIntOrNull() ?: return@Button

                    if (aplicacionesSeleccionadas.isEmpty()) {
                        Toast.makeText(context, "Selecciona al menos una app", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val horario = HorarioUso(
                        id = horarioId ?: UUID.randomUUID().toString(),
                        nombreHorario = nombreHorario,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        tiempoMaximoMinutos = tiempo,
                        aplicacionesRestringidas = aplicacionesSeleccionadas.toList()
                    )

                    viewModel.guardarHorario(idEstudiante, horario) {
                        if (it) {
                            Toast.makeText(context, "Horario guardado", Toast.LENGTH_SHORT).show()
                            onVolver()
                        } else {
                            Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) {
                Text(if (horarioId == null) "Crear Horario" else "Guardar Cambios", color = Color.White)
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
