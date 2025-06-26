// app/src/main/java/com/example/proyectogrado/ui/screens/PantallaConfigurarHorario.kt
package com.example.proyectogrado.ui.screens

import android.widget.Toast
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

    // Para la lista de aplicaciones disponibles
    val aplicacionesDisponiblesConNombres = remember {
        listOf(
            Pair("com.whatsapp", "WhatsApp"),
            Pair("com.facebook.katana", "Facebook"),
            Pair("com.google.android.youtube", "YouTube"),
            Pair("com.instagram.android", "Instagram"),
            Pair("com.tiktok.android", "TikTok"),
            Pair("com.twitter.android", "X (Twitter)"),
            Pair("com.snapchat.android", "Snapchat"),
            Pair("com.netflix.mediaclient", "Netflix"),
            Pair("com.rovio.angrybirds", "Angry Birds (Ejemplo)"),
            Pair("com.supercell.clashofclans", "Clash of Clans (Ejemplo)")
        )
    }

    LaunchedEffect(horarioId) {
        if (horarioId != null) {
            viewModel.cargarHorarioParaEdicion(idEstudiante, horarioId)
        } else {
            viewModel.limpiarHorarioEnEdicion()
        }
    }

    LaunchedEffect(horarioEnEdicion) {
        horarioEnEdicion?.let { horario ->
            nombreHorario = horario.nombreHorario
            horaInicio = horario.horaInicio
            horaFin = horario.horaFin
            tiempoMaximo = horario.tiempoMaximoMinutos.toString()
            aplicacionesSeleccionadas.clear()
            aplicacionesSeleccionadas.addAll(horario.aplicacionesRestringidas)
        } ?: run {
            if (horarioId == null) {
                nombreHorario = ""
                horaInicio = ""
                horaFin = ""
                tiempoMaximo = "0"
                aplicacionesSeleccionadas.clear()
            }
        }
    }

    val scrollState = rememberScrollState() // <--- ESTO ES NUEVO: Estado del scroll

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (horarioId == null) "Crear Nuevo Horario" else "Editar Horario") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { // Usa onVolver para el botón de retroceso
                        Icon(Icons.Filled.ArrowBack, "Volver")
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
                .verticalScroll(scrollState), // <--- ESTO ES NUEVO: Habilita el scroll
            verticalArrangement = Arrangement.spacedBy(12.dp) // Mantén el espaciado entre elementos
        ) {
            Text(
                text = if (horarioId == null) "Crear Nuevo Horario para ${idEstudiante.take(8)}..."
                else "Editar Horario para ${idEstudiante.take(8)}...",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nombreHorario,
                onValueChange = { nombreHorario = it },
                label = { Text("Nombre del Horario (ej: Juegos Tarde)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = horaInicio,
                onValueChange = { newValue ->
                    horaInicio = newValue
                },
                label = { Text("Hora de inicio (ej: 08:00)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                value = horaFin,
                onValueChange = { newValue ->
                    horaFin = newValue
                },
                label = { Text("Hora de fin (ej: 21:00)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            OutlinedTextField(
                value = tiempoMaximo,
                onValueChange = { newValue ->
                    tiempoMaximo = newValue.filter { it.isDigit() }
                },
                label = { Text("Tiempo máximo de uso (minutos, 0 para bloqueo total)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Selecciona aplicaciones a restringir en este horario:", style = MaterialTheme.typography.titleMedium)

            aplicacionesDisponiblesConNombres.forEach { (packageName, appName) ->
                val isChecked = aplicacionesSeleccionadas.contains(packageName)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isChecked) aplicacionesSeleccionadas.remove(packageName)
                            else aplicacionesSeleccionadas.add(packageName)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isSelected ->
                            if (isSelected) aplicacionesSeleccionadas.add(packageName)
                            else aplicacionesSeleccionadas.remove(packageName)
                        }
                    )
                    Text(text = appName, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombreHorario.isBlank() || horaInicio.isBlank() || horaFin.isBlank() || tiempoMaximo.isBlank()) {
                        Toast.makeText(context, "⚠️ Completa todos los campos del horario", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val horaRegex = Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$") // Regex para HH:MM (00:00 a 23:59)
                    if (!horaInicio.matches(horaRegex)) {
                        Toast.makeText(context, "⚠️ Formato de 'Hora de inicio' incorrecto (ej: 08:00)", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (!horaFin.matches(horaRegex)) {
                        Toast.makeText(context, "⚠️ Formato de 'Hora de fin' incorrecto (ej: 21:00)", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val maxTiempoInt = tiempoMaximo.toIntOrNull()
                    if (maxTiempoInt == null || maxTiempoInt < 0) {
                        Toast.makeText(context, "⚠️ Tiempo máximo debe ser un número válido y no negativo", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (aplicacionesSeleccionadas.isEmpty()) {
                        Toast.makeText(context, "⚠️ Selecciona al menos una aplicación para restringir en este horario.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    val horario = HorarioUso(
                        id = horarioId ?: UUID.randomUUID().toString(),
                        nombreHorario = nombreHorario,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        tiempoMaximoMinutos = maxTiempoInt,
                        aplicacionesRestringidas = aplicacionesSeleccionadas.toList()
                    )

                    viewModel.guardarHorario(idEstudiante, horario) { exito ->
                        if (exito) {
                            Toast.makeText(context, "✅ Horario '${nombreHorario}' guardado", Toast.LENGTH_SHORT).show()
                            onVolver()
                        } else {
                            Toast.makeText(context, "❌ Error al guardar el horario '${nombreHorario}'", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (horarioId == null) "Crear Nuevo Horario" else "Guardar Cambios")
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}