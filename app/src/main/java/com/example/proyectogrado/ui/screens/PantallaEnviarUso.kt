package com.example.proyectogrado.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.domain.model.AppUso
import com.example.proyectogrado.services.ServicioUsoApps
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectogrado.utils.PreferenciasEstudiante


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun PantallaEnviarUso(onFinish: () -> Unit) {
    val context = LocalContext.current
    val servicio = remember { ServicioUsoApps(context) }
    val fecha = servicio.obtenerFechaActual()
    val firestore = FirebaseFirestore.getInstance()

    var resultado by remember { mutableStateOf("Analizando uso de apps...") }

    // Lógica para obtener y guardar datos automáticamente
    LaunchedEffect(Unit) {
        println("📲 Entrando a PantallaEnviarUso")

        val idEstudiante = PreferenciasEstudiante.obtenerId(context)
        println("🔍 ID del estudiante: $idEstudiante")

        if (idEstudiante.isBlank()) {
            resultado = "⚠️ No se ha configurado el ID del estudiante"
            return@LaunchedEffect
        }

        val appsUsadas = servicio.obtenerUsoDeHoy()

        if (appsUsadas.isEmpty()) {
            resultado = "No se detectó uso de apps hoy."
            return@LaunchedEffect
        }

        val coleccion = firestore.collection("usoApps")
            .document(idEstudiante)
            .collection(fecha)

        appsUsadas.forEach { app ->
            coleccion.add(app)
        }

        resultado = "✅ Uso registrado correctamente"
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Enviando uso de aplicaciones...", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(resultado)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFinish) {
            Text("Finalizar")
        }
    }
}
