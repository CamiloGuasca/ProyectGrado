package com.example.proyectogrado.ui.screens

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectogrado.services.ServicioUsoApps
import com.example.proyectogrado.utils.PreferenciasEstudiante
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@Composable
fun PantallaEnviarUso() {
    val context = LocalContext.current
    val servicio = remember { ServicioUsoApps(context) }
    val fecha = servicio.obtenerFechaActual()
    val firestore = FirebaseFirestore.getInstance()

    var resultado by remember { mutableStateOf("Analizando uso de apps...") }
    var enviado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val idEstudiante = PreferenciasEstudiante.obtenerId(context)

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
        enviado = true

        // Espera 2 segundos y cierra sesión + app
        Handler(Looper.getMainLooper()).postDelayed({
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Sesión finalizada", Toast.LENGTH_SHORT).show()
            (context as? Activity)?.finishAffinity()
        }, 2000)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enviando uso de aplicaciones...", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(resultado)

        if (enviado) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}
